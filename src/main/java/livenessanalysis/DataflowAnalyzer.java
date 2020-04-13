package livenessanalysis;

import typecheck.Symbol;

import java.util.HashSet;
import java.util.PriorityQueue;

public class DataflowAnalyzer {
  private final DataflowNode start;

  public DataflowAnalyzer(DataflowGraphBuilder dataflowGraphBuilder) {
    this.start = dataflowGraphBuilder.getCurrent(); // analysis should run from the end to start for speedup
    analyzeGraph();
  }

  private void analyzeGraph() {
    generateInAndOutSets();
  }

  private void generateInAndOutSets() { // algorithm 10.4 from Appel, Modern Compiler Impl. in Java; initial in- and out-sets are empty
    if (start.getPredecessors().isEmpty()) return;
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

        if (differentElementsIn(oldIn, curr.getIn()) || differentElementsIn(oldOut, curr.getOut())) {
          changes = true;
        }
        for (DataflowNode predecessor : curr.getPredecessors()) {
          if (predecessor.getNumber() < currNumber) queue.add(predecessor);
        }
      }
    }
  }

  private HashSet<Symbol> getUseJointOutMinusDef(DataflowNode node) { // use[n] joint with (out[n]-def[n])
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
