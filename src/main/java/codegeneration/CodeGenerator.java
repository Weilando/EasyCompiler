package codegeneration;

import analysis.DepthFirstAdapter;
import lineevaluation.LineEvaluator;
import node.*;
import stackdepthevaluation.StackDepthEvaluator;
import typecheck.SymbolTable;
import typecheck.Type;

import java.util.Arrays;

public class CodeGenerator extends DepthFirstAdapter {
  private final CodeCache cache;
  private final String programName;
  private final SymbolTable symbolTable;
  private int lastCLabel; // "Continue" label
  private int lastHLabel; // "Head" label
  private int lastTLabel; // "True" label

  public CodeGenerator(CodeCache cache, String programName, SymbolTable symbolTable) {
    this.cache = cache;
    this.programName = programName;
    this.symbolTable = symbolTable;
    this.lastCLabel = 0;
    this.lastHLabel = 0;
    this.lastTLabel = 0;
  }


  // Basic structure
  @Override
  public void inAPrg(APrg node) { // pretend to be a java class internally
    String[] fileHead = {
        String.format(".source %s.java", this.programName),
        String.format(".class %s", this.programName),
        ".super java/lang/Object"};
    String[] objectInit = {
        ".method public <init>()V",
        "\taload_0",
        "\tinvokespecial java/lang/Object/<init>()V",
        "\treturn",
        ".end method", ""};
    cache.addLines(Arrays.asList(fileHead));
    cache.addLines(Arrays.asList(objectInit));
  }

  @Override
  public void caseAMain(AMain node) {
    StackDepthEvaluator stackDepthEvaluator = new StackDepthEvaluator();
    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(stackDepthEvaluator);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(stackDepthEvaluator);
    }

    String[] beginMainMethod = {
        ".method public static main([Ljava/lang/String;)V",
        String.format("\t.limit stack %d", stackDepthEvaluator.getMaxDepthCounter()),
        String.format("\t.limit locals %d", symbolTable.countSymbols() + 1), // all type-checked symbols and args[]
        ""};
    String[] endMainMethod = {
        "", "\treturn",
        ".end method"
    };

