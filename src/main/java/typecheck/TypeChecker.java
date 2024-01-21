package typecheck;

import analysis.DepthFirstAdapter;
import java.util.LinkedList;
import node.AAssignStat;
import node.AFuncExpr;
import node.AFuncStat;
import node.AIfStat;
import node.AIfelseStat;
import node.AInitStat;
import node.APrintStat;
import node.APrintlnStat;
import node.AWhileStat;
import node.PExpr;
import node.TIdentifier;
import symboltable.FunctionArgumentTypeList;
import symboltable.SymbolTable;
import symboltable.Type;

/**
 * The type checker walks the AST using depth first search. IT assigns a type to
 * each node and checks the compatibility of childrens' types.
 */
public class TypeChecker extends DepthFirstAdapter {
  private final TypeErrorHandler errorHandler;
  private final SymbolTable symbolTable;

  public TypeChecker(SymbolTable symbolTable) {
    this.errorHandler = new TypeErrorHandler();
    this.symbolTable = symbolTable;
  }

  @Override
  public void outAFuncStat(AFuncStat node) {
    TIdentifier id = node.getId();
    String idText = id.getText();
    FunctionArgumentTypeList definitionArgTypes = symbolTable.getFunctionArgumentTypes(idText);
    LinkedList<PExpr> statementArgTypes = node.getArgs();
    checkFunctionCall(id, definitionArgTypes, statementArgTypes);
  }

  @Override
  public void caseAFuncExpr(AFuncExpr node) {
    TIdentifier id = node.getId();
    String idText = id.getText();
    FunctionArgumentTypeList definitionArgTypes = symbolTable.getFunctionArgumentTypes(idText);
    LinkedList<PExpr> statementArgTypes = node.getArgs();
    checkFunctionCall(id, definitionArgTypes, statementArgTypes);
  }

  // Variable statements
  @Override
  public void outAInitStat(AInitStat node) {
    TIdentifier id = node.getId();
    String idText = id.getText();
    String scopeName = symbolTable.determineScope(node);
    Type varType = symbolTable.getSymbolType(scopeName, idText);
    Type exprType = evaluateType(node.getExpr());
    checkAssignment(id, varType, exprType);
  }

  @Override
  public void outAAssignStat(AAssignStat node) {
    TIdentifier id = node.getId();
    String idText = id.getText();

    String scopeName = symbolTable.determineScope(node);
    if (!symbolTable.containsSymbol(scopeName, idText)) {
      errorHandler.printNotDeclaredError(id);
      return;
    }

    Type varType = symbolTable.getSymbolType(scopeName, idText);
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

  private void checkFunctionCall(TIdentifier id, FunctionArgumentTypeList definitionArgTypes,
      LinkedList<PExpr> statementArgTypes) {
    int expectedNumberOfArgs = definitionArgTypes.getNumberOfArguments();
    int givenNumberOfArgs = statementArgTypes.size();

    if (expectedNumberOfArgs != givenNumberOfArgs) {
      errorHandler.printWrongNumberOfArgumentsError(id, expectedNumberOfArgs, givenNumberOfArgs);
    } else {
      for (int i = 0; i < expectedNumberOfArgs; i++) {
        PExpr givenArg = statementArgTypes.get(i);
        Type givenArgType = evaluateType(givenArg);
        Type expectedArgType = definitionArgTypes.getArgumentType(i);
        if (givenArgType != expectedArgType) {
          errorHandler.printWrongArgumentTypeError(id, i, expectedArgType, givenArgType);
        }
      }
    }
  }

  public boolean errorsOccurred() {
    return this.errorHandler.errorsOccurred();
  }

  public int getErrorNumber() {
    return this.errorHandler.getErrorNumber();
  }
}
