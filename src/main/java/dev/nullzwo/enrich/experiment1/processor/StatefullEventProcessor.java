package dev.nullzwo.enrich.experiment1.processor;

public record StatefullEventProcessor<S, E>(String name, S initialState, EventReaction<S, E> reaction) implements EventProcessor<S, E> {
	@Override
	public Result<S, E> process(S state, E event) {
		return reaction.process(state, event);
	}

	interface EventReaction<S, E> {
		Result<S, E> process(S state, E event);
	}
}
