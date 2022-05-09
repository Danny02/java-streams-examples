package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;

import java.util.List;
import java.util.function.Function;

public interface EventProcessor<S> {

	static <S> EventProcessor<S> projection(S initial, Projection<S> projection) {
		return new StatefullEventProcessor<>(initial, (s, e) -> new Result<>(projection.process(s, e), List.of()));
	}

	static <A> EventProcessor<Void> process(Class<A> eventType, Function<A, VehicleEvent> transform) {
		return process(e -> eventType.isInstance(e) ? List.of(transform.apply((A)e)) : List.of());
	}
	static EventProcessor<Void> process(StatelessEventProcessor processor) {
		return processor;
	}
}
