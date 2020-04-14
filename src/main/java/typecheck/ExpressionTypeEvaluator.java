package typecheck;

import analysis.DepthFirstAdapter;
import node.*;

public class ExpressionTypeEvaluator extends DepthFirstAdapter {
  final private SymbolTable symbolTable;
  private Type type;

  public ExpressionTypeEvaluator(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.type = Type.ERROR; // initial "result-value"
  }


  // Simple type expressions ("Leafs")
  @Override
  public void caseABooleanExpr(ABooleanExpr node) {
    type = Type.BOOLEAN;
  }

  @Override
  public void caseAFloatExpr(AFloatExpr node) {
    type = Type.FLOAT;
  }

  @Override
  public void caseAIntExpr(AIntExpr node) {
    type = Type.INT;
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();

    if (symbolTable.contains(id)) {
      type = symbolTable.getType(id);
    }
  }


  // Arithmetic expressions (->float and int allowed)
  @Override
  public void caseAPlusExpr(APlusExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
      type = Type.INT;
    } else if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.FLOAT;
    }
  }

  @Override
  public void caseAMinusExpr(AMinusExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
      type = Type.INT;
    } else if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.FLOAT;
    }
  }

  @Override
  public void caseAMultExpr(AMultExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
      type = Type.INT;
    } else if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.FLOAT;
    }
  }

  @Override
  public void caseADivExpr(ADivExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
      type = Type.INT;
    } else if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.FLOAT;
    }
  }

  @Override
  public void caseAModExpr(AModExpr node) { // only defined for integers!
    if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
      type = Type.INT;
    }
  }


  // Boolean expressions (->only boolean allowed)
  @Override
  public void caseAAndExpr(AAndExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.BOOLEAN)) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseAOrExpr(AOrExpr node) {
    if (bothOfType(node.getLeft(), node.getRight(), Type.BOOLEAN)) {
      type = Type.BOOLEAN;
    }
  }


  // Comparison expressions (many work with floats and ints, but result always is boolean)
  @Override
  public void caseAEqExpr(AEqExpr node) {
    // Booleans, floats and ints can be compared, but the type needs to be the same
    if (haveSameType(node.getLeft(), node.getRight()) || bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseANeqExpr(ANeqExpr node) {
    // Booleans, floats and ints can be compared, but the type needs to be the same
    if (haveSameType(node.getLeft(), node.getRight()) || bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseALtExpr(ALtExpr node) {
    if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseAGtExpr(AGtExpr node) {
    if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseALteqExpr(ALteqExpr node) {
    if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseAGteqExpr(AGteqExpr node) {
    if (bothNumerical(node.getLeft(), node.getRight())) {
      type = Type.BOOLEAN;
    }
  }


  // Unary expressions
  @Override
  public void caseANotExpr(ANotExpr node) {
    if (hasType(node.getExpr(), Type.BOOLEAN)) {
      type = Type.BOOLEAN;
    }
  }

  @Override
  public void caseAUplusExpr(AUplusExpr node) {
    if (hasType(node.getExpr(), Type.FLOAT)) {
      type = Type.FLOAT;
    } else if (hasType(node.getExpr(), Type.INT)) {
      type = Type.INT;
    }
  }

  @Override
  public void caseAUminusExpr(AUminusExpr node) {
    if (hasType(node.getExpr(), Type.FLOAT)) {
      type = Type.FLOAT;
    } else if (hasType(node.getExpr(), Type.INT)) {
      type = Type.INT;
    }
  }


  // Helpers
  private boolean bothNumerical(Node leftNode, Node rightNode) {
    return isNumerical(leftNode) && isNumerical(rightNode);
  }

  private boolean bothOfType(Node leftNode, Node rightNode, Type type) {
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

  private boolean hasType(Node node, Type type) {
    ExpressionTypeEvaluator exprEv = new ExpressionTypeEvaluator(symbolTable);
    node.apply(exprEv);
    return exprEv.getType().equals(type);
  }

  private boolean isNumerical(Node node) {
    return hasType(node, Type.FLOAT) || hasType(node, Type.INT);
  }

  public Type getType() {
    return type;
  }
}
