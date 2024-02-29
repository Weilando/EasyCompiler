package livenessanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import node.Node;
import node.Start;
import symboltable.Symbol;
import symboltable.SymbolTable;

/** Analyzer for live variables. */
public class LivenessAnalyzer {
  private final SymbolTable symbolTable;
  private final HashMap<String, DataflowGraphBuilder> dataflowGraphs;
  private final HashMap<String, DataflowAnalyzer> dataflowAnalyzers;

  /**
   * Analyzer for live variables.
   *
   * @param ast         Abstract syntax tree to analyze.
   * @param symbolTable Filled symbol table for the corresponding AST.
   */
  public LivenessAnalyzer(Start ast, SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.dataflowGraphs = createDataflowGraphs(ast);
    this.dataflowAnalyzers = analyzeDataflowGraphs();
  }

  /**
   * Create one dataflow graph (including in- and out-sets) per function.
   *
   * @param ast Abstract syntax tree to analyze.
   * @return Map with one dataflow graph per function.
   */
  HashMap<String, DataflowGraphBuilder> createDataflowGraphs(Start ast) {
    HashMap<String, DataflowGraphBuilder> dataflowGraphs = new HashMap<>();
    HashMap<String, Node> functionSubTrees = findFunctionSubtrees(ast);

    for (String scopeName : symbolTable.getScopeNames()) {
      DataflowGraphBuilder graphBuilder = new DataflowGraphBuilder(this.symbolTable);
      Node functionSubTree = functionSubTrees.get(scopeName);
      functionSubTree.apply(graphBuilder);

      dataflowGraphs.put(scopeName, graphBuilder);
    }

    return dataflowGraphs;
  }

  HashMap<String, DataflowAnalyzer> analyzeDataflowGraphs() {
    HashMap<String, DataflowAnalyzer> dataflowAnalyzers = new HashMap<>();
    for (String functionName : this.dataflowGraphs.keySet()) {
      DataflowGraphBuilder graphBuilder = this.dataflowGraphs.get(functionName);
      DataflowAnalyzer graphAnalyzer = new DataflowAnalyzer(graphBuilder);
      graphAnalyzer.generateInAndOutSets();
      dataflowAnalyzers.put(functionName, graphAnalyzer);
    }
    return dataflowAnalyzers;
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
    DataflowAnalyzer dataflowAnalyzer = this.dataflowAnalyzers.get(functionName);
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

  /**
   * Find declared function arguments that are never read.
   *
   * @param functionName Name of the function to analyze.
   * @return List of unused function arguments.
   */
  public List<Symbol> getUnusedArgs(String functionName) {
    DataflowGraphBuilder graphBuilder = this.dataflowGraphs.get(functionName);
    DataflowNode startNode = graphBuilder.getStart();
    HashSet<Symbol> definedArguments = startNode.getDef();
    HashSet<Symbol> usedArguments = startNode.getOut();
    return definedArguments.stream().filter(symbol -> !usedArguments.contains(symbol))
        .collect(Collectors.toList());

  }

  /**
   * Find declared function arguments that are never read.
   *
   * @return List of unused function arguments per function.
   */
  public HashMap<String, List<Symbol>> getUnusedArgsPerFunction() {
    HashMap<String, List<Symbol>> unusedArgsPerFunction = new HashMap<>();
    for (String functionName : this.symbolTable.getScopeNames()) {
      List<Symbol> unusedArguments = getUnusedArgs(functionName);
      unusedArgsPerFunction.put(functionName, unusedArguments);
    }
    return unusedArgsPerFunction;
  }
}
