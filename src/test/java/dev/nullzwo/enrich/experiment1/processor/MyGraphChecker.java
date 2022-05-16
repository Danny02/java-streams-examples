package dev.nullzwo.enrich.experiment1.processor;

import java.util.List;

import static dev.nullzwo.enrich.experiment1.processor.AttrGraph.attribute;
import static dev.nullzwo.enrich.experiment1.processor.FixGraph.cata;

public class MyGraphChecker implements GraphChecker {

	@Override
	public boolean isAcyclicGraph(FixGraph graph) {
//		cata(graph, attribute(g -> childrenProduceMainEvent(new FixGraph(g))))
//		return cata(graph, g -> g.results().stream().reduce())
		return false;
	}

	private boolean childrenProduceMainEvent(FixGraph graph) {
		List<EventInfo> allProduced = cata(graph, g -> g.results().stream().flatMap(List::stream).toList());
		return allProduced.contains(graph.unfix().triggering());
	}

	List<EventInfo> produces(FixGraph graph) {
		return graph.unfix().results().stream().flatMap(s -> produces(s).stream()).toList();
	}
}
