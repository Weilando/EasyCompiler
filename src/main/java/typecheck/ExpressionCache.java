package typecheck;

import node.PExpr;

import java.util.HashMap;

public class ExpressionCache {
  private final HashMap<PExpr, String> table;

  public ExpressionCache() {
    this.table = new HashMap<>();
  }

  void add(PExpr expr, String type) {
    table.put(expr, type);
  }

  public String getType(PExpr expr) {
    return table.get(expr);
  }
}
