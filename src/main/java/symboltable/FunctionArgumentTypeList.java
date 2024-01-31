package symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** List of (data) types for all arguments of a single function. */
public class FunctionArgumentTypeList {
  private final List<Type> arguments = new ArrayList<>();

  public void addArgumentType(Type type) {
    arguments.add(type);
  }

  public Type getArgumentType(int position) {
    return arguments.get(position);
  }

  public int getNumberOfArguments() {
    return arguments.size();
  }

  public String getJvmTypeString() {
    return arguments.stream().map(t -> t.getJvmType()).collect(Collectors.joining(""));
  }
}
