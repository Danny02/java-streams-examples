package dev.nullzwo.enrich.experiment1;

import dev.nullzwo.enrich.AbstractExperiment;
import dev.nullzwo.enrich.experiment1.Domain.BaseDateChanged;
import dev.nullzwo.enrich.experiment1.Domain.DealerId;
import dev.nullzwo.enrich.experiment1.Domain.VehicleId;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.Topology;

import java.util.List;
import java.util.UUID;

import static dev.nullzwo.enrich.experiment1.Domain.Country.CHINA;
import static dev.nullzwo.enrich.experiment1.Streams.VEHICLES_TOPIC;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;

public class Experiment1 extends AbstractExperiment {

	@Override
	public List<ProducerRecord<Object, Object>> exampleData() {
		return List.of(
				new ProducerRecord(VEHICLES_TOPIC,
						new VehicleId(randomUUID()),
						new BaseDateChanged("vin-213-asd-23", new DealerId(UUID.randomUUID()), CHINA)
				),
				new ProducerRecord(VEHICLES_TOPIC,
						new VehicleId(randomUUID()),
						new BaseDateChanged("vin-213-asd-23", new DealerId(UUID.randomUUID()), CHINA)
				)
		);
	}

	@Override
	public List<String> topics() {
		return Streams.TOPICS;
	}

	@Override
	public Topology createTopology() {
		return Streams.all();
	}

	public static void main(String[] args) throws InterruptedException {
		new Experiment1().run();
    }
}
