package symboltable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class FunctionArgumentTypeListTest {
  @Test
  public void newListIsEmpty() {
    FunctionArgumentTypeList arguments = new FunctionArgumentTypeList();
    assertEquals(0, arguments.getNumberOfArguments());
  }

  @Test
  public void addNewArgumentsInTheCorrectOrder() {
    FunctionArgumentTypeList arguments = new FunctionArgumentTypeList();
    arguments.addArgumentType(Type.BOOLEAN);
    assertEquals(1, arguments.getNumberOfArguments());
    arguments.addArgumentType(Type.INT);
    assertEquals(Type.BOOLEAN, arguments.getArgumentType(0));
    assertEquals(Type.INT, arguments.getArgumentType(1));
    assertEquals(2, arguments.getNumberOfArguments());
  }

  @Test
  public void getJvmTypeString() {
    FunctionArgumentTypeList arguments = new FunctionArgumentTypeList();
    arguments.addArgumentType(Type.BOOLEAN);
    arguments.addArgumentType(Type.STRING);
    arguments.addArgumentType(Type.INT);
    arguments.addArgumentType(Type.FLOAT);
    assertEquals("ZLjava/lang/String;IF", arguments.getJvmTypeString());
  }
}
