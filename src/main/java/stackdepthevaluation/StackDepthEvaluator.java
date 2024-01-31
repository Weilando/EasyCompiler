package stackdepthevaluation;

import analysis.DepthFirstAdapter;
import node.AAddExpr;
import node.AAndExpr;
import node.AAssignStat;
import node.ABooleanExpr;
import node.AConcatExpr;
import node.ADeclArg;
import node.ADivExpr;
import node.AEqExpr;
import node.AFloatExpr;
import node.AFunc;
import node.AFuncExpr;
import node.AFuncStat;
import node.AGtExpr;
import node.AGteqExpr;
import node.AIdExpr;
import node.AIfStat;
import node.AIfelseStat;
import node.AInitStat;
import node.AIntExpr;
import node.ALtExpr;
import node.ALteqExpr;
import node.AMain;
import node.AModExpr;
import node.AMulExpr;
import node.ANeqExpr;
import node.ANotExpr;
import node.AOrExpr;
import node.APrintStat;
import node.APrintlnStat;
import node.AReturnStat;
import node.AStringExpr;
import node.ASubExpr;
import node.AUminusExpr;
import node.AWhileStat;
import node.PExpr;
import node.PStat;
import symboltable.Type;

/**
 * Depth first walker for the AST which determines the stack depth for
 * functions. Applicable to all nodes, but intended for function definitions.
 */
public class StackDepthEvaluator extends DepthFirstAdapter {
  private int depthCounter = 0;
  private int maxDepthCounter = 0;

  // Basic structure
  // no stack-operations for program/class

