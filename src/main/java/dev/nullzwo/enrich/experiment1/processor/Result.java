package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.Domain.VehicleEvent;

import java.util.List;

public record Result<S>(S state, List<VehicleEvent> events) {
}
