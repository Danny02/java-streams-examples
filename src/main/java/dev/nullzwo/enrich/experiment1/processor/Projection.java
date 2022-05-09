package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain;

public interface Projection<S> {
	S process(S state, Domain.VehicleEvent event);
}
