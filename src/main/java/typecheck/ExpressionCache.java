package typecheck;

import node.PExpr;

import java.util.HashMap;

public class ExpressionCache {
  private final HashMap<PExpr, Type> table;

  public ExpressionCache() {
    this.table = new HashMap<>();
  }

  void add(PExpr expr, Type type) {
    table.put(expr, type);
  }

  public Type getType(PExpr expr) {
    return table.get(expr);
  }
}
