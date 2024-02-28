package livenessanalysis;

import java.util.HashSet;
import symboltable.SymbolTable;

/**
 * Analyzer for an existing interference graph. Colors the graph with as few
 * colors as possible, and never colors two neighbors the same. Can be used to
 * determine the highest number of simultaneously used registers.
 */
public class InterferenceGraphAnalyzer {
  private final HashSet<InterferenceGraphNode> nodes;
  private int colors;

  /**
   * Analyzer for an existing interference graph. Colors the graph with as few
   * colors as possible, and never colors two neighbors the same. Can be used to
   * determine the highest number of simultaneously used registers.
   *
   * @param symbolTable   Filled symbol table.
   * @param dataflowStart Start node of the dataflow graph.
   * @param functionName  Name of the function to build the graph for.
   */
  public InterferenceGraphAnalyzer(
      SymbolTable symbolTable, DataflowNode dataflowStart, String functionName) {
    InterferenceGraphBuilder graphBuilder = new InterferenceGraphBuilder(
        symbolTable, dataflowStart, functionName);
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
