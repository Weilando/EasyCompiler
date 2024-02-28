package livenessanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import symboltable.Symbol;
import symboltable.SymbolTable;

/**
 * Builder for an interference graph. Builds an interference graph based on a
 * dataflow graph.
 */
public class InterferenceGraphBuilder {
  private final HashMap<Symbol, InterferenceGraphNode> nodes;
  private final DataflowNode dataflowStart;

  /**
   * Builder for an interference graph. Builds an interference graph based on a
   * dataflow graph.
   *
   * @param symbolTable   Filled symbol table.
   * @param dataflowStart Start node of the dataflow graph.
   * @param functionName  Name of the function to build the graph for.
   */
  public InterferenceGraphBuilder(
      SymbolTable symbolTable, DataflowNode dataflowStart, String functionName) {
    this.dataflowStart = dataflowStart;
    this.nodes = symbolTable.generateInterferenceGraphNodes(functionName);
    generateEdges();
  }

  private void generateEdges() {
    PriorityQueue<DataflowNode> queue = new PriorityQueue<>();
    DataflowNode curr;
    queue.add(dataflowStart);

    while (queue.peek() != null) {
      curr = queue.poll();
      int currNumber = curr.getNumber();

      HashSet<Symbol> currOut = curr.getOut();
      for (Symbol currSymbol : currOut) {
        InterferenceGraphNode currNode = nodes.get(currSymbol);
        currOut.forEach(symbol -> nodes.get(symbol).addNeighbor(currNode));
      }

      for (DataflowNode predecessor : curr.getPredecessors()) {
        if (predecessor.getNumber() < currNumber) {
          queue.add(predecessor);
        }
      }
    }

    nodes.forEach((symbol, node) -> node.removeSelfNeighborhood());
  }

  HashSet<InterferenceGraphNode> getGraphNodeSet() {
    HashSet<InterferenceGraphNode> nodeSet = new HashSet<>();
    nodes.forEach((symbol, interferenceGraphNode) -> nodeSet.add(interferenceGraphNode));
    return nodeSet;
  }
}
