package dev.nullzwo.enrich.experiment1.processor;

public class MyGraphCheckerTest extends ProcessorGraphTest {

	@Override
	GraphChecker checker() {
		return new MyGraphChecker();
	}
}
