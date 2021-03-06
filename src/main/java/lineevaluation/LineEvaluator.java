package lineevaluation;

import analysis.ReversedDepthFirstAdapter;
import node.Node;
import node.Start;
import node.Token;

import java.util.HashMap;

public class LineEvaluator extends ReversedDepthFirstAdapter {
  static private final HashMap<Node, Integer> lines = new HashMap<>();
  static private final HashMap<Node, Integer> positions = new HashMap<>();
  static private int last_line = -1;
  static private int last_pos = -1;

  public static void setLines(Start ast) {
    LineEvaluator lineEvaluator = new LineEvaluator();
    ast.apply(lineEvaluator);
  }

  static public int getLine(Node node) {
    return lines.get(node);
  }

  static public int getPosition(Node node) {
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
