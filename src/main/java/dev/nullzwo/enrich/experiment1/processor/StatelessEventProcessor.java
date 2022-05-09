package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;

import java.util.List;

public interface StatelessEventProcessor extends EventProcessor<Void> {
	List<VehicleEvent> process(VehicleEvent event);
}
