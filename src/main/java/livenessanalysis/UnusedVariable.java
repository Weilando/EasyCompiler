package livenessanalysis;

import symboltable.Symbol;

/** Data class for a variable that is either never assigned or read. */
public class UnusedVariable {
  private final int lineNumber;
  private final String statementType;
  private final Symbol symbol;

  /**
   * Data class for a variable that is either never assigned or read.
   *
   * @param lineNumber    Line number of the declaration or write statement.
   * @param statementType Type of declaration or write statement.
   * @param symbol        Symbol that is never assigned or read.
   */
  public UnusedVariable(int lineNumber, String statementType, Symbol symbol) {
    this.lineNumber = lineNumber;
    this.statementType = statementType;
    this.symbol = symbol;
  }

  public String getStatementType() {
    return this.statementType;
  }

  public Symbol getSymbol() {
    return this.symbol;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }
}
