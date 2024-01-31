package livenessanalysis;

import node.Start;
import symboltable.SymbolTable;

public class LivenessAnalyzer {
  private final DataflowGraphBuilder graphBuilder;
  private final InterferenceGraphAnalyzer interferenceGraphAnalyzer;

  public LivenessAnalyzer(Start ast, SymbolTable symbolTable) {
    this.graphBuilder = new DataflowGraphBuilder(symbolTable);
    ast.apply(graphBuilder);

    DataflowAnalyzer dataflowAnalyzer = new DataflowAnalyzer(graphBuilder);
    this.interferenceGraphAnalyzer = new InterferenceGraphAnalyzer(
        symbolTable, dataflowAnalyzer.getDataflowStart());
  }

  public void printGraph() {
    graphBuilder.printGraph();
  }

  public int getMinimumRegisters() {
    return interferenceGraphAnalyzer.countColors();
  }
}
