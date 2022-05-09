package dev.nullzwo.enrich;

import dev.nullzwo.enrich.experiment1.Domain;
import dev.nullzwo.enrich.experiment1.Streams;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.nullzwo.enrich.experiment1.Domain.Country.CHINA;
import static dev.nullzwo.enrich.experiment1.Streams.VEHICLES_TOPIC;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.kafka.streams.KafkaStreams.State.RUNNING;

public abstract class AbstractExperiment {

	public static final String KAFKA_IMAGE = "confluentinc/cp-kafka:6.2.1";

	public List<String> topics() {
		return List.of();
	}

	private KafkaContainer startKafka() {
		var kafka = new KafkaContainer(DockerImageName.parse(KAFKA_IMAGE));
		kafka.start();
		// kafka.followOutput(frame => println(frame.getUtf8String))
		var admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()));
		admin.createTopics(topics().stream().map(n -> new NewTopic(n, 5, (short) 1)).collect(Collectors.toList()));
		Runtime.getRuntime().addShutdownHook(new Thread(kafka::close));
		return kafka;
	}

	public Topology createTopology() {
		return null;
	}

	private void startStreams(KafkaContainer kafka) {
		var topology = createTopology();
		if(topology == null) {
			return;
		}

		var config = new Properties();
		config.putAll(Map.of(
				StreamsConfig.APPLICATION_ID_CONFIG, "java-streams",
				StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once_v2",
				StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, JsonSerde.class.getName(),
				StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, JsonSerde.class.getName(),
				StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "0",
				StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()
		));

		var streams = new KafkaStreams(topology, config);
		var startLatch = new CountDownLatch(1);
		streams.setStateListener((newState, oldState) -> {
			if ((newState == RUNNING) && (oldState != RUNNING)) startLatch.countDown();
		});
		streams.start();
		try {
			if (!startLatch.await(60, SECONDS))
				throw new RuntimeException("Streams never finished rebalancing on startup");
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
	}

	public List<ProducerRecord<Object, Object>> exampleData() {
		return List.of();
	}

	private void createExampleData(KafkaProducer<Object, Object> producer) {
		exampleData().stream().map(producer::send).forEach(f -> {
			try {
				f.get();
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException(e);
			}
		});

		System.out.println("### produced example data");
	}

	static void consumeMessages(KafkaConsumer<String, String> consumer) {
		consumer.subscribe(Pattern.compile("^(?!java-streams).*"));
		System.out.println("### subsribed to all topics");
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
			System.out.println("### fetched " + records.count() + " messages");
			for (ConsumerRecord<String, String> record : records) {
				System.out.println(format("%s: %s -> %s",record.topic(),record.key(),record.value()));
			}
		}
	}

	public void run() {
		var kafka = startKafka();
		startStreams(kafka);

		var jsonSerde = JsonSerde.INST;
		var config = Map.<String, Object>of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
		var producer = new KafkaProducer<>(config, jsonSerde.serializer(), jsonSerde.serializer());

		createExampleData(producer);

		var cconfig = Map.<String, Object>of(
				ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
				ConsumerConfig.GROUP_ID_CONFIG, "foobar",
				ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
		);
		var consumer = new KafkaConsumer<>(cconfig, Serdes.String().deserializer(), Serdes.String().deserializer());

		consumeMessages(consumer);
	}
}
