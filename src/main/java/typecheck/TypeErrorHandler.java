package typecheck;

import lineevaluation.LineEvaluator;
import node.Node;
import node.TIdentifier;

public class TypeErrorHandler {
  private int errorNumber;

  public TypeErrorHandler() {
    errorNumber = 0;
  }


  // Error generators
  void throwAlreadyDefinedError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "is already defined.");
  }

  void throwNotDeclaredError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "has not been declared before usage.");
  }

  void throwFlawedExpressionError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "could not be assigned, because the expression contains incompatible types.");
  }

  void throwIncompatibleError(TIdentifier id, Type varType, Type exprType) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "could not be assigned, because the given expression's type is incompatible. Expected \"" + varType.toString().toLowerCase() + "\", but found \"" + exprType.toString().toLowerCase() + "\".");
  }

  void throwConditionError(Node node, String statementName, Type wrongType) {
    errorNumberIncrement();
    if (wrongType.equals(Type.ERROR)) {
      System.out.println(generateErrorHeadNode(node) + "Expression in " + statementName + " condition contains type errors.");
    } else {
      System.out.println(generateErrorHeadNode(node) + "Expression in " + statementName + " condition has incompatible type. Expected: \"boolean\", but found \"" + wrongType.toString().toLowerCase() + "\".");
    }
  }

  void throwPrintError(Node node) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadNode(node) + "Expression in print statement contains type errors.");
  }

  void throwPrintlnError(Node node) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadNode(node) + "Expression in println statement contains type errors.");
  }

  void throwInternalError(TIdentifier id) { // Should never happen
    System.err.println(generateErrorHeadId(id) + "could not be initialized, because an internal type error occurred.");
    System.exit(0);
  }


  // Helpers
  private String generateErrorHeadId(TIdentifier id) {
    return "Type-Error at (" + id.getLine() + "," + id.getPos() + "): Identifier \"" + id.getText() + "\" ";
  }

  private String generateErrorHeadNode(Node node) {
    return "Type-Error at (" + LineEvaluator.getLine(node) + "," + LineEvaluator.getPosition(node) + "): ";
  }

  private void errorNumberIncrement() {
    this.errorNumber++;
  }

  int getErrorNumber() {
    return this.errorNumber;
  }

  boolean errorsOccurred() {
    return getErrorNumber() > 0;
  }
}
