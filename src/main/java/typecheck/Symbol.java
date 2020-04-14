package typecheck;

public class Symbol {
  private final Type type;
  private final int variableNumber;

  public Symbol(Type type, int variableNumber) {
    this.type = type;
    this.variableNumber = variableNumber;
  }

  public int getVariableNumber() {
    return variableNumber;
  }

  public Type getType() {
    return type;
  }

  public String toString() {
    return String.format("<#%d,%s>", variableNumber, type);
  }
}
