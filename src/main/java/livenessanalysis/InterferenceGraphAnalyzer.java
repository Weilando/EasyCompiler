package livenessanalysis;

import typecheck.SymbolTable;

import java.util.HashSet;

public class InterferenceGraphAnalyzer {
  private final HashSet<InterferenceGraphNode> nodes;
  private int colors;

  public InterferenceGraphAnalyzer(SymbolTable symbolTable, DataflowNode dataflowStart) {
    InterferenceGraphBuilder graphBuilder = new InterferenceGraphBuilder(symbolTable, dataflowStart);
    this.nodes = graphBuilder.getGraphNodeSet();
    this.colors = 0;
    colorGraph();
  }

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
