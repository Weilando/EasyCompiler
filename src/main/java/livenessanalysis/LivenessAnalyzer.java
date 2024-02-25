package livenessanalysis;

import node.Start;
import symboltable.SymbolTable;

/** Analyzer for live variables. */
public class LivenessAnalyzer {
  private final DataflowGraphBuilder graphBuilder;
  private final InterferenceGraphAnalyzer interferenceGraphAnalyzer;

  /**
   * Analyzer for live variables.
   *
   * @param ast         Abstract syntax tree to analyze.
   * @param symbolTable Filled symbol table for the corresponding AST.
   */
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
