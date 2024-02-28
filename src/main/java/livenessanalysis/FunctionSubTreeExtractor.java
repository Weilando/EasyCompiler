package livenessanalysis;

import analysis.DepthFirstAdapter;
import java.util.HashMap;
import java.util.LinkedList;
import node.AFunc;
import node.AMain;
import node.APrg;
import node.Node;

/**
 * Extractor that collects the subtrees of all defined functions in the abstract
 * syntax tree.
 */
public class FunctionSubTreeExtractor extends DepthFirstAdapter {
  private LinkedList<Node> functionSubTrees;

  public FunctionSubTreeExtractor() {
    this.functionSubTrees = new LinkedList<>();
  }

  @Override
  public void caseAPrg(APrg node) {
    this.functionSubTrees.addAll(node.getFunc());
    this.functionSubTrees.add(node.getMain());
  }

  HashMap<String, Node> getFunctionSubTrees() {
    HashMap<String, Node> functionSubTreeMap = new HashMap<>();
    for (Node node : this.functionSubTrees) {
      if (node.getClass() == AMain.class) {
        functionSubTreeMap.put("main", node);
      } else {
        AFunc functionNode = (AFunc) node;
        String functionName = functionNode.getId().getText();
        functionSubTreeMap.put(functionName, node);
      }
    }
    return functionSubTreeMap;
  }
}
