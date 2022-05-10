package dev.nullzwo.enrich.handsonsession;

import dev.nullzwo.enrich.AbstractExperiment;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.util.List;

public class HansOn extends AbstractExperiment {

	public static final String VEHICLES_TOPIC = "vehicles";

	interface VehicleEvent {
	}

	public record DealerChanged(String dealerId) implements VehicleEvent {
	}

	public record PriceReset() implements VehicleEvent {
	}

	public record PriceChanged(long newPrice) implements VehicleEvent {
	}

	@Override
	public List<String> topics() {
		return List.of(VEHICLES_TOPIC, "price-readmodel");
	}

	@Override
	public List<ProducerRecord<Object, Object>> exampleData() {
		return List.of(
				new ProducerRecord<>(VEHICLES_TOPIC, "vin-mycar23", new DealerChanged("my-dealer-id-3432")),
				new ProducerRecord<>(VEHICLES_TOPIC, "vin-mycar23", new PriceChanged(420)),
				new ProducerRecord<>(VEHICLES_TOPIC, "vin-yourcar64", new DealerChanged("my-dealer-id-3432")),
				new ProducerRecord<>(VEHICLES_TOPIC, "vin-yourcar64", new PriceChanged(689)),
				new ProducerRecord<>(VEHICLES_TOPIC, "vin-yourcar64", new PriceReset())
		);
	}

	@Override
	public Topology createTopology() {
		var builder = new StreamsBuilder();

		builder.<String, VehicleEvent>stream(VEHICLES_TOPIC)
				.groupByKey()
				.aggregate(() -> 0L, (vin, event, currentPrice) -> switch (event) {
					case PriceChanged pc -> pc.newPrice;
					case PriceReset pr -> 0L;
					default -> currentPrice;
				}).toStream().to("price-readmodel");

		// create event processors

		return builder.build();
	}

	public static void main(String[] args) {
		new HansOn().run();
	}
}
