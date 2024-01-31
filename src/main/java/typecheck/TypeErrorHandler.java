package typecheck;

import lineevaluation.LineEvaluator;
import node.Node;
import node.TIdentifier;
import symboltable.Type;

/**
 * Generator for specialized type errors. Usually, it prints the error message
 * to generate a full list of errors. It only fails for unexpected behaviour.
 */
public class TypeErrorHandler {
  private int errorNumber;

  public TypeErrorHandler() {
    errorNumber = 0;
  }

  // Error generators
  void printFlawedExpressionError(TIdentifier id) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadId(id);
    String errorMessage = "cannot be assigned, because the expression contains incompatible types.";
    System.out.println(errorHead + errorMessage);
  }

  void printIncompatibleError(TIdentifier id, Type varType, Type exprType) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadId(id);
    String errorMessage = ("cannot be assigned, because the expression type is incompatible."
        + " Expected \"%s\", but found\"%s\".")
        .formatted(varType.toString().toLowerCase(), exprType.toString().toLowerCase());
    System.out.println(errorHead + errorMessage);
  }

  void printNotDeclaredError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "has not been declared before usage.");
  }

  void printConditionError(Node node, String statementName, Type wrongType) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadNode(node);
    if (wrongType.equals(Type.ERROR)) {
      String errorMessage = "Expression in " + statementName + " condition contains type errors.";
      System.out.println(errorHead + errorMessage);
    } else {
      String errorMessage = ("Expression in %s condition has incompatible type."
          + " Expected \"boolean\" but found \"%s\".")
          .formatted(statementName, wrongType.toString().toLowerCase());
      System.out.println(errorHead + errorMessage);
    }
  }

  void printWrongNumberOfArgumentsError(
      TIdentifier functioIdentifier, int expectedNumber, int givenNumber) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadId(functioIdentifier);
    String errorMessage = "Function requires %d arguments but got %d.".formatted(
        expectedNumber, givenNumber);
    System.out.println(errorHead + errorMessage);
  }

  void printWrongArgumentTypeError(
      TIdentifier functioIdentifier, int argPosition, Type expectedType, Type givenType) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadId(functioIdentifier);
    String errorMessage = "Argument %d has the wrong type. Expected %s but got %s.".formatted(
        argPosition, expectedType, givenType);
    System.out.println(errorHead + errorMessage);
  }

  void printWrongReturnTypeError(
      Node returnNode, Type expectedType, Type givenType) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadNode(returnNode);
    String errorMessage = "Return expression has the wrong type. Expected %s but got %s.".formatted(
        expectedType, givenType);
    System.out.println(errorHead + errorMessage);
  }

  void printPrintError(Node node) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadNode(node);
    String errorMessage = "Expression in print statement contains type errors.";
    System.out.println(errorHead + errorMessage);
  }

  void printPrintNoneError(Node node) {
    errorNumberIncrement();
    String errorHead = generateErrorHeadNode(node);
    String errorMessage = "Cannot print none.";
    System.out.println(errorHead + errorMessage);
  }

  void throwInternalError(TIdentifier id) { // Should never happen
    String errorHead = generateErrorHeadId(id);
    String errorMessage = "could not be initialized, because an internal type error occurred.";
    System.err.println(errorHead + errorMessage);
    System.exit(0);
  }

  // Helpers
  private String generateErrorHeadId(TIdentifier id) {
    return "Type-Error at (%d,%d): Identifier \"%s\" ".formatted(
        id.getLine(), id.getPos(), id.getText());
  }

  private String generateErrorHeadNode(Node node) {
    return "Type-Error at (%d,%d): ".formatted(
        LineEvaluator.getLine(node), LineEvaluator.getPosition(node));
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
