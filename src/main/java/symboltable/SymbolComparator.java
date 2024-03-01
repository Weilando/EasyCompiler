package symboltable;

import java.util.Comparator;

/** Comparator for Symbol instances. */
public class SymbolComparator implements Comparator<Symbol> {

  @Override
  public int compare(Symbol s1, Symbol s2) {
    return s1.getVariableNumber() - s2.getVariableNumber();
  }
}
