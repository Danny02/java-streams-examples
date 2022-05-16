package dev.nullzwo.enrich.experiment1.processor;

public class MyGraphCheckerTest extends ProcessorGraphTest {

	@Override
	public GraphChecker checker() {
		return new MyGraphChecker();
	}
}
