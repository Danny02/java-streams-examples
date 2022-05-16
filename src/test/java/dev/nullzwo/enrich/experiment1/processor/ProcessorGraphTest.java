package dev.nullzwo.enrich.experiment1.processor;

import dev.nullzwo.enrich.experiment1.processor.FixGraph.Algebra;
import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static dev.nullzwo.enrich.experiment1.processor.AttrGraph.attribute;
import static dev.nullzwo.enrich.experiment1.processor.FixGraph.cata;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class ProcessorGraphTest {

	public abstract GraphChecker checker();

	@Test
	void emptyGraphIsAcyclic() {
		var graph = new FixGraph(new EventGraph(new EventInfo("event"), List.of()));
		assertThat(graph).matches(checker()::isAcyclicGraph, "is acyclic");
	}

	@Property
	void detectCyclicGraph(@ForAll("cyclic") FixGraph graph) {
		assertThat(graph).matches(not(checker()::isAcyclicGraph), "is cyclic");
	}

	@Property
	void detectAcyclicGraph(@ForAll("acyclic") FixGraph graph) {
		assertThat(graph).matches(checker()::isAcyclicGraph, "is acyclic");
	}

	Arbitrary<EventInfo> arbEventInfo() {
		return arbName().map(EventInfo::new);
	}

	Arbitrary<String> arbName() {
		return Arbitraries.strings().alpha().ofLength(5);
	}

	@Provide
	Arbitrary<FixGraph> cyclic() {
		var loopEvent = new EventInfo("looop");
		var withLoop = new FixGraph(new EventGraph<>(loopEvent, List.of(new FixGraph(new EventGraph<>(loopEvent, List.of())))));

		return arbEventPool().flatMap(pool -> Arbitraries.randoms().map(r -> insertAll(withLoop, pool, r)));
	}

	public static void main(String[] args) {
		var arb = new ProcessorGraphTest(){
			@Override
			public GraphChecker checker() {
				return null;
			}
		}.cyclic();

		for (int i = 0; i < 5; i++) {
			System.out.println(arb.sample());
		}
	}

	private FixGraph insertAll(FixGraph graph, Pool pool, Random r) {
		var current = graph;
		for (var event : pool.events) {
			current = insert(current, event, r);
		}
		return current;
	}

	private FixGraph insert(FixGraph graph, EventInfo event, Random r) {
		Algebra<Integer> count = g -> g.results().stream().reduce(0, (a, b) -> a + b) + 1;
		var anno = cata(graph, attribute(count));
		var index = r.nextInt(anno.head() + 1) - 1;
		return AttrGraph.toFix(insert(anno, event, index));
	}

	private AttrGraph<Integer> insert(AttrGraph<Integer> graph, EventInfo event, int index) {
		if(index == -1) {
			return new AttrGraph<>(0, new EventGraph<>(event, List.of(graph)));
		} else if(index == 0) {
			var next = new ArrayList<>(graph.tail().results());
			next.add(new AttrGraph<>(0, new EventGraph<>(event, List.of())));
			return new AttrGraph<>(0, new EventGraph<>(graph.tail().triggering(), next));
		} else {
			index--;
			var children = graph.tail().results();
			for (int i = 0; i < children.size(); i++) {
				var child = children.get(i);
				if(index < child.head()) {
					var replaces = insert(child, event, index);
					var next = new ArrayList<>(children);
					next.set(i, replaces);
					return new AttrGraph<>(graph.head(), new EventGraph<>(graph.tail().triggering(), next));
				} else {
					index -= child.head();
				}
			}
			throw new IllegalArgumentException("asd");
		}
	}

	record Pool(List<EventInfo> events) {}

	Arbitrary<Pool> arbEventPool() {
		return arbEventInfo().set().map(pool -> new Pool(new ArrayList<>(pool)));
	}

	@Provide
	Arbitrary<FixGraph> acyclic() {
		return arbEventPool().filter(p -> !p.events.isEmpty()).flatMap(pool -> ana(pool, this::createAcyclic));
	}

	Arbitrary<EventGraph<Pool>> createAcyclic(Pool pool) {
		var head = pool.events.get(0);
		var tail = pool.events.stream().skip(1).toList();
		return Arbitraries.subsetOf(tail)
				.filter(p -> !p.isEmpty())
				.map(s -> new Pool(s.stream().toList()))
				.list().ofMaxSize(3)
				.map(ps -> new EventGraph(head, ps));
	}

	<A, B> Arbitrary<EventGraph<B>> map(Arbitrary<EventGraph<A>> ag, Function<A, B> f) {
		return ag.map(g ->  new EventGraph<>(g.triggering(), g.results().stream().map(f).toList()));
	}

	<A> Arbitrary<List<A>> sequence(List<Arbitrary<A>> g) {
		return Combinators.combine(g).as(identity());
	}

	<A> Arbitrary<EventGraph<A>> sequence(EventGraph<Arbitrary<A>> g) {
		return sequence(g.results()).map(l -> new EventGraph(g.triggering(), l));
	}

	<AA> Arbitrary<FixGraph> ana(AA start, ArbGraphCoAlgebra<AA> alg) {
		return map(alg.apply(start), (AA a) -> ana(a, alg)).flatMap(s -> sequence(s)).map(FixGraph::new);
	}

	interface ArbGraphCoAlgebra<A> {
		Arbitrary<EventGraph<A>> apply(A a);
	}

//	@Property
//	void singleNodeGraphIsAcyclic(@ForAll ProcessorInfo pinfo) {
//		var graph = List.of(pinfo);
//		assertThat(graph).matches(checker()::isAcyclicGraph);
//	}
}
