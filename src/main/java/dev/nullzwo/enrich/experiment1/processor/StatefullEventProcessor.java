package dev.nullzwo.enrich.experiment1.processor;

public record StatefullEventProcessor<S>(S initial,
										 StatefullEventReaction<S> processing) implements EventProcessor<S> {
}
