package typecheck;

public class Symbol {
  private final String type;
  private final int variableNumber;

  public Symbol(String type, int variableNumber) {
    this.type = type;
    this.variableNumber = variableNumber;
  }

  public int getVariableNumber() {
    return variableNumber;
  }

  public String getType() {
    return type;
  }

  public String toString() {
    return String.format("<#%d,%s>", variableNumber, type);
  }
}
