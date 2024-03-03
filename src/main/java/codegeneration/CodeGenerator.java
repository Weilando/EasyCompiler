package codegeneration;

import analysis.DepthFirstAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lineevaluation.LineEvaluator;
import node.AAddExpr;
import node.AAndExpr;
import node.AAssignStat;
import node.ABooleanExpr;
import node.AConcatExpr;
import node.ADeclStat;
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
import node.APrg;
import node.APrintStat;
import node.APrintlnStat;
import node.AReturnStat;
import node.AStringExpr;
import node.ASubExpr;
import node.AUminusExpr;
import node.AWhileStat;
import node.Node;
import node.PExpr;
import node.PStat;
import node.TIdentifier;
import stackdepthevaluation.StackDepthEvaluator;
import symboltable.FunctionArgumentTypeList;
import symboltable.SymbolTable;
import symboltable.Type;

/** The code generator walks the AST and generates Jasmin code. */
public class CodeGenerator extends DepthFirstAdapter {
  private final CodeCache cache;
  private final String programName;
  private final SymbolTable symbolTable;
  private final LineEvaluator lineEvaluator;
  private int lastContinueLabel;
  private int lastHeadLabel;
  private int lastTrueLabel;

  /**
   * The CodeGenerator walks the AST using Depth First Search and emits Jasmin
   * assembly code.
   *
   * @param programName   Name of the program, used for source and class name
   * @param symbolTable   Filled symbol table
   * @param lineEvaluator Line evaluator that was applied to the AST
   */
  public CodeGenerator(
      String programName, SymbolTable symbolTable, LineEvaluator lineEvaluator) {
    this.cache = new CodeCache();
    this.programName = programName;
    this.symbolTable = symbolTable;
    this.lineEvaluator = lineEvaluator;
    this.lastContinueLabel = 0;
    this.lastHeadLabel = 0;
    this.lastTrueLabel = 0;
  }

  // Basic structure and function definitions
  @Override
  public void inAPrg(APrg node) { // pretend to be a java class internally
    String[] fileHead = {
        String.format(".source %s.java", this.programName),
        String.format(".class %s", this.programName),
        ".super java/lang/Object" };
    String[] objectInit = {
        ".method public <init>()V",
        "\taload_0",
        "\tinvokespecial java/lang/Object/<init>()V",
        "\treturn",
        ".end method", "" };
    cache.addLines(Arrays.asList(fileHead));
    cache.addLines(Arrays.asList(objectInit));
  }

  @Override
  public void caseAMain(AMain node) {
    StackDepthEvaluator stackDepthEvaluator = new StackDepthEvaluator();
    node.apply(stackDepthEvaluator);

    final String[] beginMainFunction = {
        ".method public static main([Ljava/lang/String;)V",
        String.format("\t.limit stack %d", stackDepthEvaluator.getMaxDepthCounter()),
        String.format("\t.limit locals %d",
            symbolTable.countSymbolsInScope("main") + 1), // all type-checked symbols and args[]
        "" };
    final String[] endMainFunction = {
        "", "\treturn", ".end method", ""
    };

    cache.addLines(Arrays.asList(beginMainFunction));

    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(this);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(this);
    }

