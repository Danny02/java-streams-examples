package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.processor.FixGraph.Algebra;

import static dev.nullzwo.enrich.experiment1.processor.FixGraph.*;

public record AttrGraph<A>(A head, EventGraph<AttrGraph<A>> tail) {

	public static <A> Algebra<AttrGraph<A>> attribute(Algebra<A> alg) {
		return graph -> new AttrGraph<>(alg.apply(map(graph, attr -> attr.head)), graph);
	}

	public static FixGraph toFix(AttrGraph<?> graph) {
		return ana(graph, attr -> attr.tail);
	}
}
