package lineevaluation;

import analysis.ReversedDepthFirstAdapter;
import java.util.HashMap;
import node.Node;
import node.Start;
import node.Token;

/**
 * Helper that determines the line and position of a node by applying reversed
 * depth first search to the AST.
 */
public class LineEvaluator extends ReversedDepthFirstAdapter {
  private static final HashMap<Node, Integer> lines = new HashMap<>();
  private static final HashMap<Node, Integer> positions = new HashMap<>();
  private static int last_line = -1;
  private static int last_pos = -1;

  public static void setLines(Start ast) {
    LineEvaluator lineEvaluator = new LineEvaluator();
    ast.apply(lineEvaluator);
  }

  public static int getLine(Node node) {
    return lines.get(node);
  }

  public static int getPosition(Node node) {
    return positions.get(node);
  }

  // All non-token nodes
  public void defaultOut(Node node) {
    lines.put(node, last_line);
    positions.put(node, last_pos);
  }

  // All tokens
  public void defaultCase(Node node) {
    Token token = (Token) node;
    last_line = token.getLine();
    last_pos = token.getPos();
    lines.put(node, last_line);
    positions.put(node, last_pos);
  }
}
