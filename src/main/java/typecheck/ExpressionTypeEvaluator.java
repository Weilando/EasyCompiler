package typecheck;

import analysis.DepthFirstAdapter;
import node.*;

public class ExpressionTypeEvaluator extends DepthFirstAdapter {
  final private SymbolTable symbolTable;
  private String type;

  public ExpressionTypeEvaluator(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.type = "error"; // initial "result-value"
  }


  // Simple type expressions ("Leafs")
  @Override
  public void caseAIntExpr(AIntExpr node) {
    type = "int";
  }

  @Override
  public void caseABooleanExpr(ABooleanExpr node) {
    type = "boolean";
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();

    if (symbolTable.contains(id)) {
      type = symbolTable.getType(id);
    }
  }


  // Arithmetic expressions (->only int allowed)
  @Override
  public void caseAPlusExpr(APlusExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "int";
    }
  }

  @Override
  public void caseAMinusExpr(AMinusExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "int";
    }
  }

  @Override
  public void caseAMultExpr(AMultExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "int";
    }
  }

  @Override
  public void caseADivExpr(ADivExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "int";
    }
  }

  @Override
  public void caseAModExpr(AModExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "int";
    }
  }


  // Boolean expressions (->only boolean allowed)
  @Override
  public void caseAAndExpr(AAndExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "boolean")) {
      type = "boolean";
    }
  }

  @Override
  public void caseAOrExpr(AOrExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "boolean")) {
      type = "boolean";
    }
  }


  // Comparison expressions (many work with ints, but result always is boolean)
  @Override
  public void caseAEqExpr(AEqExpr node) {
    // Both ints and booleans can be compared, but the type needs to be the same
    if (haveSameType(node.getLeft(), node.getRight())) {
      type = "boolean";
    }
  }

  @Override
  public void caseANeqExpr(ANeqExpr node) {
    // Both ints and booleans can be compared, but the type needs to be the same
    if (haveSameType(node.getLeft(), node.getRight())) {
      type = "boolean";
    }
  }

  @Override
  public void caseALtExpr(ALtExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "boolean";
    }
  }

  @Override
  public void caseAGtExpr(AGtExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "boolean";
    }
  }

  @Override
  public void caseALteqExpr(ALteqExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "boolean";
    }
  }

  @Override
  public void caseAGteqExpr(AGteqExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), "int")) {
      type = "boolean";
    }
  }


  // Unary expressions
  @Override
  public void caseANotExpr(ANotExpr node) {
    if (hasType(node.getExpr(), "boolean")) {
      type = "boolean";
    }
  }

  @Override
  public void caseAUplusExpr(AUplusExpr node) {
    if (hasType(node.getExpr(), "int")) {
      type = "int";
    }
  }

  @Override
  public void caseAUminusExpr(AUminusExpr node) {
    if (hasType(node.getExpr(), "int")) {
      type = "int";
    }
  }


  // Helpers
  private boolean bothOfType(Node leftNode, Node rightNode, String type) {
    ExpressionTypeEvaluator leftEv = new ExpressionTypeEvaluator(symbolTable);
    ExpressionTypeEvaluator rightEv = new ExpressionTypeEvaluator(symbolTable);

    leftNode.apply(leftEv);
    rightNode.apply(rightEv);

    return leftEv.getType().equals(type) && rightEv.getType().equals(type);
  }

  private boolean haveSameType(Node leftNode, Node rightNode) {
    ExpressionTypeEvaluator leftEv = new ExpressionTypeEvaluator(symbolTable);
    ExpressionTypeEvaluator rightEv = new ExpressionTypeEvaluator(symbolTable);

    leftNode.apply(leftEv);
    rightNode.apply(rightEv);

    return leftEv.getType().equals(rightEv.getType());
  }

  private boolean hasType(Node node, String type) {
    ExpressionTypeEvaluator exprEv = new ExpressionTypeEvaluator(symbolTable);
    node.apply(exprEv);
    return exprEv.getType().equals(type);
  }

  public String getType() {
    return type;
  }
}
