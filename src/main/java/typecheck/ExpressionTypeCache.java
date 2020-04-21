package typecheck;

import node.PExpr;

import java.util.HashMap;

public class ExpressionTypeCache {
  private final HashMap<PExpr, Type> table;

  public ExpressionTypeCache() {
    this.table = new HashMap<>();
  }

  void add(PExpr expr, Type type) {
    table.put(expr, type);
  }

  public Type getType(PExpr expr) {
    if (table.containsKey(expr)) {
      return table.get(expr);
    }
    return Type.UNDEFINED;
  }
}
