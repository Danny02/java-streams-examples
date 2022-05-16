package dev.nullzwo.enrich.experiment1.processor;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public record EventGraph<A>(EventInfo triggering, List<A> results) {
	@Override
	public String toString() {
		if (results.isEmpty()) {
			return triggering.name();
		} else {
			var child =
					results.stream()
							.map(sub -> Arrays.stream(sub.toString().split("\n"))
									.map(l -> "\t" + l).collect(joining("\n")))
							.collect(joining(","));
			return triggering.name() + "(\n" + child + "\n)";
		}
	}
}