    cache.addLines(Arrays.asList(endMainFunction));
  }

  @Override
  public void caseAFunc(AFunc node) {
    StackDepthEvaluator stackDepthEvaluator = new StackDepthEvaluator();
    node.apply(stackDepthEvaluator);
    TIdentifier functionId = node.getId();
    String functionName = functionId.getText();
    Type returnType = symbolTable.getFunctionReturnType(functionName);
    String returnJvmType = returnType.getJvmType();
    FunctionArgumentTypeList argumentTypeList = symbolTable.getFunctionArgumentTypes(functionName);
    String argumentJvmTypeString = argumentTypeList.getJvmTypeString();

    final String[] beginFunction = {
        ".method public static %s(%s)%s".formatted(
            functionName, argumentJvmTypeString, returnJvmType),
        "\t.limit stack %d".formatted(stackDepthEvaluator.getMaxDepthCounter()),
        "\t.limit locals %d".formatted(symbolTable.countSymbolsInScope(functionName)),
        "" };
    final String[] endFunction = {
        "", "\treturn", ".end method", ""
    };

    cache.addLines(Arrays.asList(beginFunction));

    for (PStat currDecl : node.getDeclarations()) {
      currDecl.apply(this);
    }
    for (PStat currStat : node.getStatements()) {
      currStat.apply(this);
    }

    cache.addLines(Arrays.asList(endFunction));
  }

  // Statements
  @Override
  public void caseAIfStat(AIfStat node) {
    addLineNumber(node, "if statement");
    final String continueLabel = getNewContinueLabel();

    // generate condition and skip then-body if false (i.e., equal to 0)
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", continueLabel));

    // generate body (failed condition jumps to end of body)
    node.getThenBlock().apply(this);
    cache.addIndentedLine(String.format("%s:", continueLabel));
  }

  @Override
  public void caseAIfelseStat(AIfelseStat node) {
    addLineNumber(node, "if-else statement");
    final String continueLabelElse = getNewContinueLabel();
    final String continueLabelEnd = getNewContinueLabel();

    // generate condition and skip then-body if false (i.e., equal to 0)
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", continueLabelElse));

    // generate then-body
    node.getThenBlock().apply(this);
    cache.addIndentedLine(String.format("goto %s", continueLabelEnd));

    // generate else-block
    cache.addIndentedLine(String.format("%s:", continueLabelElse));
    node.getElseBlock().apply(this);
    cache.addIndentedLine(String.format("%s:", continueLabelEnd));
  }

  @Override
  public void caseAWhileStat(AWhileStat node) {
    addLineNumber(node, "while statement");
    final String headLabel = getNewHeadLabel();
    final String continueLabel = getNewContinueLabel();

    // generate head and skip while-body if false (i.e., equal to 0)
    cache.addIndentedLine(String.format("%s:", headLabel));
    node.getExpr().apply(this);
    cache.addIndentedLine(String.format("ifeq %s", continueLabel));

    // generate body
    node.getBody().apply(this);
    cache.addIndentedLine(String.format("goto %s", headLabel));
    cache.addIndentedLine(String.format("%s:", continueLabel));
  }

  @Override
  public void caseAFuncStat(AFuncStat node) {
    addLineNumber(node, "function call");

    TIdentifier functionId = node.getId();
    String functionName = functionId.getText();

    Type returnType = symbolTable.getFunctionReturnType(functionName);
    String returnJvmType = returnType.getJvmType();

    FunctionArgumentTypeList argumentTypeList = symbolTable.getFunctionArgumentTypes(functionName);
    String argumentJvmTypeString = argumentTypeList.getJvmTypeString();

    for (PExpr currArg : node.getArgs()) { // put arguments on the stack
      currArg.apply(this);
    }

    final String functionCall = "invokestatic %s/%s(%s)%s".formatted(
        this.programName, functionName, argumentJvmTypeString, returnJvmType);
    cache.addIndentedLine(functionCall);
    if (!returnType.equals(Type.NONE)) {
      cache.addIndentedLine("pop"); // ignore return value
    }
  }

  @Override
  public void caseAFuncExpr(AFuncExpr node) {
    addLineNumber(node, "function expression");

    TIdentifier functionId = node.getId();
    String functionName = functionId.getText();

    Type returnType = symbolTable.getFunctionReturnType(functionName);
    String returnJvmType = returnType.getJvmType();

    FunctionArgumentTypeList argumentTypeList = symbolTable.getFunctionArgumentTypes(functionName);
    String argumentJvmTypeString = argumentTypeList.getJvmTypeString();

    for (PExpr currArg : node.getArgs()) { // put arguments on the stack
      currArg.apply(this);
    }

    final String functionCall = "invokestatic %s/%s(%s)%s".formatted(
        this.programName, functionName, argumentJvmTypeString, returnJvmType);
    cache.addIndentedLine(functionCall);
  }

  @Override
  public void caseAReturnStat(AReturnStat node) {
    addLineNumber(node, "return statement");

    PExpr expr = node.getExpr();
    String scopeName = symbolTable.determineScope(node);
    Type returnType = symbolTable.getFunctionReturnType(scopeName);

    if (expr != null) { // put return value on the stack
      expr.apply(this);
    }

    switch (returnType) {
      case BOOLEAN:
      case INT:
        cache.addIndentedLine("ireturn");
        break;
      case FLOAT:
        cache.addIndentedLine("freturn");
        break;
      case STRING:
        cache.addIndentedLine("areturn");
        break;
      default:
        cache.addIndentedLine("return");
    }
  }

  @Override
  public void caseAPrintStat(APrintStat node) {
    addLineNumber(node, "print statement");
    PExpr expr = node.getExpr();
    Type type = expr.getType();

    expr.apply(this); // put expression value on the stack

    String[] preparePrint = {
        "getstatic java/lang/System/out Ljava/io/PrintStream;",
        "swap" };
    cache.addIndentedLines(Arrays.asList(preparePrint));

    if (type.equals(Type.FLOAT) && node.getExpr().getType().equals(Type.INT)) {
      cache.addIndentedLine("i2f");
    }

    String printCommand = String.format(
        "invokevirtual java/io/PrintStream/print(%s)V",
        type.getJvmType());
    cache.addIndentedLine(printCommand);
  }

  @Override
  public void caseAPrintlnStat(APrintlnStat node) {
    addLineNumber(node, "println statement");
    PExpr expr = node.getExpr();
    Type type = expr.getType();

    expr.apply(this); // put expression value on the stack

    String[] preparePrint = {
        "getstatic java/lang/System/out Ljava/io/PrintStream;",
        "swap" };
    cache.addIndentedLines(Arrays.asList(preparePrint));

    if (type.equals(Type.FLOAT) && node.getExpr().getType().equals(Type.INT)) {
      cache.addIndentedLine("i2f");
    }

    String printCommand = String.format(
        "invokevirtual java/io/PrintStream/println(%s)V",
        type.getJvmType());
    cache.addIndentedLine(printCommand);
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
    String scopeName = symbolTable.determineScope(node);
    generateAssignCode(scopeName, id, node.getExpr().getType().equals(Type.INT));
  }

  @Override
  public void inAAssignStat(AAssignStat node) {
    addLineNumber(node, "assign statement");
  }

  @Override
  public void outAAssignStat(AAssignStat node) {
    String id = node.getId().getText();
    String scopeName = symbolTable.determineScope(node);
    generateAssignCode(scopeName, id, node.getExpr().getType().equals(Type.INT));
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
    cache.addIndentedLine("iand"); // logic of "and" behaves like multiplication
  }

  @Override
  public void outAOrExpr(AOrExpr node) {
    cache.addIndentedLine("ior");
  }

  // String operations
  @Override
  public void inAConcatExpr(AConcatExpr node) {
    List<String> stringBufferInit = Arrays.asList(
        "new java/lang/StringBuffer  ; concat",
        "dup",
        "invokespecial java/lang/StringBuffer/<init>()V");
    cache.addIndentedLines(stringBufferInit);
  }

  private String getAppendCommand(PExpr node) {
    /* Get append command for a StringBuilder using the correct type. */
    String baseCommand = "invokevirtual java/lang/StringBuffer/append(%s)Ljava/lang/StringBuffer;";
    String jvmType = node.getType().getJvmType();
    return baseCommand.formatted(jvmType);
  }

  @Override
  public void caseAConcatExpr(AConcatExpr node) {
    inAConcatExpr(node);
    if (node.getLeft() != null) {
      node.getLeft().apply(this);
    }
    cache.addIndentedLine(getAppendCommand(node.getLeft()));
    if (node.getRight() != null) {
      node.getRight().apply(this);
    }
    cache.addIndentedLine(getAppendCommand(node.getRight()));
    outAConcatExpr(node);
  }

  @Override
  public void outAConcatExpr(AConcatExpr node) {
    cache.addIndentedLine("invokevirtual java/lang/StringBuffer/toString()Ljava/lang/String;");
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
    // logic of "not" behaves like adding 1 mod 2
    String[] lines = { "ldc 1", "iadd", "ldc 2", "irem" };
    cache.addIndentedLines(Arrays.asList(lines));
  }

  // Comparison expressions (calculate a boolean value with true=1, false=0)
  @Override
  public void outAEqExpr(AEqExpr node) {
    if (node.getLeft().getType().equals(Type.STRING)) {
      cache.addIndentedLine("invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z");
    } else {
      cache.addIndentedLines(Arrays.asList(getComparisonCode("ifeq")));
    }
  }

  @Override
  public void outANeqExpr(ANeqExpr node) {
    if (node.getLeft().getType().equals(Type.STRING)) {
      cache.addIndentedLine("invokevirtual java/lang/String/equals(Ljava/lang/Object;)Z");

      // negate
      final String trueLabel = getNewTrueLabel();
      final String continueLabel = getNewContinueLabel();
      List<String> negationCode = Arrays.asList(
          String.format("ifeq %s", trueLabel),
          "ldc 0",
          String.format("goto %s", continueLabel),
          String.format("%s:", trueLabel),
          "ldc 1",
          String.format("%s:", continueLabel));
      cache.addIndentedLines(negationCode);
    } else {
      cache.addIndentedLines(Arrays.asList(getComparisonCode("ifne")));
    }
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
    String scopeName = symbolTable.determineScope(node);
    int varNumber = symbolTable.getVariableNumber(scopeName, id);
    String loadCommand = "";

    switch (this.symbolTable.getSymbolType(scopeName, id)) {
      case FLOAT:
        loadCommand = "fload";
        break;
      case STRING:
        loadCommand = "aload";
        break;
      default: // boolean and int are stored as int
        loadCommand = "iload";
    }

    cache.addIndentedLine(String.format("%s %s", loadCommand, varNumber));
  }

  @Override
  public void outAIntExpr(AIntExpr node) {
    cache.addIndentedLine(String.format("ldc %s", node.getLit().getText()));
  }

  @Override
  public void outAStringExpr(AStringExpr node) {
    cache.addIndentedLine(String.format("ldc %s", node.getLit().getText()));
  }

  // Helpers
  String getNewContinueLabel() {
    /* Generates a unique label to continue after assembly control structures. */
    return String.format("C%d", ++this.lastContinueLabel);
  }

  String getNewTrueLabel() {
    /* Generates a unique label for true cases in assembly instructions. */
    return String.format("T%d", ++this.lastTrueLabel);
  }

  String getNewHeadLabel() {
    /* Generates a unique head label for assembly instructions. */
    return String.format("H%d", ++this.lastHeadLabel);
  }

  String[] getComparisonCode(String operand) {
    /* Generates comparison code for boolean, float and int. */
    final String continueLabel = getNewContinueLabel();
    final String trueLabel = getNewTrueLabel();
    return new String[] {
        "isub",
        String.format("%s %s", operand, trueLabel),
        "ldc 0",
        String.format("goto %s", continueLabel),
        String.format("%s:", trueLabel),
        "ldc 1",
        String.format("%s:", continueLabel) };
  }

  void generateAssignCode(String scopeName, String id, boolean castInt) {
    int varNumber = symbolTable.getVariableNumber(scopeName, id);
    String storeCommand = "";

    switch (this.symbolTable.getSymbolType(scopeName, id)) {
      case FLOAT:
        if (castInt) {
          cache.addIndentedLine("i2f");
        }
        storeCommand = "fstore";
        break;
      case STRING:
        storeCommand = "astore";
        break;
      default: // boolean and int are stored as int
        storeCommand = "istore";
    }

    cache.addIndentedLine(String.format("%s %s", storeCommand, varNumber));
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
    cache.addIndentedLine(".line %d ; %s".formatted(this.lineEvaluator.getLine(node), description));
  }

  public ArrayList<String> getCode() {
    return this.cache.getCode();
  }
}
