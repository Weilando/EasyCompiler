package stackdepthevaluation;

import analysis.DepthFirstAdapter;
import node.*;

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

  // Statements
  @Override
  public void caseAIfStat(AIfStat node) {
    // evaluate condition
    node.getExpr().apply(this);
    decrementDepthCounter(); // ifeq pops 1

    // evaluate body
    node.getThenBlock().apply(this);
  }

  @Override
  public void caseAIfelseStat(AIfelseStat node) {
    // evaluate condition
    node.getExpr().apply(this);
    decrementDepthCounter(); // ifeq pops 1

    // evaluate then-body
    node.getThenBlock().apply(this);

    // evaluate else-block
    node.getElseBlock().apply(this);
  }

  @Override
  public void caseAWhileStat(AWhileStat node) {
    // evaluate head
    node.getExpr().apply(this);
    decrementDepthCounter(); // ifeq pops 1

    // evaluate body
    node.getBody().apply(this);
  }

  @Override
  public void caseAPrintStat(APrintStat node) {
    // Evaluate expression
    PExpr expr = node.getExpr();
    expr.apply(this);

    incrementDepthCounter(); // getstatic pushes 1
    decrementDepthCounter(); // invokevirtual pops 2
    decrementDepthCounter();
  }

  @Override
  public void caseAPrintlnStat(APrintlnStat node) {
    // Evaluate expression
    PExpr expr = node.getExpr();
    expr.apply(this);

    incrementDepthCounter(); // getstatic pushes 1
    decrementDepthCounter(); // invokevirtual pops 2
    decrementDepthCounter();
  }

  // Variable statements
  // no stack-operations for declaration

  @Override
  public void outAInitStat(AInitStat node) {
    decrementDepthCounter(); // istore pops 1
  }

  @Override
  public void outAAssignStat(AAssignStat node) {
    decrementDepthCounter(); // istore pops 1
  }

  // Arithmetic operations
  @Override
  public void outAAddExpr(AAddExpr node) {
    decrementDepthCounter(); // iadd pops 2 arguments and pushes 1 result
  }

  @Override
  public void outASubExpr(ASubExpr node) {
    decrementDepthCounter(); // isub pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAMulExpr(AMulExpr node) {
    decrementDepthCounter(); // imul pops 2 arguments and pushes 1 result
  }

  @Override
  public void outADivExpr(ADivExpr node) {
    decrementDepthCounter(); // idiv pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAModExpr(AModExpr node) {
    decrementDepthCounter(); // irem pops 2 arguments and pushes 1 result
  }

  // Boolean operations
  @Override
  public void outAAndExpr(AAndExpr node) {
    decrementDepthCounter(); // iand pops 2 arguments and pushes 1 result
  }

  @Override
  public void outAOrExpr(AOrExpr node) {
    decrementDepthCounter(); // ior pops 2 arguments and pushes 1 result
  }

  // Unary operations
  // Unary plus has no effect and does not need to be processed.
  @Override
  public void outAUminusExpr(AUminusExpr node) {
    incrementDepthCounter(); // ldc -1 pushes 1
    decrementDepthCounter(); // imul pops 2 arguments and pushes 1 result
  }

  @Override
  public void outANotExpr(ANotExpr node) {
    incrementDepthCounter(); // ldc pushes 1
    decrementDepthCounter(); // iadd pops 2 arguments and pushes 1 result
    incrementDepthCounter(); // ldc pushes 1
    decrementDepthCounter(); // irem pops 2 arguments and pushes 1 result
  }

  // Comparison expressions (calculate a boolean value with true=1, false=0)
  @Override
  public void outAEqExpr(AEqExpr node) {
    decrementDepthCounter(); // ifeq pops 1
  }

  @Override
  public void outANeqExpr(ANeqExpr node) {
    decrementDepthCounter(); // ifne pops 1
  }

  @Override
  public void outALteqExpr(ALteqExpr node) {
    decrementDepthCounter(); // ifle pops 1
  }

  @Override
  public void outALtExpr(ALtExpr node) {
    decrementDepthCounter(); // iflt pops 1
  }

  @Override
  public void outAGteqExpr(AGteqExpr node) {
    decrementDepthCounter(); // ifge pops 1
  }

  @Override
  public void outAGtExpr(AGtExpr node) {
    decrementDepthCounter(); // ifgt pops 1
  }

  // Literal expressions
  @Override
  public void outABooleanExpr(ABooleanExpr node) {
    incrementDepthCounter(); // ldc pushes 1
  }

  @Override
  public void outAFloatExpr(AFloatExpr node) {
    incrementDepthCounter(); // ldc pushes 1
  }

  @Override
  public void outAIntExpr(AIntExpr node) {
    incrementDepthCounter(); // ldc pushes 1
  }

  @Override
  public void outAStringExpr(AStringExpr node) {
    incrementDepthCounter(); // ldc pushes 1
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    incrementDepthCounter(); // iload/fload pushes 1
  }

  // Counter-logic
  public int getMaxDepthCounter() {
    return this.maxDepthCounter;
  }

  private void incrementDepthCounter() {
    this.depthCounter++;
    if (this.depthCounter > this.maxDepthCounter) {
      this.maxDepthCounter++;
    }
  }

  private void decrementDepthCounter() {
    this.depthCounter--;
  }
}
