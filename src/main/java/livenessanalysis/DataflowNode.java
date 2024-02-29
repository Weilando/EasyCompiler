package livenessanalysis;

import java.util.HashSet;
import symboltable.Symbol;

/** Node in a dataflow graph. */
public class DataflowNode implements Comparable<DataflowNode> {
  private final int number;
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
  public DataflowNode(int number, String statementType) {
    this.number = number;
    this.statementType = statementType;
    this.predecessors = new HashSet<>();
    this.successors = new HashSet<>();
    this.in = new HashSet<>();
    this.out = new HashSet<>();
    this.def = new HashSet<>();
    this.use = new HashSet<>();
  }

  private void addSuccessor(DataflowNode node) {
    successors.add(node);
  }

  private void addPredecessor(DataflowNode node) {
    predecessors.add(node);
  }

  HashSet<DataflowNode> getSuccessors() {
    return successors;
  }

  HashSet<DataflowNode> getPredecessors() {
    return predecessors;
  }

  void addEdgeTo(DataflowNode node) {
    node.addPredecessor(this);
    addSuccessor(node);
  }

  void addDef(Symbol symbol) {
    def.add(symbol);
  }

  public HashSet<Symbol> getDef() {
    return def;
  }

  void addUse(Symbol symbol) {
    use.add(symbol);
  }

  public HashSet<Symbol> getUse() {
    return use;
  }

  void addIn(HashSet<Symbol> symbols) {
    in.addAll(symbols);
  }

  HashSet<Symbol> getIn() {
    return this.in;
  }

  void addOut(HashSet<Symbol> symbols) {
    out.addAll(symbols);
  }

  public HashSet<Symbol> getOut() {
    return out;
  }

  public int getNumber() {
    return number;
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
    return "#%d\tDef: %s Use: %s, In: %s, Out: %s, Edges to: %s, Statement type: %s".formatted(
        getNumber(), getDef(), getUse(), getIn(), getOut(),
        getSuccessorNumbers(), getStatementType());
  }

  public int compareTo(DataflowNode node) {
    return Integer.compare(number, node.number);
  }
}
