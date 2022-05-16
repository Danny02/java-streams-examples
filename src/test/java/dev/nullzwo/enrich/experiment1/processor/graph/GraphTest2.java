package dev.nullzwo.enrich.experiment1.processor.graph;

import dev.nullzwo.enrich.experiment1.processor.FixGraph;
import dev.nullzwo.enrich.experiment1.processor.GraphChecker;
import dev.nullzwo.enrich.experiment1.processor.ProcessorGraphTest;

import java.util.*;
import java.util.stream.Collectors;

import static dev.nullzwo.enrich.experiment1.processor.FixGraph.cata;

class GraphTest2 extends ProcessorGraphTest {

	@Override
	public GraphChecker checker() {
		return new GraphChecker() {
			@Override
			public boolean isAcyclicGraph(FixGraph graph) {
				Map<String, Set<String>> edges = cata(graph, n -> {
					var childs = n.results().stream()
							.flatMap(m -> m.keySet().stream())
							.collect(Collectors.toSet());
					var thiz = Map.of(n.triggering().name(), childs);
					return n.results().stream().reduce(thiz, this::merge);
				});

				return !new Graph(edges).containsALoop();
			}

			Map<String, Set<String>> merge(Map<String, Set<String>> a, Map<String, Set<String>> b) {
				var all = new HashSet<>(a.keySet());
				all.addAll(b.keySet());

				var m = new HashMap<String, Set<String>>();
				for (String k : all) {
					var aa = a.get(k);
					var bb = b.get(k);
					if (aa == null) {
						m.put(k, bb);
					} else if (bb == null) {
						m.put(k, aa);
					} else {
						Set<String> merged = new HashSet<>(aa);
						aa.addAll(bb);
						m.put(k, merged);
					}
				}
				return m;
			}
		};
	}
}
