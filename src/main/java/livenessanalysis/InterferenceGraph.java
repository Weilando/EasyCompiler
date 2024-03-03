package livenessanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import symboltable.Symbol;
import symboltable.SymbolTable;

/**
 * Interference graph with dataflow nodes and symbol nodes. Colors represent
 * registers, and two neighbors never have the same color. Can be used to
 * determine simultaneously usable registers..
 */
public class InterferenceGraph {
  private final HashSet<InterferenceGraphNode> nodes;
  private int colors;

  /**
   * Builder for an interference graph based on a dataflow graph. Creates
   * additional symbol nodes and colors the graph with as few colors as possible.
   * Neighbors never have the same color.
   * The colors determine simultaneously usable registers.
   *
   * @param symbolTable   Filled symbol table.
   * @param dataflowStart Start node of the dataflow graph.
   * @param functionName  Name of the function to build the graph for.
   */
  public InterferenceGraph(
      SymbolTable symbolTable, DataflowNode dataflowStart, String functionName) {
    HashMap<Symbol, InterferenceGraphNode> nodeMap = generateGraph(
        symbolTable, dataflowStart, functionName);

    this.nodes = getGraphNodeSetFromMap(nodeMap);
    this.colors = 0;
    colorGraph();
  }

  /**
   * Adds symbol nodes to the dataflow graph and creates edges based on in- and
   * out-sets.
   *
   * @param symbolTable   Filled symbol table.
   * @param dataflowStart Start node of the dataflow graph.
   * @param functionName  Name of the function to build the graph for.
   */
  private HashMap<Symbol, InterferenceGraphNode> generateGraph(
      SymbolTable symbolTable, DataflowNode dataflowStart, String functionName) {
    HashMap<Symbol, InterferenceGraphNode> nodes = symbolTable
        .generateInterferenceGraphNodes(functionName);
    PriorityQueue<DataflowNode> queue = new PriorityQueue<>();
    queue.add(dataflowStart);

    while (queue.peek() != null) {
      DataflowNode curr = queue.poll();

      HashSet<Symbol> currOut = curr.getOut();
      for (Symbol currSymbol : currOut) {
        InterferenceGraphNode currNode = nodes.get(currSymbol);
        currOut.forEach(symbol -> nodes.get(symbol).addNeighbor(currNode));
      }

      for (DataflowNode predecessor : curr.getPredecessors()) {
        if (predecessor.getNumber() < curr.getNumber()) {
          queue.add(predecessor);
        }
      }
    }

    nodes.forEach((symbol, node) -> node.removeSelfNeighborhood());
    return nodes;
  }

  HashSet<InterferenceGraphNode> getGraphNodeSetFromMap(
      HashMap<Symbol, InterferenceGraphNode> nodeMap) {
    HashSet<InterferenceGraphNode> nodeSet = new HashSet<>();
    nodeMap.forEach((symbol, interferenceGraphNode) -> nodeSet.add(interferenceGraphNode));
    return nodeSet;
  }

  /** Color the graph nodes based on their neighborhood (existing edges). */
  void colorGraph() {
    for (InterferenceGraphNode currNode : nodes) {
      HashSet<Integer> neighborColors = currNode.getColorsInNeighborhood();
      int currColor = 1;
      while (neighborColors.contains(currColor)) {
        currColor++;
      }
      currNode.setColor(currColor);

      if (currColor > this.colors) {
        this.colors = currColor;
      }
    }
  }

  public int countColors() {
    return colors;
  }
}
