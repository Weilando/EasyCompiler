package livenessanalysis;

import java.util.HashSet;
import symboltable.Symbol;

/** Node in a dataflow graph. */
public class DataflowNode implements Comparable<DataflowNode> {
  private final int number;
  private final int lineNumber;
  private final String statementType;
  private final HashSet<DataflowNode> predecessors;
  private final HashSet<DataflowNode> successors;
  private final HashSet<Symbol> use;
  private final HashSet<Symbol> def;
  private final HashSet<Symbol> in;
  private final HashSet<Symbol> out;

  /**
   * Node in a dataflow graph.
   *
   * @param number        Identifier of this node.
   * @param statementType Related statement of this node.
   */
  public DataflowNode(int number, int lineNumber, String statementType) {
    this.number = number;
    this.lineNumber = lineNumber;
    this.statementType = statementType;
    this.predecessors = new HashSet<>();
    this.successors = new HashSet<>();
    this.in = new HashSet<>();
    this.out = new HashSet<>();
    this.def = new HashSet<>();
    this.use = new HashSet<>();
  }

  private void addSuccessor(DataflowNode node) {
    this.successors.add(node);
  }

  private void addPredecessor(DataflowNode node) {
    this.predecessors.add(node);
  }

  HashSet<DataflowNode> getSuccessors() {
    return this.successors;
  }

  HashSet<DataflowNode> getPredecessors() {
    return this.predecessors;
  }

  void addEdgeTo(DataflowNode node) {
    node.addPredecessor(this);
    addSuccessor(node);
  }

  void addDef(Symbol symbol) {
    this.def.add(symbol);
  }

  public HashSet<Symbol> getDef() {
    return this.def;
  }

  void addUse(Symbol symbol) {
    this.use.add(symbol);
  }

  public HashSet<Symbol> getUse() {
    return this.use;
  }

  void addIn(HashSet<Symbol> symbols) {
    this.in.addAll(symbols);
  }

  HashSet<Symbol> getIn() {
    return this.in;
  }

  void addOut(HashSet<Symbol> symbols) {
    this.out.addAll(symbols);
  }

  public HashSet<Symbol> getOut() {
    return this.out;
  }

  public int getNumber() {
    return this.number;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  private String getSuccessorNumbers() {
    String returnString = "{";
    for (DataflowNode curr : getSuccessors()) {
      returnString = returnString.concat(String.format("#%d, ", curr.getNumber()));
    }
    return returnString.concat("}");
  }

  String getStatementType() {
    return statementType;
  }

  /** Generate a string representation including all relevant sets. */
  public String toString() {
    return "#%d, %s on line %d:\tDef: %s Use: %s, In: %s, Out: %s, Edges to: %s".formatted(
        getNumber(), getStatementType(), getLineNumber(),
        getDef(), getUse(), getIn(), getOut(), getSuccessorNumbers());
  }

  public int compareTo(DataflowNode node) {
    return Integer.compare(number, node.number);
  }
}
