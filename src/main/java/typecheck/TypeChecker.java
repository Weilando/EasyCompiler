package typecheck;

import analysis.DepthFirstAdapter;
import node.*;

public class TypeChecker extends DepthFirstAdapter {
  final private TypeErrorHandler errorHandler;
  final private ExpressionCache expressionCache;
  final private SymbolTable symbolTable;

  public TypeChecker() {
    this.errorHandler = new TypeErrorHandler();
    this.expressionCache = new ExpressionCache();
    this.symbolTable = new SymbolTable();
  }

  // Variable statements
  public void outAInitStat(AInitStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();
    String varType = "";
    if (node.getType() instanceof AIntTp) {
      varType = "int";
    } else if (node.getType() instanceof ABooleanTp) {
      varType = "boolean";
    } else { // Should never happen
      errorHandler.throwInternalError(id);
    }

    if (symbolTable.contains(key)) {
      errorHandler.throwAlreadyDefinedError(id);
    } else {
      symbolTable.add(key, varType);
    }

    String exprType = evaluateType(node.getExpr());

    if (exprType.equals("error")) {
      errorHandler.throwFlawedExpressionError(id);
    } else if (!exprType.equals(varType)) {
      errorHandler.throwIncompatibleError(id, varType, exprType);
    }
  }

  public void outADeclStat(ADeclStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();
    String type = "";
    if (node.getType() instanceof AIntTp) {
      type = "int";
    } else if (node.getType() instanceof ABooleanTp) {
      type = "boolean";
    }

    if (symbolTable.contains(key)) {
      errorHandler.throwAlreadyDefinedError(id);
    } else {
      symbolTable.add(key, type);
    }
  }

  public void outAAssignStat(AAssignStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();

    if (!symbolTable.contains(key)) {
      errorHandler.throwNotDeclaredError(id);
      return;
    }

    String varType = symbolTable.getType(key);
    String exprType = evaluateType(node.getExpr());

    if (exprType.equals("error")) {
      errorHandler.throwFlawedExpressionError(id);
    } else if (!exprType.equals(varType)) {
      errorHandler.throwIncompatibleError(id, varType, exprType);
    }
  }


  // Control statements
  @Override
  public void outAWhileStat(AWhileStat node) {
    String headType = evaluateType(node.getExpr());
    if (!headType.equals("boolean")) {
      errorHandler.throwConditionError(node, "while", headType);
    }
  }

  @Override
  public void outAIfStat(AIfStat node) {
    String headType = evaluateType(node.getExpr());
    if (!headType.equals("boolean")) {
      errorHandler.throwConditionError(node, "if", headType);
    }
  }

  @Override
  public void outAIfelseStat(AIfelseStat node) {
    String headType = evaluateType(node.getExpr());
    if (!headType.equals("boolean")) {
      errorHandler.throwConditionError(node, "if", headType);
    }
  }


  // Print statement
  @Override
  public void outAPrintStat(APrintStat node) {
    PExpr expr = node.getExpr();
    String contentType = evaluateType(expr);
    if (!(contentType.equals("boolean") || contentType.equals("int"))) {
      errorHandler.throwPrintError(node);
    }
    expressionCache.add(expr, contentType);
  }

  @Override
  public void outAPrintlnStat(APrintlnStat node) {
    PExpr expr = node.getExpr();
    String contentType = evaluateType(expr);
    if (!(contentType.equals("boolean") || contentType.equals("int"))) {
      errorHandler.throwPrintlnError(node);
    }
    expressionCache.add(expr, contentType);
  }

  // Helpers
  private String evaluateType(PExpr expr) {
    ExpressionTypeEvaluator typeEvaluator = new ExpressionTypeEvaluator(symbolTable);
    expr.apply(typeEvaluator);
    return typeEvaluator.getType();
  }

  public boolean errorsOccurred() {
    return this.errorHandler.errorsOccurred();
  }

  public ExpressionCache getExpressionCache() {
    return this.expressionCache;
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public void printSymbolTable() {
    this.symbolTable.printTable();
  }
}
