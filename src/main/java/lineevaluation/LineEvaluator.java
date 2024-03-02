package lineevaluation;

import analysis.ReversedDepthFirstAdapter;
import java.util.HashMap;
import node.Node;
import node.Token;

/**
 * Helper that determines the line and position of all nodes by applying
 * reversed depth first search to the AST.
 */
public class LineEvaluator extends ReversedDepthFirstAdapter {
  private final HashMap<Node, Integer> lines;
  private final HashMap<Node, Integer> positions;
  private int lastLine;
  private int lastPos;

  /**
   * Helper that determines the line and position of all nodes by applying
   * reversed depth first search to the AST.
   */
  public LineEvaluator() {
    this.lines = new HashMap<>();
    this.positions = new HashMap<>();
    this.lastLine = -1;
    this.lastPos = -1;
  }

  public int getLine(Node node) {
    return this.lines.get(node);
  }

  public int getPosition(Node node) {
    return this.positions.get(node);
  }

  /** Add the last line number and position to a non-token node. */
  public void defaultOut(Node node) {
    this.lines.put(node, this.lastLine);
    this.positions.put(node, this.lastPos);
  }

  /** Create line number and position for a token node. */
  public void defaultCase(Node node) {
    Token token = (Token) node;
    this.lastLine = token.getLine();
    this.lastPos = token.getPos();
    this.lines.put(node, this.lastLine);
    this.positions.put(node, this.lastPos);
  }
}
