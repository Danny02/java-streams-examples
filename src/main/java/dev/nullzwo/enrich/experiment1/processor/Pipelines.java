package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.*;
import dev.nullzwo.enrich.experiment1.algebras.KafkaStreamsAlg;
import dev.nullzwo.enrich.experiment1.algebras.StreamAlg;
import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Target;
import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Workflow;
import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Pipeline;
import dev.nullzwo.enrich.experiment1.algebras.StreamAlg.Pipeline.Transf.Entry;

import java.util.List;

import static dev.nullzwo.enrich.experiment1.processor.Processors.*;
import static dev.nullzwo.enrich.experiment1.Streams.VEHICLES_TOPIC;
import static java.util.stream.Collectors.toList;

public class Pipelines {

	private static Pipeline<Country, Integer> countVehiclesPerCountry(Pipeline<VehicleId, VehicleEvent> vehicles) {

		return vehicles.<VehicleId, Country, Country, Integer>transform((store, key, value) -> {
					if (value instanceof BaseDateChanged bdc) {
						var last = store.get(key);
						var next = bdc.country();
						if (!next.equals(last)) {
							store.set(key, next);
							return List.of(new Entry<>(last, -1), new Entry<>(next, 1));
						}
					}
					return List.of();
				}, null, null)
				.foldByKey(0, (v, c) -> v + c, null);
	}

	public static <P> P buildStreems(StreamAlg<P> alg) {
		Pipeline<VehicleId, VehicleEvent> vehicles = alg.from(VEHICLES_TOPIC, null, null);

		return alg.compile(
				new Workflow<>(createEvents(resetPrice, vehicles), new Target<>(VEHICLES_TOPIC, null, null)),
				new Workflow<>(createState(eventsPerCar, vehicles), new Target<>("events-per-car", null, null)),
				new Workflow<>(countVehiclesPerCountry(vehicles), new Target<>("country-counts", null, null)),
				new Workflow<>(createEvents(detectDealerChanges, vehicles), new Target<>(VEHICLES_TOPIC, null, null))
		);
	}

	static <S> Pipeline<VehicleId, VehicleEvent> createEvents(EventProcessor<S> processor, Pipeline<VehicleId, VehicleEvent> vehicles) {
		return switch (processor) {
			case StatefullEventProcessor<S> p -> vehicles.<VehicleId, S, VehicleId, VehicleEvent>transform((store, key, value) -> {
				var current = store.get(key);
				var result = p.processing().process(current, value);
				store.set(key, result.state());
				return result.events().stream().map(e -> new Entry<>(key, e)).collect(toList());
			}, null, null);
			case StatelessEventProcessor p -> vehicles.flatMap(p::process);
			default -> throw new IllegalStateException();
		};
	}

	static <S> Pipeline<VehicleId, S> createState(EventProcessor<S> processor, Pipeline<VehicleId, VehicleEvent> vehicles) {
		return switch (processor) {
			case StatefullEventProcessor<S> p -> vehicles.<VehicleId, S, VehicleId, S>transform((store, key, value) -> {
				var current = store.get(key);
				var result = p.processing().process(current, value);
				store.set(key, result.state());
				return List.of(new Entry<>(key, result.state()));
			}, null, null);
			case StatelessEventProcessor p -> throw new IllegalArgumentException();
			default -> throw new IllegalStateException();
		};
	}

	public static void main(String[] args) {
		var t = buildStreems(new KafkaStreamsAlg());
		System.out.println(t.describe());
	}
}
