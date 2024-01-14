package typecheck;

import analysis.DepthFirstAdapter;
import node.AAssignStat;
import node.ABooleanTp;
import node.ADeclStat;
import node.AFloatTp;
import node.AIfStat;
import node.AIfelseStat;
import node.AInitStat;
import node.AIntTp;
import node.APrintStat;
import node.APrintlnStat;
import node.AStringTp;
import node.AWhileStat;
import node.PExpr;
import node.TIdentifier;

/**
 * The type checker walks the AST using depth first search. IT assigns a type to
 * each node and checks the compatibility of childrens' types.
 */
public class TypeChecker extends DepthFirstAdapter {
  private final TypeErrorHandler errorHandler;
  private final SymbolTable symbolTable;

  public TypeChecker() {
    this.errorHandler = new TypeErrorHandler();
    this.symbolTable = new SymbolTable();
  }

  // Variable statements
  public void outAInitStat(AInitStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();
    Type varType = Type.ERROR;
    if (node.getType() instanceof ABooleanTp) {
      varType = Type.BOOLEAN;
    } else if (node.getType() instanceof AFloatTp) {
      varType = Type.FLOAT;
    } else if (node.getType() instanceof AIntTp) {
      varType = Type.INT;
    } else if (node.getType() instanceof AStringTp) {
      varType = Type.STRING;
    } else { // Should never happen
      errorHandler.throwInternalError(id);
    }

    if (symbolTable.contains(key)) {
      errorHandler.printAlreadyDefinedError(id);
    } else {
      symbolTable.add(key, varType);
    }

    Type exprType = evaluateType(node.getExpr());
    checkAssignment(id, varType, exprType);
  }

  public void outADeclStat(ADeclStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();
    Type type = Type.ERROR;
    if (node.getType() instanceof ABooleanTp) {
      type = Type.BOOLEAN;
    } else if (node.getType() instanceof AFloatTp) {
      type = Type.FLOAT;
    } else if (node.getType() instanceof AIntTp) {
      type = Type.INT;
    } else if (node.getType() instanceof AStringTp) {
      type = Type.STRING;
    }

    if (symbolTable.contains(key)) {
      errorHandler.printAlreadyDefinedError(id);
    } else {
      symbolTable.add(key, type);
    }
  }

  public void outAAssignStat(AAssignStat node) {
    TIdentifier id = node.getId();
    String key = id.getText();

    if (!symbolTable.contains(key)) {
      errorHandler.printNotDeclaredError(id);
      return;
    }

    Type varType = symbolTable.getType(key);
    Type exprType = evaluateType(node.getExpr());
    checkAssignment(id, varType, exprType);
  }

  // Control statements
  @Override
  public void outAWhileStat(AWhileStat node) {
    Type headType = evaluateType(node.getExpr());
    if (!headType.equals(Type.BOOLEAN)) {
      errorHandler.printConditionError(node, "while", headType);
    }
  }

  @Override
  public void outAIfStat(AIfStat node) {
    Type headType = evaluateType(node.getExpr());
    if (!headType.equals(Type.BOOLEAN)) {
      errorHandler.printConditionError(node, "if", headType);
    }
  }

  @Override
  public void outAIfelseStat(AIfelseStat node) {
    Type headType = evaluateType(node.getExpr());
    if (!headType.equals(Type.BOOLEAN)) {
      errorHandler.printConditionError(node, "if", headType);
    }
  }

  // Print statement
  @Override
  public void outAPrintStat(APrintStat node) {
    PExpr expr = node.getExpr();
    Type contentType = evaluateType(expr);
    if (contentType.equals(Type.ERROR)) {
      errorHandler.printPrintError(node);
    }
    expr.setType(contentType);
  }

  @Override
  public void outAPrintlnStat(APrintlnStat node) {
    PExpr expr = node.getExpr();
    Type contentType = evaluateType(expr);
    if (contentType.equals(Type.ERROR)) {
      errorHandler.printPrintlnError(node);
    }
    expr.setType(contentType);
  }

  // Helpers
  private Type evaluateType(PExpr expr) {
    ExpressionTypeEvaluator typeEvaluator = new ExpressionTypeEvaluator(symbolTable);
    expr.apply(typeEvaluator);
    return typeEvaluator.getType();
  }

  private void checkAssignment(TIdentifier id, Type varType, Type exprType) {
    if (exprType.equals(Type.ERROR)) {
      errorHandler.printFlawedExpressionError(id);
    } else if (varType.equals(Type.FLOAT)) {
      if (!(exprType.equals(Type.FLOAT) || exprType.equals(Type.INT))) {
        errorHandler.printIncompatibleError(id, varType, exprType);
      }
    } else if (!varType.equals(exprType)) {
      errorHandler.printIncompatibleError(id, varType, exprType);
    }
  }

  public boolean errorsOccurred() {
    return this.errorHandler.errorsOccurred();
  }

  public int getErrorNumber() {
    return this.errorHandler.getErrorNumber();
  }

  public SymbolTable getSymbolTable() {
    return this.symbolTable;
  }

  public void printSymbolTable() {
    this.symbolTable.printTable();
  }
}
