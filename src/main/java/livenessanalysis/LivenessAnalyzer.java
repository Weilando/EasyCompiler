package livenessanalysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lineevaluation.LineEvaluator;
import node.Node;
import node.Start;
import symboltable.Symbol;
import symboltable.SymbolComparator;
import symboltable.SymbolTable;

/** Analyzer for live variables. */
public class LivenessAnalyzer {
  private final SymbolTable symbolTable;
  private final LineEvaluator lineEvaluator;
  private final HashMap<String, DataflowGraph> dataflowGraphs;

  /**
   * Analyzer for live variables.
   *
   * @param ast         Abstract syntax tree to analyze.
   * @param symbolTable Filled symbol table for the corresponding AST.
   */
  public LivenessAnalyzer(Start ast, SymbolTable symbolTable, LineEvaluator lineEvaluator) {
    this.symbolTable = symbolTable;
    this.lineEvaluator = lineEvaluator;
    this.dataflowGraphs = createDataflowGraphs(ast);
  }

  /**
   * Create one dataflow graph (including in- and out-sets) per function.
   *
   * @param ast Abstract syntax tree to analyze.
   * @return Map with one dataflow graph per function.
   */
  HashMap<String, DataflowGraph> createDataflowGraphs(Start ast) {
    HashMap<String, DataflowGraph> dataflowGraphs = new HashMap<>();
    HashMap<String, Node> functionSubTrees = findFunctionSubtrees(ast);

    for (String scopeName : symbolTable.getScopeNames()) {
      Node functionSubTree = functionSubTrees.get(scopeName);
      DataflowGraph dataflowGraph = new DataflowGraph(
          this.symbolTable, this.lineEvaluator, functionSubTree);
      dataflowGraphs.put(scopeName, dataflowGraph);
    }

    return dataflowGraphs;
  }

  private HashMap<String, Node> findFunctionSubtrees(Start ast) {
    FunctionSubTreeExtractor functionSubTreeExtractor = new FunctionSubTreeExtractor();
    ast.apply(functionSubTreeExtractor);
    return functionSubTreeExtractor.getFunctionSubTrees();
  }

  public void printDataflowGraph(String functionName) {
    DataflowGraph dataflowGraph = this.dataflowGraphs.get(functionName);
    dataflowGraph.printGraph();
  }

  /**
   * Approximate the minimum number of registers, using the maximum number of
   * variables that are potentially live simultaneously.
   *
   * @param functionName Name of the function to analyze.
   * @return Minimum number of required registers.
   */
  public int getMinimumRegisters(String functionName) {
    DataflowGraph dataflowGraph = this.dataflowGraphs.get(functionName);
    InterferenceGraph interferenceGraphAnalyzer = new InterferenceGraph(
        symbolTable, dataflowGraph.getEnd(), functionName);
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
    DataflowGraph dataflowGraph = this.dataflowGraphs.get(functionName);
    DataflowNode startNode = dataflowGraph.getStart();
    HashSet<Symbol> definedArguments = startNode.getDef();
    HashSet<Symbol> usedArguments = startNode.getOut();
    return findDefinedButUnusedSymbols(definedArguments, usedArguments);
  }

  /**
   * Find symbols that are defined but never read. Sorts the list based on the
   * variable numbers of the symbols.
   *
   * @param definedSymbols Symbols that are declared (and maybe written)
   * @param usedSymbols    Symbols that are read at least once
   * @return Sorted list of unused Symbols
   */

  public static List<Symbol> findDefinedButUnusedSymbols(
      HashSet<Symbol> definedSymbols, HashSet<Symbol> usedSymbols) {
    List<Symbol> unusedArguments = definedSymbols.stream()
        .filter(symbol -> !usedSymbols.contains(symbol))
        .collect(Collectors.toList());
    unusedArguments.sort(new SymbolComparator());
    return unusedArguments;
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
