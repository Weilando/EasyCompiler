package livenessanalysis;

import java.util.HashMap;
import node.Node;
import node.Start;
import symboltable.SymbolTable;

/** Analyzer for live variables. */
public class LivenessAnalyzer {
  private final SymbolTable symbolTable;
  private final HashMap<String, DataflowGraphBuilder> dataflowGraphs;

  /**
   * Analyzer for live variables.
   *
   * @param ast         Abstract syntax tree to analyze.
   * @param symbolTable Filled symbol table for the corresponding AST.
   */
  public LivenessAnalyzer(Start ast, SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.dataflowGraphs = createDataflowGraphs(ast, symbolTable);
  }

  /**
   * Create one dataflow graph per function.
   *
   * @param ast         Abstract syntax tree to analyze.
   * @param symbolTable Filled symbol table corresponding to the AST.
   * @return Map with one dataflow graph per function.
   */
  HashMap<String, DataflowGraphBuilder> createDataflowGraphs(Start ast, SymbolTable symbolTable) {
    HashMap<String, DataflowGraphBuilder> dataflowGraphs = new HashMap<>();
    HashMap<String, Node> functionSubTrees = findFunctionSubtrees(ast);

    for (String scopeName : symbolTable.getScopeNames()) {
      DataflowGraphBuilder graphBuilder = new DataflowGraphBuilder(symbolTable);
      Node functionSubTree = functionSubTrees.get(scopeName);
      functionSubTree.apply(graphBuilder);
      dataflowGraphs.put(scopeName, graphBuilder);
    }

    return dataflowGraphs;
  }

  private HashMap<String, Node> findFunctionSubtrees(Start ast) {
    FunctionSubTreeExtractor functionSubTreeExtractor = new FunctionSubTreeExtractor();
    ast.apply(functionSubTreeExtractor);
    return functionSubTreeExtractor.getFunctionSubTrees();
  }

  public void printDataflowGraph(String functionName) {
    DataflowGraphBuilder graphBuilder = this.dataflowGraphs.get(functionName);
    graphBuilder.printGraph();
  }

  /**
   * Approximate the minimum number of registers, using the maximum number of
   * variables that are potentially live simultaneously.
   *
   * @param functionName Name of the function to analyze.
   * @return Minimum number of required registers.
   */
  public int getMinimumRegisters(String functionName) {
    DataflowGraphBuilder graphBuilder = this.dataflowGraphs.get(functionName);
    DataflowAnalyzer dataflowAnalyzer = new DataflowAnalyzer(graphBuilder);
    InterferenceGraphAnalyzer interferenceGraphAnalyzer = new InterferenceGraphAnalyzer(
        symbolTable, dataflowAnalyzer.getDataflowStart(), functionName);
    return interferenceGraphAnalyzer.countColors();
  }

  /**
   * Approximate the minimum number of registers, using the maximum number of
   * variables that are potentially live simultaneously.
   *
   * @return Minimum number of required registers per function.
   */
  public HashMap<String, Integer> getMinimumRegistersPerFunction() {
    HashMap<String, Integer> minimumRegistersPerFunction = new HashMap<>();
    for (String functionName : this.symbolTable.getScopeNames()) {
      int minimumRegisters = getMinimumRegisters(functionName);
      minimumRegistersPerFunction.put(functionName, minimumRegisters);
    }
    return minimumRegistersPerFunction;
  }
}