    cache.addLines(Arrays.asList(beginMainMethod));

    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(this);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(this);
    }

    cache.addLines(Arrays.asList(endMainMethod));
  }


  // Statements
  @Override
  public void caseAIfStat(AIfStat node) {
    addLineNumber(node, "if statement");
    String CLabel = getNewCLabel();

    // generate condition
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", CLabel)); // "Skip" then-body, if condition is false (i.e. equal to 0)

    // generate body
    node.getThenBlock().apply(this);
    cache.addIndentedLine(String.format("%s:", CLabel)); // generate end of body (this is where a failed condition jumps to
  }

  @Override
  public void caseAIfelseStat(AIfelseStat node) {
    addLineNumber(node, "if-else statement");
    String CLabelElse = getNewCLabel();
    String CLabelEnd = getNewCLabel();

    // generate condition
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", CLabelElse)); // "Skip" then-body, if condition is false (i.e. equal to 0)

    // generate then-body
    node.getThenBlock().apply(this);
    cache.addIndentedLine(String.format("goto %s", CLabelEnd));

    // generate else-block
    cache.addIndentedLine(String.format("%s:", CLabelElse));
    node.getElseBlock().apply(this);
    cache.addIndentedLine(String.format("%s:", CLabelEnd));
  }

  @Override
  public void caseAWhileStat(AWhileStat node) {
    addLineNumber(node, "while statement");
    String HLabel = getNewHLabel();
    String CLabel = getNewCLabel();

    // generate head
    cache.addIndentedLine(String.format("%s:", HLabel));
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", CLabel)); // "Skip" while-body, if condition is false (i.e. equal to 0)

    // generate body
    node.getBody().apply(this);
    cache.addIndentedLine(String.format("goto %s", HLabel));
    cache.addIndentedLine(String.format("%s:", CLabel));
  }

  @Override
  public void caseAPrintStat(APrintStat node) {
    addLineNumber(node, "print statement");
    PExpr expr = node.getExpr();
    Type type = expr.getType();

    expr.apply(this); // put expression value on the stack

    String[] preparePrint = {
        "getstatic java/lang/System/out Ljava/io/PrintStream;",
        "swap"};
    cache.addIndentedLines(Arrays.asList(preparePrint));

    if (type.equals(Type.BOOLEAN)) {
      cache.addIndentedLine("invokevirtual java/io/PrintStream/print(Z)V");
    } else if (type.equals(Type.FLOAT)) {
      if (node.getExpr().getType().equals(Type.INT)) {
        cache.addIndentedLine("i2f");
      }
      cache.addIndentedLine("invokevirtual java/io/PrintStream/print(F)V");
    } else if (type.equals(Type.INT)) {
      cache.addIndentedLine("invokevirtual java/io/PrintStream/print(I)V");
    }
  }

  @Override
  public void caseAPrintlnStat(APrintlnStat node) {
    addLineNumber(node, "println statement");
    PExpr expr = node.getExpr();
    Type type = expr.getType();

    expr.apply(this); // put expression value on the stack

    String[] preparePrint = {
        "getstatic java/lang/System/out Ljava/io/PrintStream;",
        "swap"};
    cache.addIndentedLines(Arrays.asList(preparePrint));

    if (type.equals(Type.BOOLEAN)) {
      cache.addIndentedLine("invokevirtual java/io/PrintStream/println(Z)V");
    } else if (type.equals(Type.FLOAT)) {
      if (node.getExpr().getType().equals(Type.INT)) {
        cache.addIndentedLine("i2f");
      }
      cache.addIndentedLine("invokevirtual java/io/PrintStream/println(F)V");
    } else if (type.equals(Type.INT)) {
      cache.addIndentedLine("invokevirtual java/io/PrintStream/println(I)V");
    }
  }


  // Variable statements
  @Override
  public void inADeclStat(ADeclStat node) {
    addLineNumber(node, "declaration statement");
  }

  @Override
  public void inAInitStat(AInitStat node) {
    addLineNumber(node, "init statement");
  }

  @Override
  public void outAInitStat(AInitStat node) {
    String id = node.getId().getText();
    generateAssignCode(id, node.getExpr().getType().equals(Type.INT));
  }

  @Override
  public void inAAssignStat(AAssignStat node) {
    addLineNumber(node, "assign statement");
  }

  @Override
  public void outAAssignStat(AAssignStat node) {
    String id = node.getId().getText();
    generateAssignCode(id, node.getExpr().getType().equals(Type.INT));
  }


  // Arithmetic operations
  @Override
  public void caseAAddExpr(AAddExpr node) {
    if (node.getType().equals(Type.FLOAT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), true);
      cache.addIndentedLine("fadd");
    } else if (node.getType().equals(Type.INT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), false);
      cache.addIndentedLine("iadd");
    }
  }

  @Override
  public void caseASubExpr(ASubExpr node) {
    if (node.getType().equals(Type.FLOAT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), true);
      cache.addIndentedLine("fsub");
    } else if (node.getType().equals(Type.INT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), false);
      cache.addIndentedLine("isub");
    }
  }

  @Override
  public void caseAMulExpr(AMulExpr node) {
    if (node.getType().equals(Type.FLOAT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), true);
      cache.addIndentedLine("fmul");
    } else if (node.getType().equals(Type.INT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), false);
      cache.addIndentedLine("imul");
    }
  }

  @Override
  public void caseADivExpr(ADivExpr node) {
    if (node.getType().equals(Type.FLOAT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), true);
      cache.addIndentedLine("fdiv");
    } else if (node.getType().equals(Type.INT)) {
      generateArithmeticChildren(node.getLeft(), node.getRight(), false);
      cache.addIndentedLine("idiv");
    }
  }

  @Override
  public void outAModExpr(AModExpr node) {
    cache.addIndentedLine("irem");
  }


  // Boolean operations
  @Override
  public void outAAndExpr(AAndExpr node) {
    cache.addIndentedLine("iand"); // logic and acts behaves multiplication
  }

  @Override
  public void outAOrExpr(AOrExpr node) {
    cache.addIndentedLine("ior");
  }


  // Unary operations
  // Unary plus has no effect and does not need to be processed.
  @Override
  public void outAUminusExpr(AUminusExpr node) {
    if (node.getType().equals(Type.FLOAT)) {
      if (node.getExpr().getType().equals(Type.INT)) {
        cache.addIndentedLine("i2f");
      }
      cache.addIndentedLine("fneg");
    } else if (node.getType().equals(Type.INT)) {
      cache.addIndentedLine("ineg");
    }
  }

  @Override
  public void outANotExpr(ANotExpr node) {
    String[] lines = {"ldc 1", "iadd", "ldc 2", "irem"}; // logic not behaves like adding 1 mod 2
    cache.addIndentedLines(Arrays.asList(lines));
  }


  // Comparison expressions (calculate a boolean value with true=1, false=0)
  @Override
  public void outAEqExpr(AEqExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("ifeq")));
  }

  @Override
  public void outANeqExpr(ANeqExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("ifne")));
  }

  @Override
  public void outALteqExpr(ALteqExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("ifle")));
  }

  @Override
  public void outALtExpr(ALtExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("iflt")));
  }

  @Override
  public void outAGteqExpr(AGteqExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("ifge")));
  }

  @Override
  public void outAGtExpr(AGtExpr node) {
    cache.addIndentedLines(Arrays.asList(getComparisonCode("ifgt")));
  }


  // Literal expressions
  @Override
  public void outABooleanExpr(ABooleanExpr node) {
    String value = node.getLit().getText();

    if (value.equals("true")) {
      cache.addIndentedLine("ldc 1");
    } else {
      cache.addIndentedLine("ldc 0");
    }
  }

  @Override
  public void outAFloatExpr(AFloatExpr node) {
    cache.addIndentedLine(String.format("ldc %s", node.getLit().getText()));
  }

  @Override
  public void outAIdExpr(AIdExpr node) {
    String id = node.getId().getText();
    int varNumber = symbolTable.getVariableNumber(id);

    if (this.symbolTable.getType(id).equals(Type.FLOAT)) {
      cache.addIndentedLine(String.format("fload %s", varNumber));
    } else { // boolean and int are represented as int
      cache.addIndentedLine(String.format("iload %s", varNumber));
    }
  }

  @Override
  public void outAIntExpr(AIntExpr node) {
    cache.addIndentedLine(String.format("ldc %s", node.getLit().getText()));
  }


  // Helpers
  String getNewCLabel() {
    return String.format("C%d", ++this.lastCLabel);
  }

  String getNewTLabel() {
    return String.format("T%d", ++this.lastTLabel);
  }

  String getNewHLabel() {
    return String.format("H%d", ++this.lastHLabel);
  }

  String[] getComparisonCode(String operand) {
    String CLabel = getNewCLabel();
    String TLabel = getNewTLabel();
    return new String[]{
        "isub",
        String.format("%s %s", operand, TLabel),
        "ldc 0",
        String.format("goto %s", CLabel),
        String.format("%s:", TLabel),
        "ldc 1",
        String.format("%s:", CLabel)};
  }

  void generateAssignCode(String id, boolean castInt) {
    int varNumber = symbolTable.getVariableNumber(id);

    if (this.symbolTable.getType(id).equals(Type.FLOAT)) {
      if (castInt) {
        cache.addIndentedLine("i2f");
      }
      cache.addIndentedLine(String.format("fstore %s", varNumber));
    } else { // boolean and int are represented as int
      cache.addIndentedLine(String.format("istore %s", varNumber));
    }
  }

  void generateArithmeticChildren(PExpr left, PExpr right, boolean cast) {
    left.apply(this);
    if (cast && left.getType().equals(Type.INT)) {
      cache.addIndentedLine("i2f");
    }
    right.apply(this);
    if (cast && right.getType().equals(Type.INT)) {
      cache.addIndentedLine("i2f");
    }
  }

  void addLineNumber(Node node, String description) {
    cache.addIndentedLine(String.format(".line %d ; %s", LineEvaluator.getLine(node), description));
  }
}
