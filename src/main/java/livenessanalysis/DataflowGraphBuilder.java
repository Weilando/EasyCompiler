package livenessanalysis;

import analysis.DepthFirstAdapter;
import java.util.PriorityQueue;
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
import node.AWhileStat;
import symboltable.Symbol;
import symboltable.SymbolTable;

/**
 * Builder for a dataflow graph. Performs a backwards analysis of the AST (i.e.,
 * uses a depth first approach). To build a dataflow graph, apply this class to
 * a function-related sub-tree of the AST.
 */
public class DataflowGraphBuilder extends DepthFirstAdapter {
  private DataflowNode start;
  private final SymbolTable symbolTable;
  private DataflowNode current;
  private int currentNumber;

  /**
   * Builder for a dataflow graph. Performs a backwards analysis of the AST (i.e.,
   * uses a depth first approach). To build a dataflow graph, apply this class to
   * a function-related sub-tree of the AST.
   *
   * @param symbolTable Filled symbol table for the corresponding AST.
   */
  public DataflowGraphBuilder(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.start = null;
    this.currentNumber = 0;
    this.current = null;
  }

  @Override
  public void inAFunc(AFunc node) {
    this.start = getNewSymbolNode("function head");
    this.current = this.start;
  }

  @Override
  public void inAMain(AMain node) {
    this.start = getNewSymbolNode("function head");
    this.current = this.start;
  }

  @Override
  public void inADeclArg(ADeclArg node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);
    current.addDef(defSymbol);
  }

  // Statements
  // Create a new Node while going in a statement and add symbol to def-set, if it
  // is a writing statement
  // Writing Statements
  @Override
  public void inAInitStat(AInitStat node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);

    DataflowNode successor = getNewSymbolNode("init");
    successor.addDef(defSymbol);

    current.addEdgeTo(successor);
    current = successor;
  }

  @Override
  public void inAAssignStat(AAssignStat node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol defSymbol = symbolTable.getSymbol(scopeName, id);

    DataflowNode successor = getNewSymbolNode("assign");
    successor.addDef(defSymbol);

    current.addEdgeTo(successor);
    current = successor;
  }

  // Possibly reading statements
  @Override
  public void inADeclStat(ADeclStat node) {
    DataflowNode successor = getNewSymbolNode("declaration");
    current.addEdgeTo(successor);
    current = successor;
  }

  @Override
  public void inAPrintStat(APrintStat node) {
    DataflowNode successor = getNewSymbolNode("print");
    current.addEdgeTo(successor);
    current = successor;
  }

  @Override
  public void inAPrintlnStat(APrintlnStat node) {
    DataflowNode successor = getNewSymbolNode("println");
    current.addEdgeTo(successor);
    current = successor;
  }

  @Override
  public void caseAIfStat(AIfStat node) {
    DataflowNode successor = getNewSymbolNode("if");
    current.addEdgeTo(successor);
    current = successor;

    node.getExpr().apply(this);
    node.getThenBlock().apply(this);
  }

  @Override
  public void inAIfelseStat(AIfelseStat node) {
    DataflowNode successor = getNewSymbolNode("if else");
    current.addEdgeTo(successor);
    current = successor;

    node.getExpr().apply(this);
    node.getThenBlock().apply(this);
    node.getElseBlock().apply(this);
  }

  @Override
  public void inAWhileStat(AWhileStat node) {
    DataflowNode successor = getNewSymbolNode("while");
    current.addEdgeTo(successor);
    current = successor;

    node.getExpr().apply(this);
    node.getBody().apply(this);

    // current is the last statement of while-body now and needs to point on the
    // head of this while loop
    current.addEdgeTo(successor);
  }

  // Expressions; add symbols to current DataflowNode's use-set
  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    Symbol symbol = symbolTable.getSymbol(scopeName, id);
    current.addUse(symbol);
  }

  // Helpers
  private DataflowNode getNewSymbolNode(String statementType) {
    return new DataflowNode(++currentNumber, statementType);
  }

  DataflowNode getStart() {
    return this.start;
  }

  DataflowNode getCurrent() {
    return this.current;
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
}
