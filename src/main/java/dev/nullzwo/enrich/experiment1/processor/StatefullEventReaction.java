package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;

public interface StatefullEventReaction<S> {
	Result<S> process(S state, VehicleEvent event);
}
