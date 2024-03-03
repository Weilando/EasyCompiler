package symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import livenessanalysis.InterferenceGraphNode;
import node.AFunc;
import node.AMain;
import node.APrg;
import node.Node;

/**
 * A data structure that stores all known symbols and their types.
 * More precisely, it holds
 * 1. variables with identifier and type
 * 2. functions with their return types and lists of argument types
 * Please note that each function argument is a declared variable.
 */
public class SymbolTable {
  private final HashMap<String, FunctionArgumentTypeList> functionArgumentListTable;
  private final HashMap<String, Type> functionReturnTypeTable;
  private final HashMap<String, HashMap<String, Symbol>> symbolTablePerScope;
  private final HashMap<String, Integer> nextFreeSymbolNumberPerScope;

  /** Create a new symbol table with empty collections. */
  public SymbolTable() {
    this.functionArgumentListTable = new HashMap<>();
    this.functionReturnTypeTable = new HashMap<>();
    this.symbolTablePerScope = new HashMap<>();
    this.nextFreeSymbolNumberPerScope = new HashMap<>();
  }

  /** Creates the data structures for a new function scope. */
  public void addNewScope(String scopeName, Type returnType) {
    functionArgumentListTable.put(scopeName, new FunctionArgumentTypeList());
    functionReturnTypeTable.put(scopeName, returnType);
    symbolTablePerScope.put(scopeName, new HashMap<>());
    nextFreeSymbolNumberPerScope.put(scopeName, 0);
  }

  /** Adds a symbol to an existing function scope. */
  public void addSymbolToScope(String scopeName, String symbolId, Type symbolType) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    int symbolNumber = nextFreeSymbolNumberPerScope.get(scopeName);
    scopeSymbolTable.put(symbolId, new Symbol(symbolType, symbolNumber));
    nextFreeSymbolNumberPerScope.put(scopeName, ++symbolNumber);
  }

  public ArrayList<String> getScopeNames() {
    return new ArrayList<>(this.functionReturnTypeTable.keySet());
  }

  public boolean containsSymbol(String scopeName, String symbolId) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    return scopeSymbolTable.containsKey(symbolId);
  }

  public Type getSymbolType(String scopeName, String symbolId) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    return scopeSymbolTable.get(symbolId).getType();
  }

  public int countSymbolsInScope(String scopeName) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    return scopeSymbolTable.size();
  }

  public int getVariableNumber(String scopeName, String id) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    return scopeSymbolTable.get(id).getVariableNumber();
  }

  public Symbol getSymbol(String scopeName, String id) {
    HashMap<String, Symbol> scopeSymbolTable = symbolTablePerScope.get(scopeName);
    return scopeSymbolTable.get(id);
  }

  public void addFunctionArgumentType(String functionId, Type argumentType) {
    FunctionArgumentTypeList functionArgumentList = functionArgumentListTable.get(functionId);
    functionArgumentList.addArgumentType(argumentType);
  }

  public FunctionArgumentTypeList getFunctionArgumentTypes(String functionId) {
    return functionArgumentListTable.get(functionId);
  }

  public int getNumberOfArguments(String functionId) {
    return functionArgumentListTable.get(functionId).getNumberOfArguments();
  }

  public void addFunctionReturnType(String functionId, Type returnType) {
    functionReturnTypeTable.put(functionId, returnType);
  }

  public Type getFunctionReturnType(String functionId) {
    return functionReturnTypeTable.get(functionId);
  }

  /**
   * Determines the scope from the parent node class and identifier.
   * Walks up the AST until it finds a function definition.
   */
  public String determineScope(Node node) {
    Node parent = node.parent();
    Class<? extends Node> parentClass = parent.getClass();
    if (parentClass == AMain.class) {
      return "main";
    } else if (parentClass == AFunc.class) {
      AFunc p = (AFunc) parent;
      return p.getId().getText();
    } else if (parentClass == APrg.class) {
      System.out.println("Error while determining scope of node " + node);
      System.exit(0);
      return "";
    } else {
      return determineScope(parent);
    }
  }

  /**
   * Generate an interefence graph node for each symbol in the scope.
   *
   * @param scopeName Function name to generate the inteference graph for.
   * @return Map with one interference graph node per symbol.
   */
  public HashMap<Symbol, InterferenceGraphNode> generateInterferenceGraphNodes(String scopeName) {
    HashMap<String, Symbol> scopeSymbolTable = this.symbolTablePerScope.get(scopeName);
    HashMap<Symbol, InterferenceGraphNode> nodes = new HashMap<>();
    scopeSymbolTable.forEach(
        (name, symbol) -> nodes.put(symbol, new InterferenceGraphNode(symbol)));
    return nodes;
  }

  /**
   * Find all non-argument symbols in the scope.
   *
   * @param scopeName Function name to find non-argument symbols for.
   * @return List with non-argument symbols.
   */
  public List<Symbol> getNonArgumentSymbols(String scopeName) {
    HashMap<String, Symbol> scopeSymbolTable = this.symbolTablePerScope.get(scopeName);
    FunctionArgumentTypeList argumentTypeList = this.functionArgumentListTable.get(scopeName);
    final int numberOfArguments = argumentTypeList.getNumberOfArguments();
    return scopeSymbolTable.values()
        .stream().sorted(new SymbolComparator())
        .filter((Symbol symbol) -> (symbol.getVariableNumber() >= numberOfArguments))
        .toList();
  }
}
