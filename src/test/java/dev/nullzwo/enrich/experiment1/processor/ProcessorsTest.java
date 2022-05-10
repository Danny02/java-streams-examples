package dev.nullzwo.enrich.experiment1.processor;


import dev.nullzwo.enrich.experiment1.Domain.BaseDateChanged;
import dev.nullzwo.enrich.experiment1.Domain.DealerChanged;
import dev.nullzwo.enrich.experiment1.Domain.DealerId;
import dev.nullzwo.enrich.experiment1.Domain.PriceChanged;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static dev.nullzwo.enrich.experiment1.Domain.Country.CHINA;
import static dev.nullzwo.enrich.experiment1.processor.Processors.*;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

public class ProcessorsTest {

	@Test
	void resetPriceOnDealerChange() {
		var processor = runInMemory(resetPrice);
		var outcome = processor.process(new DealerChanged(new DealerId(randomUUID()), new DealerId(randomUUID())));
		assertThat(outcome).containsOnly(new PriceChanged(0));
	}

	@Test
	void noResetPriceOnBaseDataChanged() {
		var processor = runInMemory(resetPrice);
		var outcome = processor.process(new BaseDateChanged("vin", new DealerId(randomUUID()), CHINA));
		assertThat(outcome).isEmpty();
	}

	@Test
	void noResetPriceOnPriceChanged() {
		var processor = runInMemory(resetPrice);
		var outcome = processor.process(new PriceChanged(3));
		assertThat(outcome).isEmpty();
	}

	@Test
	void emitDealerChanged() {
		var processor = runInMemory(detectDealerChanges);
		DealerId old = new DealerId(randomUUID());
		DealerId next = new DealerId(randomUUID());
		var outcome = List.of(
				new BaseDateChanged("vin", old, CHINA),
				new BaseDateChanged("vin", next, CHINA)
		).stream().flatMap(e -> processor.process(e).stream()).collect(Collectors.toList());
		assertThat(outcome).containsExactly(new DealerChanged(null, old), new DealerChanged(old, next));
	}

	@Test
	void shouldCountEvents() {
		var processor = runInMemory(eventsPerCar);
		List.of(
				new BaseDateChanged("vin", new DealerId(randomUUID()), CHINA),
				new PriceChanged(0),
				new DealerChanged(new DealerId(randomUUID()), new DealerId(randomUUID()))
		).forEach(processor::process);

		assertThat(processor.state).isEqualTo(3);
	}

	@AfterAll
	static void printMappings() {
		for (String processor : eventMapping.keySet()) {
			System.out.println(processor + ":");
			Map<String, Set<String>> pmappings = eventMapping.get(processor);
			for (String incoming : pmappings.keySet()) {
				Set<String> outcomes = pmappings.get(incoming);
				for (String outgoing : outcomes) {
					System.out.println("\t" + incoming + " -> " + outgoing);
				}

				if(outcomes.isEmpty()) {
					System.out.println("\t" + incoming + " -> ...");
				}
			}
		}
	}

	static Map<String, Map<String, Set<String>>> eventMapping = new HashMap<>();

	static void addMapping(String processor, Object initialEvent, List<?> outcomeEvents) {
		var mapping = eventMapping.get(processor);
		if (mapping == null) {
			mapping = new HashMap<>();
			eventMapping.put(processor, mapping);
		}

		var key = initialEvent.getClass().getSimpleName();
		var outcomes = mapping.get(key);
		if (outcomes == null) {
			outcomes = new HashSet<>();
			mapping.put(key, outcomes);
		}

		var out = outcomes;
		outcomeEvents.forEach(e -> out.add(e.getClass().getSimpleName()));
	}

	static <S, E> InmemoryEventProcessor<S, E> runInMemory(EventProcessor<S, E> processor) {
		return new InmemoryEventProcessor(processor);
	}

	public static class InmemoryEventProcessor<S, E> {
		boolean initialized = false;
		S state = null;

		final EventProcessor<S, E> processor;

		public InmemoryEventProcessor(EventProcessor<S, E> processor) {
			this.processor = processor;
		}

		List<E> process(E event) {
			if (!initialized) {
				state = processor.initialState();
				initialized = true;
			}
			var result = processor.process(state, event);
			if(!result.events().isEmpty() || !Objects.equals(result.state(), state)) {
				addMapping(processor.name(), event, result.events());
			}

			state = result.state();
			return result.events();
		}
	}
}
