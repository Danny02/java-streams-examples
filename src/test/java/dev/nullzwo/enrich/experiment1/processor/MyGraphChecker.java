package dev.nullzwo.enrich.experiment1.processor;

import java.util.List;

public class MyGraphChecker implements GraphChecker {

	@Override
	public boolean isAcyclicGraph(FixGraph graph) {
		var isNot = graph.unfix().results().stream().anyMatch(g -> !isAcyclicGraph(g));
		if(isNot) {
			return false;
		}
		return !produces(graph).contains(graph.unfix().triggering());
	}

	List<EventInfo> produces(FixGraph graph) {
		return graph.unfix().results().stream().flatMap(s -> produces(s).stream()).toList();
	}
}
