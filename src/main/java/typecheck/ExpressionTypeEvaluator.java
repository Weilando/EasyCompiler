package typecheck;

import analysis.DepthFirstAdapter;
import node.*;

public class ExpressionTypeEvaluator extends DepthFirstAdapter {
  final private ExpressionTypeCache expressionTypeCache;
  final private SymbolTable symbolTable;
  private Type type;

  public ExpressionTypeEvaluator(ExpressionTypeCache expressionTypeCache, SymbolTable symbolTable) {
    this.expressionTypeCache = expressionTypeCache;
    this.symbolTable = symbolTable;
    this.type = Type.UNDEFINED;
  }

  // Simple type expressions ("Leafs")
  @Override
  public void caseABooleanExpr(ABooleanExpr node) {
    this.expressionTypeCache.add(node, Type.BOOLEAN);
    type = Type.BOOLEAN;
  }

  @Override
  public void caseAFloatExpr(AFloatExpr node) {
    this.expressionTypeCache.add(node, Type.FLOAT);
    type = Type.FLOAT;
  }

  @Override
  public void caseAIntExpr(AIntExpr node) {
    this.expressionTypeCache.add(node, Type.INT);
    type = Type.INT;
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();

    if (symbolTable.contains(id)) {
      type = symbolTable.getType(id);
    } else {
      type = Type.ERROR;
    }
    this.expressionTypeCache.add(node, type);
  }


  // Arithmetic expressions (->float and int allowed)
  @Override
  public void caseAPlusExpr(APlusExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAMinusExpr(AMinusExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAMultExpr(AMultExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseADivExpr(ADivExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmetic(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAModExpr(AModExpr node) { // only defined for integers!
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      if (bothOfType(node.getLeft(), node.getRight(), Type.INT)) {
        type = Type.INT;
      } else {
        type = Type.ERROR;
      }
      this.expressionTypeCache.add(node, type);
    }
  }


  // Boolean expressions (->only boolean allowed)
  @Override
  public void caseAAndExpr(AAndExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenBoolean(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAOrExpr(AOrExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenBoolean(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }


  // Comparison expressions (many work with floats and ints, but result always is boolean)
  @Override
  public void caseAEqExpr(AEqExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenEqualityComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseANeqExpr(ANeqExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenEqualityComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseALtExpr(ALtExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAGtExpr(AGtExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseALteqExpr(ALteqExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAGteqExpr(AGteqExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildrenArithmeticComparison(node.getLeft(), node.getRight());
      this.expressionTypeCache.add(node, type);
    }
  }


  // Unary expressions
  @Override
  public void caseANotExpr(ANotExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      if (hasType(node.getExpr(), Type.BOOLEAN)) {
        type = Type.BOOLEAN;
      } else {
        type = Type.ERROR;
      }
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAUplusExpr(AUplusExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildArithmeticUnary(node.getExpr());
      this.expressionTypeCache.add(node, type);
    }
  }

  @Override
  public void caseAUminusExpr(AUminusExpr node) {
    type = this.expressionTypeCache.getType(node);
    if (type.equals(Type.UNDEFINED)) {
      type = evaluateChildArithmeticUnary(node.getExpr());
      this.expressionTypeCache.add(node, type);
    }
  }


  // Helpers
  private boolean bothNumerical(Node leftNode, Node rightNode) {
    return isNumerical(leftNode) && isNumerical(rightNode);
  }

  private boolean bothOfType(Node leftNode, Node rightNode, Type type) {
    ExpressionTypeEvaluator leftEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);
    ExpressionTypeEvaluator rightEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);

    leftNode.apply(leftEv);
    rightNode.apply(rightEv);

    return leftEv.getType().equals(type) && rightEv.getType().equals(type);
  }

  private Type evaluateChildArithmeticUnary(PExpr expr) {
    if (isNumerical(expr)) {
      ExpressionTypeEvaluator exprEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);
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
    // Booleans, floats and ints can be compared, but the type needs to be the same
    if (haveSameType(left, right) || bothNumerical(left, right)) {
      return Type.BOOLEAN;
    }
    return Type.ERROR;
  }

  private boolean haveSameType(Node leftNode, Node rightNode) {
    ExpressionTypeEvaluator leftEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);
    ExpressionTypeEvaluator rightEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);

    leftNode.apply(leftEv);
    rightNode.apply(rightEv);

    return leftEv.getType().equals(rightEv.getType());
  }

  private boolean hasType(Node node, Type type) {
    ExpressionTypeEvaluator exprEv = new ExpressionTypeEvaluator(expressionTypeCache, symbolTable);
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
