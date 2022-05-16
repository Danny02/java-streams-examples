package dev.nullzwo.enrich.experiment1.processor;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public record Reaction(EventInfo triggering, EventInfo result) {
}
