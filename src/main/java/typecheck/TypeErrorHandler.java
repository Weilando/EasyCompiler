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

  void throwIncompatibleError(TIdentifier id, String varType, String exprType) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "could not be assigned, because the given expression's type is incompatible. Expected \"" + varType + "\", but found \"" + exprType + "\".");
  }

  void throwConditionError(Node node, String statementType, String wrongType) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadNode(node) + "Expression in " + statementType + " condition has incompatible type. Expected: \"boolean\", but found \"" + wrongType + "\".");
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

  public int getErrorNumber() {
    return this.errorNumber;
  }

  public boolean errorsOccurred() {
    return getErrorNumber() > 0;
  }
}
