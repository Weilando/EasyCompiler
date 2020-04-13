package livenessanalysis;

import typecheck.Symbol;
import typecheck.SymbolTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class InterferenceGraphBuilder {
  private final HashMap<Symbol, InterferenceGraphNode> nodes;
  private final DataflowNode dataflowStart;

  public InterferenceGraphBuilder(SymbolTable symbolTable, DataflowNode dataflowStart) {
    this.dataflowStart = dataflowStart;
    this.nodes = symbolTable.generateInterferenceGraphNodes();
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
        if (predecessor.getNumber() < currNumber) queue.add(predecessor);
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
