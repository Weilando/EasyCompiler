package typecheck;

import livenessanalysis.InterferenceGraphNode;

import java.util.HashMap;

public class SymbolTable {
  final private HashMap<String, Symbol> table;
  private int lastSymbolNumber;

  public SymbolTable() {
    table = new HashMap<>();
    lastSymbolNumber = 0;
  }

  void add(String id, String type) {
    table.put(id, new Symbol(type, ++lastSymbolNumber));
  }

  public boolean contains(String id) {
    return table.containsKey(id);
  }

  public String getType(String id) {
    return table.get(id).getType();
  }

  public int countSymbols() {
    return table.size();
  }

  public int getVariableNumber(String id) {
    return table.get(id).getVariableNumber();
  }

  public Symbol getSymbol(String id) {
    return table.get(id);
  }

  void printTable() {
    System.out.println(String.format("Symbol table:\n%s\n", this.table));
  }

  public HashMap<Symbol, InterferenceGraphNode> generateInterferenceGraphNodes() {
    HashMap<Symbol, InterferenceGraphNode> nodes = new HashMap<>();
    table.forEach((name, symbol) -> nodes.put(symbol, new InterferenceGraphNode(symbol)));
    return nodes;
  }
}
