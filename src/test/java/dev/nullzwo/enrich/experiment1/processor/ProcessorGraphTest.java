package dev.nullzwo.enrich.experiment1.processor;

import net.jqwik.api.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

public abstract class ProcessorGraphTest {

	abstract GraphChecker checker();

	@Test
	void emptyGraphIsAcyclic() {
		var graph = new FixGraph(new EventGraph(new EventInfo("event"), List.of()));
		assertThat(graph).matches(checker()::isAcyclicGraph);
	}

	@Property
	void detectCyclicGraph(@ForAll("cyclic") FixGraph graph) {
		assertThat(graph).matches(not(checker()::isAcyclicGraph));
	}

	@Property
	void detectAcyclicGraph(@ForAll("acyclic") FixGraph graph) {
		assertThat(graph).matches(checker()::isAcyclicGraph);
	}

	Arbitrary<EventInfo> arbEventInfo() {
		return arbName().map(EventInfo::new);
	}

	Arbitrary<String> arbName() {
		return Arbitraries.strings().alpha().ofLength(5);
	}

	@Provide
	Arbitrary<FixGraph> cyclic() {
		return arbEventInfo().list().ofMinSize(1).flatMap(pool -> cata(new Pool(pool), this::createAcyclic)).map(g -> {
			return replaceLeaf(g, new FixGraph(new EventGraph<>(g.unfix().triggering(), List.of())));
		});
	}

	FixGraph replaceLeaf(FixGraph g, FixGraph leaf) {
		if(g.unfix().results().isEmpty()) {
			return new FixGraph(new EventGraph<>(g.unfix().triggering(), List.of(leaf)));
		} else {
			return new FixGraph(map(g.unfix(), s -> replaceLeaf(s, leaf)));
		}
	}

	// pool of events and names
	// start with single event
	// all used events are removed from pool
	// chose multiple subsets as subtrees
	// one of pool is always next child event

	record Pool(List<EventInfo> events) {}

	@Provide
	Arbitrary<FixGraph> acyclic() {
		return arbEventInfo().list().ofMinSize(1).flatMap(pool -> cata(new Pool(pool), this::createAcyclic));
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

	<A, B> EventGraph<B> map(EventGraph<A> g, Function<A, B> f) {
		return new EventGraph<>(g.triggering(), g.results().stream().map(f).toList());
	}

	<A> Arbitrary<List<A>> sequence(List<Arbitrary<A>> g) {
		return Combinators.combine(g).as(identity());
	}

	<A> Arbitrary<EventGraph<A>> sequence(EventGraph<Arbitrary<A>> g) {
		return sequence(g.results()).map(l -> new EventGraph(g.triggering(), l));
	}

	<AA> Arbitrary<FixGraph> cata(AA start, GraphAlgebra<AA> alg) {
		return map(alg.apply(start), (AA a) -> cata(a, alg)).flatMap(s -> sequence(s)).map(FixGraph::new);
	}

	interface GraphAlgebra<A> {
		Arbitrary<EventGraph<A>> apply(A a);
	}

//	@Property
//	void singleNodeGraphIsAcyclic(@ForAll ProcessorInfo pinfo) {
//		var graph = List.of(pinfo);
//		assertThat(graph).matches(checker()::isAcyclicGraph);
//	}
}
