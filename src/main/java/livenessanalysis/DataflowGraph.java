package livenessanalysis;

import analysis.DepthFirstAdapter;
import java.util.HashSet;
import java.util.PriorityQueue;
import lineevaluation.LineEvaluator;
import node.AAssignStat;
import node.ADeclArg;
import node.ADeclStat;
import node.AFunc;
import node.AIdExpr;
import node.AIfStat;
import node.AIfelseStat;
import node.AInitStat;
import node.AMain;
import node.APrintStat;
import node.APrintlnStat;
import node.AReturnStat;
import node.AWhileStat;
import node.Node;
import symboltable.Symbol;
import symboltable.SymbolTable;

/**
 * Dataflow graph with one node per statement. Nodes register the symbol
 * definition and usage across the program.
 */
public class DataflowGraph extends DepthFirstAdapter {
  private final SymbolTable symbolTable;
  private final LineEvaluator lineEvaluator;
  private DataflowNode start;
  private DataflowNode end;
  private DataflowNode current;
  private int currentNumber;

  /**
   * Builder for a dataflow graph. Walks the AST to create a node per statement.
   * Performs a backwards analysis of the resulting graph to determine in- and
   * out-sets.
   *
   * @param symbolTable   Filled symbol table for the corresponding AST.
   * @param lineEvaluator Line evaluator that has been applied to the AST.
   * @param astStart      Start node of the (function-related sub-)AST.
   */
  public DataflowGraph(
      SymbolTable symbolTable, LineEvaluator lineEvaluator, Node astStart) {
    this.symbolTable = symbolTable;
    this.lineEvaluator = lineEvaluator;
    this.start = null;
    this.end = null;
    this.currentNumber = 0;
    this.current = null;

    astStart.apply(this);
    this.end = this.current;
    generateInAndOutSets();
  }

  // Function heads: define dataflow graph start node
  @Override
  public void inAFunc(AFunc node) {
    this.start = getNewSymbolNode(node);
    this.current = this.start;
  }

  @Override
  public void inAMain(AMain node) {
    this.start = getNewSymbolNode(node);
    this.current = this.start;
  }

  // Argument definitions: add arguments to the start's def-set
  @Override
  public void inADeclArg(ADeclArg node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);
    current.addDef(defSymbol);
  }

  // Writing statements: create new nodes and add symbol to def-set
  @Override
  public void inAInitStat(AInitStat node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);

    DataflowNode successor = getNewSymbolNode(node);
    successor.addDef(defSymbol);

    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  @Override
  public void inAAssignStat(AAssignStat node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);

    DataflowNode successor = getNewSymbolNode(node);
    successor.addDef(defSymbol);

    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  // Possibly reading statements: create new nodes
  @Override
  public void inADeclStat(ADeclStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  @Override
  public void inAPrintStat(APrintStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  @Override
  public void inAPrintlnStat(APrintlnStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  @Override
  public void caseAIfStat(AIfStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;

    node.getExpr().apply(this);
    node.getThenBlock().apply(this);
  }

  @Override
  public void inAIfelseStat(AIfelseStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;

    node.getExpr().apply(this);
    node.getThenBlock().apply(this);
    node.getElseBlock().apply(this);
  }

  @Override
  public void inAWhileStat(AWhileStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;

    node.getExpr().apply(this);
    node.getBody().apply(this);

    // current is the last statement of while-body now and needs to point on the
    // head of this while loop
    this.current.addEdgeTo(successor);
  }

  @Override
  public void inAReturnStat(AReturnStat node) {
    DataflowNode successor = getNewSymbolNode(node);
    this.current.addEdgeTo(successor);
    this.current = successor;
  }

  // Expressions: add symbol to current DataflowNode's use-set ("read" statement)
  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol symbol = symbolTable.getSymbol(scopeName, id);
    this.current.addUse(symbol);
  }

  // Helpers
  private DataflowNode getNewSymbolNode(Node node) {
    int lineNumber = this.lineEvaluator.getLine(node);
    String statementType = node.getClass().getSimpleName();
    return new DataflowNode(++currentNumber, lineNumber, statementType);
  }

  DataflowNode getStart() {
    return this.start;
  }

  DataflowNode getEnd() {
    return this.end;
  }

  /** Print the resulting dataflow graph including relevant sets. */
  public void printGraph() {
    PriorityQueue<DataflowNode> printQueue = new PriorityQueue<>();
    printQueue.add(start);

    while (printQueue.peek() != null) {
      DataflowNode currNode = printQueue.poll();
      int currNumber = currNode.getNumber();

      System.out.println(currNode);

      for (DataflowNode successor : currNode.getSuccessors()) {
        if (successor.getNumber() > currNumber) {
          printQueue.add(successor);
        }
      }
    }
    System.out.println();
  }

  /*
   * Analyze the dataflow graph using Algorithm 10.4 from Appel, "Modern Compiler
   * Impl. in Java" to determine in- and out-sets. Initial in- and out-sets are
   * empty. The sets can be used to determine unused variables.
   */
  void generateInAndOutSets() {
    if (this.end.getPredecessors().isEmpty()) {
      return;
    }
    boolean changes = true;
    PriorityQueue<DataflowNode> queue = new PriorityQueue<>();

    while (changes) {
      changes = false;

      DataflowNode curr;
      queue.add(this.end);
      while (queue.peek() != null) {
        curr = queue.poll();
        int currNumber = curr.getNumber();

        HashSet<Symbol> oldIn = new HashSet<>(curr.getIn());
        HashSet<Symbol> oldOut = new HashSet<>(curr.getOut());
        curr.addIn(getUseJointOutMinusDef(curr));
        curr.addOut(getJoinedSuccessorIns(curr));

        if (differentElementsIn(oldIn, curr.getIn())
            || differentElementsIn(oldOut, curr.getOut())) {
          changes = true;
        }
        for (DataflowNode predecessor : curr.getPredecessors()) {
          if (predecessor.getNumber() < currNumber) {
            queue.add(predecessor);
          }
        }
      }
    }
  }

  /* use[n] joint with (out[n]-def[n]). */
  private HashSet<Symbol> getUseJointOutMinusDef(DataflowNode node) {
    HashSet<Symbol> returnSet = new HashSet<>(node.getUse());

    HashSet<Symbol> difference = new HashSet<>(node.getOut());
    difference.removeAll(node.getDef());
    returnSet.addAll(difference);

    return returnSet;
  }

  private HashSet<Symbol> getJoinedSuccessorIns(DataflowNode node) {
    HashSet<Symbol> returnSet = new HashSet<>();
    HashSet<DataflowNode> successors = node.getSuccessors();

    successors.forEach(successor -> returnSet.addAll(successor.getIn()));

    return returnSet;
  }

  private boolean differentElementsIn(HashSet<Symbol> setA, HashSet<Symbol> setB) {
    return !setA.containsAll(setB) || !setB.containsAll(setA);
  }
}
