package typecheck;

import analysis.DepthFirstAdapter;
import node.AAddExpr;
import node.AAndExpr;
import node.ABooleanExpr;
import node.AConcatExpr;
import node.ADivExpr;
import node.AEqExpr;
import node.AFloatExpr;
import node.AGtExpr;
import node.AGteqExpr;
import node.AIdExpr;
import node.AIntExpr;
import node.ALtExpr;
import node.ALteqExpr;
import node.AModExpr;
import node.AMulExpr;
import node.ANeqExpr;
import node.ANotExpr;
import node.AOrExpr;
import node.AStringExpr;
import node.ASubExpr;
import node.AUminusExpr;
import node.AUplusExpr;
import node.Node;
import node.PExpr;

/**
 * The expression type evaluator walks the AST and assigns types to expression
 * nodes. It does not checks any compatibilities.
 */
public class ExpressionTypeEvaluator extends DepthFirstAdapter {
  private final SymbolTable symbolTable;
  private Type type;

  public ExpressionTypeEvaluator(SymbolTable symbolTable) {
    this.symbolTable = symbolTable;
    this.type = Type.UNDEFINED;
  }

  // Simple type expressions ("Leafs")
  @Override
  public void caseABooleanExpr(ABooleanExpr node) {
    node.setType(Type.BOOLEAN);
    type = Type.BOOLEAN;
  }

  @Override
  public void caseAFloatExpr(AFloatExpr node) {
    node.setType(Type.FLOAT);
    type = Type.FLOAT;
  }

  @Override
  public void caseAIntExpr(AIntExpr node) {
    node.setType(Type.INT);
    type = Type.INT;
  }

  @Override
  public void caseAStringExpr(AStringExpr node) {
    node.setType(Type.STRING);
    type = Type.STRING;
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();

    if (symbolTable.contains(id)) {
      type = symbolTable.getType(id);
    } else {
      type = Type.ERROR;
    }
    node.setType(type);
  }

  // Unary expressions
  @Override
  public void caseAUminusExpr(AUminusExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildArithmeticUnary(node.getExpr());
      node.setType(type);
    }
  }

  @Override
  public void caseANotExpr(ANotExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      if (hasType(node.getExpr(), Type.BOOLEAN)) {
        type = Type.BOOLEAN;
      } else {
        type = Type.ERROR;
      }
      node.setType(type);
    }
  }

  @Override
  public void caseAUplusExpr(AUplusExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildArithmeticUnary(node.getExpr());
      node.setType(type);
    }
  }

  // Arithmetic expressions (->float and int allowed)
  @Override
  public void caseAAddExpr(AAddExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseASubExpr(ASubExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseAMulExpr(AMulExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseADivExpr(ADivExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseAModExpr(AModExpr node) { // only defined for integers!
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
        type = Type.INT;
      } else {
        type = Type.ERROR;
      }
      node.setType(type);
    }
  }

  // Boolean expressions (->only boolean allowed)
  @Override
  public void caseAAndExpr(AAndExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenBoolean(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseAOrExpr(AOrExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenBoolean(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  // Comparison expressions (many work with numbers, but result always is boolean)
  @Override
  public void caseAEqExpr(AEqExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenEqualityComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseANeqExpr(ANeqExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenEqualityComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseALtExpr(ALtExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseAGtExpr(AGtExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseALteqExpr(ALteqExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  @Override
  public void caseAGteqExpr(AGteqExpr node) {
    type = node.getType();
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      node.setType(type);
    }
  }

  // String operations
  @Override
  public void caseAConcatExpr(AConcatExpr node) {
    if (node.getType().equals(Type.UNDEFINED)) {
      ExpressionTypeEvaluator leftEv = new ExpressionTypeEvaluator(symbolTable);
      ExpressionTypeEvaluator rightEv = new ExpressionTypeEvaluator(symbolTable);

      node.getLeft().apply(leftEv);
      node.getRight().apply(rightEv);
      node.setType(Type.STRING);
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

  private Type evaluateChildArithmeticUnary(PExpr expr) {
    if (isNumerical(expr)) {
      ExpressionTypeEvaluator exprEv = new ExpressionTypeEvaluator(symbolTable);
      expr.apply(exprEv);
      return exprEv.getType();
    }
    return Type.ERROR;
  }

  private Type evaluateChildrenArithmetic(PExpr left, PExpr right) {
    if (bothOfType(left, right, Type.INT)) {
      return Type.INT;
    } else if (bothNumerical(left, right)) {
      return Type.FLOAT;
    }
    return Type.ERROR;
  }

  private Type evaluateChildrenArithmeticComparison(PExpr left, PExpr right) {
    if (bothNumerical(left, right)) {
      return Type.BOOLEAN;
    }
    return Type.ERROR;
  }

  private Type evaluateChildrenBoolean(PExpr left, PExpr right) {
    if (bothOfType(left, right, Type.BOOLEAN)) {
      return Type.BOOLEAN;
    }
    return Type.ERROR;
  }

  private Type evaluateChildrenEqualityComparison(PExpr left, PExpr right) {
    // Require the same type for comparisons (int and float are both numbers)
    if (haveSameType(left, right) || bothNumerical(left, right)) {
      return Type.BOOLEAN;
    }
    return Type.ERROR;
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
