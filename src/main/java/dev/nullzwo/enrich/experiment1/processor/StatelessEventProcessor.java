package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;

import java.util.List;

public non-sealed interface StatelessEventProcessor<S, E> extends EventProcessor<S, E> {
	List<E> process(E event);

	@Override
	default Result<S, E> process(S state, E event) {
		return new Result<>(state, process(event));
	}

	@Override
	default S initialState() {
		return null;
	}
}
