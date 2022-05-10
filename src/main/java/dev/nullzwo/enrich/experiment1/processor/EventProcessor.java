package dev.nullzwo.enrich.experiment1.processor;

import java.util.List;
import java.util.function.Function;

public sealed interface EventProcessor<S, E> permits StatefullEventProcessor, StatelessEventProcessor {

	record Result<S, E>(S state, List<E> events){}

	Result<S, E> process(S state, E event);

	S initialState();

	String name();

	@FunctionalInterface
	interface Projection<S, E> {
		S process(S state, E event);
	}

	static <S, E> EventProcessor<S, E> projection(String name, S initial, Projection<S, E> projection) {
		return new StatefullEventProcessor<>("projection-" + name, initial, (s, e) -> new Result<>(projection.process(s, e), List.of()));
	}

	static <E, A extends E> EventProcessor<Void, E> process(String name, Class<A> eventType, Function<A, E> transform) {
		return process(name, (E e) -> eventType.isInstance(e) ? List.of(transform.apply((A)e)) : List.of());
	}

	@FunctionalInterface
	interface StatelessReaction<E> {
		List<E> process(E event);
	}

	static <E> EventProcessor<Void, E> process(String name, StatelessReaction<E> reaction) {
		return new StatelessEventProcessor<>() {
			@Override
			public List<E> process(E event) {
				return reaction.process(event);
			}

			@Override
			public String name() {
				return name;
			}
		};
	}
}
