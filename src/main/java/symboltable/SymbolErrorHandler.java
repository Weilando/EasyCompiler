package symboltable;

import node.TIdentifier;

/**
 * Generator for specialized symbol errors. Usually, it prints the error message
 * to generate a full list of errors. It only fails for unexpected behaviour.
 */
public class SymbolErrorHandler {
  private int errorNumber;

  public SymbolErrorHandler() {
    errorNumber = 0;
  }

  // Error generators
  void printAlreadyDefinedError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "is already defined.");
  }

  void printNotDeclaredError(TIdentifier id) {
    errorNumberIncrement();
    System.out.println(generateErrorHeadId(id) + "has not been declared before usage.");
  }

  void throwInternalError(TIdentifier id) { // Should never happen
    String errorHead = generateErrorHeadId(id);
    String errorMessage = "could not be initialized, because an internal symbol error occurred.";
    System.err.println(errorHead + errorMessage);
    System.exit(0);
  }

  // Helpers
  private String generateErrorHeadId(TIdentifier id) {
    return "Symbol-Error at (%d,%d): Identifier \"%s\" ".formatted(
        id.getLine(), id.getPos(), id.getText());
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
