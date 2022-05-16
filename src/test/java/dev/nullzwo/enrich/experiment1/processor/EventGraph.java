package dev.nullzwo.enrich.experiment1.processor;

import java.util.List;

public record EventGraph<A>(EventInfo triggering, List<A> results) {
}
