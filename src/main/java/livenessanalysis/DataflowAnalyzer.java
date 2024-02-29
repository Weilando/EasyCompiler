package livenessanalysis;

import java.util.HashSet;
import java.util.PriorityQueue;
import symboltable.Symbol;

/**
 * Analyzer for an existing dataflow graph. Iterates over the graph and
 * determines in- and out-sets. Might be used to find unused variables.
 */
public class DataflowAnalyzer {
  private final DataflowNode start; // the Analyzer's start is the builder's bottom node

  /**
   * Analyzer for an existing dataflow graph. Iterates over the graph and
   * determines in- and out-sets. Might be used to find unused variables.
   *
   * @param dataflowGraphBuilder DataflowGraphBuilder with existing graph.
   */
  public DataflowAnalyzer(DataflowGraphBuilder dataflowGraphBuilder) {
    // analysis runs from the end to start for speedup
    this.start = dataflowGraphBuilder.getCurrent();
  }

  /*
   * Analyze the dataflow graph using Algorithm 10.4 from Appel, Modern Compiler
   * Impl. in Java. Initial in- and out-sets are empty.
   */
  void generateInAndOutSets() {
    if (start.getPredecessors().isEmpty()) {
      return;
    }
    boolean changes = true;
    PriorityQueue<DataflowNode> queue = new PriorityQueue<>();

    while (changes) {
      changes = false;

      DataflowNode curr;
      queue.add(start);
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

  DataflowNode getDataflowStart() {
    return this.start;
  }
}