  @Override
  public void caseAMain(AMain node) {
    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(this);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(this);
    }
  }

  @Override
  public void caseAFunc(AFunc node) {
    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(this);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(this);
    }
  }

  @Override
  public void caseADeclArg(ADeclArg node) {
    incrementDepthCounter(1); // each argument is pushed on the stack
  }

  @Override
  public void caseAReturnStat(AReturnStat node) {
    PExpr expr = node.getExpr();
    Type type = expr.getType();
    if (!type.equals(Type.NONE)) {
      incrementDepthCounter(1); // return value is pushed on the stack
    }
  }

  @Override
  public void caseAFuncExpr(AFuncExpr node) {
    int numberOfArgs = node.getArgs().size();
    incrementDepthCounter(numberOfArgs); // parameters are pushed on the stack
    decrementDepthCounter(numberOfArgs);

    Type type = node.getType();
    if (!type.equals(Type.NONE)) {
      incrementDepthCounter(1); // return value is pushed on the stack
    }
  }

  @Override
  public void caseAFuncStat(AFuncStat node) {
    int numberOfArgs = node.getArgs().size();
    incrementDepthCounter(numberOfArgs); // parameters are pushed on the stack
    decrementDepthCounter(numberOfArgs);
    incrementDepthCounter(1); // return value is pushed on the stack
    decrementDepthCounter(1); // return value popped
  }

  // Statements
  @Override
  public void caseAIfStat(AIfStat node) {
    // evaluate condition
    node.getExpr().apply(this);
    decrementDepthCounter(1); // ifeq pops 1

    // evaluate body
    node.getThenBlock().apply(this);
  }

  @Override
  public void caseAIfelseStat(AIfelseStat node) {
    // evaluate condition
    node.getExpr().apply(this);
    decrementDepthCounter(1); // ifeq pops 1

    // evaluate then-body
    node.getThenBlock().apply(this);

    // evaluate else-block
    node.getElseBlock().apply(this);
  }

  @Override
  public void caseAWhileStat(AWhileStat node) {
    // evaluate head
    node.getExpr().apply(this);
    decrementDepthCounter(1); // ifeq pops 1

    // evaluate body
    node.getBody().apply(this);
  }

  @Override
  public void caseAPrintStat(APrintStat node) {
    // Evaluate expression
    PExpr expr = node.getExpr();
    expr.apply(this);

    incrementDepthCounter(1); // getstatic pushes 1
    decrementDepthCounter(2); // invokevirtual pops 2
  }

  @Override
  public void caseAPrintlnStat(APrintlnStat node) {
    // Evaluate expression
    PExpr expr = node.getExpr();
    expr.apply(this);

    incrementDepthCounter(1); // getstatic pushes 1
    decrementDepthCounter(2); // invokevirtual pops 2
  }

  // Variable statements
  // no stack-operations for declaration

  @Override
  public void outAInitStat(AInitStat node) {
    decrementDepthCounter(1); // istore pops 1
  }

  @Override
  public void outAAssignStat(AAssignStat node) {
    decrementDepthCounter(1); // istore pops 1
  }

  // Arithmetic operations
  @Override
  public void outAAddExpr(AAddExpr node) {
    decrementDepthCounter(1); // iadd pops 2 arguments and pushes 1 result
  }

  @Override
  public void outASubExpr(ASubExpr node) {
    decrementDepthCounter(1); // isub pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAMulExpr(AMulExpr node) {
    decrementDepthCounter(1); // imul pops 2 arguments and pushes 1 result
  }

  @Override
  public void outADivExpr(ADivExpr node) {
    decrementDepthCounter(1); // idiv pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAModExpr(AModExpr node) {
    decrementDepthCounter(1); // irem pops 2 arguments and pushes 1 result
  }

  // Boolean operations
  @Override
  public void outAAndExpr(AAndExpr node) {
    decrementDepthCounter(1); // iand pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAOrExpr(AOrExpr node) {
    decrementDepthCounter(1); // ior pops 2 arguments and pushes 1 result
  }

  // Unary operations
  // Unary plus has no effect and does not need to be processed.
  @Override
  public void outAUminusExpr(AUminusExpr node) {
    incrementDepthCounter(1); // ldc -1 pushes 1
    decrementDepthCounter(1); // imul pops 2 arguments and pushes 1 result
  }

  @Override
  public void outANotExpr(ANotExpr node) {
    incrementDepthCounter(1); // ldc pushes 1
    decrementDepthCounter(1); // iadd pops 2 arguments and pushes 1 result
    // same pattern for ldc and irem
  }

  // Comparison expressions (calculate a boolean value with true=1, false=0)
  @Override
  public void outAEqExpr(AEqExpr node) {
    decrementDepthCounter(1); // ifeq pops 1
  }

  @Override
  public void outANeqExpr(ANeqExpr node) {
    decrementDepthCounter(1); // ifne pops 1
  }

  @Override
  public void outALteqExpr(ALteqExpr node) {
    decrementDepthCounter(1); // ifle pops 1
  }

  @Override
  public void outALtExpr(ALtExpr node) {
    decrementDepthCounter(1); // iflt pops 1
  }

  @Override
  public void outAGteqExpr(AGteqExpr node) {
    decrementDepthCounter(1); // ifge pops 1
  }

  @Override
  public void outAGtExpr(AGtExpr node) {
    decrementDepthCounter(1); // ifgt pops 1
  }

  // String operations
  @Override
  public void inAConcatExpr(AConcatExpr node) {
    incrementDepthCounter(2); // new StringBuffer and dup each push 1
    decrementDepthCounter(1); // invokespecial <init> pops 1
  }

  @Override
  public void caseAConcatExpr(AConcatExpr node) {
    inAConcatExpr(node);
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    // invokevirtual append pops 1 and pushes 1
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    // invokevirtual append pops 1 and pushes 1
    outAConcatExpr(node);
  }

  @Override
  public void outAConcatExpr(AConcatExpr node) {
    decrementDepthCounter(1); // invokevirtual toString pops 1
  }

  // Literal expressions
  @Override
  public void outABooleanExpr(ABooleanExpr node) {
    incrementDepthCounter(1); // ldc pushes 1
  }

  @Override
  public void outAFloatExpr(AFloatExpr node) {
    incrementDepthCounter(1); // ldc pushes 1
  }

  @Override
  public void outAIntExpr(AIntExpr node) {
    incrementDepthCounter(1); // ldc pushes 1
  }

  @Override
  public void outAStringExpr(AStringExpr node) {
    incrementDepthCounter(1); // ldc pushes 1
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    incrementDepthCounter(1); // iload/fload pushes 1
  }

  // Counter-logic
  public int getMaxDepthCounter() {
    return this.maxDepthCounter;
  }

  private void incrementDepthCounter(int by) {
    this.depthCounter += by;
    if (this.depthCounter > this.maxDepthCounter) {
      this.maxDepthCounter = this.depthCounter;
    }
  }

  private void decrementDepthCounter(int by) {
    this.depthCounter -= by;
  }
}
