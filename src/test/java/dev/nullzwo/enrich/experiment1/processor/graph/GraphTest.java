package dev.nullzwo.enrich.experiment1.processor.graph;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class GraphTest {

  @Test
  void cyclicConnectedGraphTest() {
    Map<String, Set<String>> mapping = new HashMap<>();
    mapping.put("c", Set.of("d"));
    mapping.put("d", Set.of("c", "g"));

    Graph graph = new Graph(mapping);
    Assertions.assertTrue(graph.containsALoop());
  }

  @Test
  void acyclicConnectedGraphTest() {
    Map<String, Set<String>> mapping = new HashMap<>();
    mapping.put("c", Set.of("d"));
    mapping.put("d", Set.of("g"));

    Graph graph = new Graph(mapping);
    Assertions.assertFalse(graph.containsALoop());
  }

  @Test
  void cyclicNonConnectedGraphTest() {
    Map<String, Set<String>> mapping = new HashMap<>();
    mapping.put("a", Set.of("b"));
    mapping.put("c", Set.of("d"));
    mapping.put("d", Set.of("c", "g"));

    Graph graph = new Graph(mapping);
    Assertions.assertTrue(graph.containsALoop());
  }

  @Test
  void acyclicNonConnectedGraphTest() {
    Map<String, Set<String>> mapping = new HashMap<>();
    mapping.put("a", Set.of("b"));
    mapping.put("c", Set.of("d"));
    mapping.put("d", Set.of("g"));

    Graph graph = new Graph(mapping);
    Assertions.assertFalse(graph.containsALoop());
  }

  @Test
  void acyclicNonConnectedGraphTest2() {
    Map<String, Set<String>> mapping = new HashMap<>();
    mapping.put("AAAAA", Set.of("UGQVT"));
    mapping.put("UGQVT", Set.of("AAAAA"));

    Graph graph = new Graph(mapping);
    Assertions.assertFalse(graph.containsALoop());
  }
}
