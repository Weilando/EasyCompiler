package livenessanalysis;

import java.util.HashSet;
import java.util.stream.Collectors;
import symboltable.Symbol;

/** Node in an interference graph. */
public class InterferenceGraphNode {
  private final Symbol symbol;
  private final HashSet<InterferenceGraphNode> neighbors;
  private int color;

  /**
   * Node in an interference graph.
   *
   * @param symbol Related symbol of this node.
   */
  public InterferenceGraphNode(Symbol symbol) {
    this.symbol = symbol;
    this.color = -1;
    neighbors = new HashSet<>();
  }

  boolean isColored() {
    return color != -1;
  }

  int getColor() {
    return color;
  }

  void setColor(int color) {
    this.color = color;
  }

  void addNeighbor(InterferenceGraphNode node) {
    neighbors.add(node);
  }

  void removeSelfNeighborhood() {
    neighbors.remove(this);
  }

  HashSet<InterferenceGraphNode> getColoredNeighbors() {
    return (HashSet<InterferenceGraphNode>) neighbors.stream()
        .filter(InterferenceGraphNode::isColored)
        .collect(Collectors.toSet());
  }

  HashSet<Integer> getColorsInNeighborhood() {
    return (HashSet<Integer>) getColoredNeighbors().stream().map(InterferenceGraphNode::getColor)
        .collect(Collectors.toSet());
  }

  @Override
  public String toString() {
    return String.format("{symbol: %s, color: %d, neighbors: %s", symbol, color, neighbors);
  }
}
