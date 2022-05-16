package dev.nullzwo.enrich.experiment1.processor;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordTest {

	@Test
	void hasStructualEquality() {
		var a = new Reaction(new EventInfo("foo"), new EventInfo("bar"));
		var b = new Reaction(new EventInfo("foo"), new EventInfo("bar"));

		assertThat(a).isEqualTo(b);
	}
}
