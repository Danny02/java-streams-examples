package dev.nullzwo.enrich.handsonsession;

import dev.nullzwo.enrich.AbstractExperiment;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.util.List;

public class HansOn extends AbstractExperiment {

	@Override
	public List<ProducerRecord<Object, Object>> exampleData() {
		return List.of();
	}

	@Override
	public Topology createTopology() {
		var builder = new StreamsBuilder();

		// create event processors

		return builder.build();
	}

	public static void main(String[] args) {
		new HansOn().run();
	}
}
