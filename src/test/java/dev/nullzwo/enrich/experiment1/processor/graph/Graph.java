package dev.nullzwo.enrich.experiment1.processor.graph;

import java.util.*;

public class Graph {

  private final int numNodes;
  private final boolean[][] adjacencyMatrix;

  public Graph(Map<String, Set<String>> mappings) {
    // count nodes
    Set<String> nodes = new HashSet<>();
    for (Map.Entry<String, Set<String>> entry : mappings.entrySet()) {
      nodes.add(entry.getKey());
      nodes.addAll(entry.getValue());
    }
    numNodes = nodes.size();
    adjacencyMatrix = new boolean[numNodes][numNodes];

    // create label mapping
    List<String> nodeOrder = nodes.stream().toList();
    Map<String, Integer> labels = new HashMap<>();
    for (int i = 0; i < numNodes; i++) {
      labels.put(nodeOrder.get(i), i);
    }

    // create edges
    for (Map.Entry<String, Set<String>> entry : mappings.entrySet()) {
      for (String child : entry.getValue()) {
        adjacencyMatrix[labels.get(entry.getKey())][labels.get(child)] = true;
      }
    }
  }


  public boolean containsALoop() {
    boolean[] visited = new boolean[numNodes];
    boolean[] stack = new boolean[numNodes];
    for (int i = 0; i < numNodes; i++) {
      if (!visited[i]) {
        if (containsALoop(i, visited, stack)) return true;
      }
    }
    return false;
  }

  private boolean containsALoop(int position, boolean[] visited, boolean[] stack) {
    if (stack[position]) return true;
    if (visited[position]) return false;
    visited[position] = true;
    stack[position] = true;
    for (int i = 0; i < numNodes; i++) {
      if (adjacencyMatrix[position][i] && containsALoop(i, visited, stack))
        return true;
    }
    stack[position] = false;
    return false;
  }
}
