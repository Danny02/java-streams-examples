package dev.nullzwo.enrich.experiment1.processor;

import java.util.function.Function;

public record FixGraph(EventGraph<FixGraph> unfix) {
	@Override
	public String toString() {
		return unfix.toString();
	}

	public static <A, B> EventGraph<B> map(EventGraph<A> g, Function<A, B> f) {
		return new EventGraph<>(g.triggering(), g.results().stream().map(f).toList());
	}

	public static <A> A cata(FixGraph start, Algebra<A> alg) {
		return alg.apply(map(start.unfix(), (FixGraph f) -> cata(f, alg)));
	}

	public interface Algebra<A> {
		A apply(EventGraph<A> a);
	}

	public static <A> FixGraph ana(A start, CoAlgebra<A> coalg) {
		return new FixGraph(map(coalg.apply(start), a -> ana(a, coalg)));
	}

	public interface CoAlgebra<A> {
		EventGraph<A> apply(A a);
	}
}
