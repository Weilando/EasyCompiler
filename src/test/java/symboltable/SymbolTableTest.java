package symboltable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class SymbolTableTest {
  @Test
  public void addNewScopeWithoutArgumentsAndFunctions() {
    SymbolTable symbolTable = new SymbolTable();
    symbolTable.addNewScope("newScope", Type.STRING);

    assertEquals(0, symbolTable.getNumberOfArguments("newScope"));
    assertEquals(Type.STRING, symbolTable.getFunctionReturnType("newScope"));
  }

  @Test
  public void addNewScopeWithOneArgumentAndTwoVariables() {
    SymbolTable symbolTable = new SymbolTable();
    symbolTable.addNewScope("newScope", Type.BOOLEAN);
    symbolTable.addFunctionArgumentType("newScope", Type.FLOAT);
    symbolTable.addSymbolToScope("newScope", "symbol0", Type.INT);
    symbolTable.addSymbolToScope("newScope", "symbol1", Type.STRING);

    FunctionArgumentTypeList argumentList = symbolTable.getFunctionArgumentTypes("newScope");
    assertEquals(1, symbolTable.getNumberOfArguments("newScope"));
    assertEquals(Type.FLOAT, argumentList.getArgumentType(0));
    assertEquals(0, symbolTable.getVariableNumber("newScope", "symbol0"));
    assertEquals(1, symbolTable.getVariableNumber("newScope", "symbol1"));
    assertEquals(Type.INT, symbolTable.getSymbolType("newScope", "symbol0"));
    assertEquals(Type.STRING, symbolTable.getSymbolType("newScope", "symbol1"));
    assertEquals(new ArrayList<String>(List.of("newScope")), symbolTable.getScopeNames());
  }

  @Test
  public void addTwoScopesWithSeparateArgumentsAndVariables() {
    SymbolTable symbolTable = new SymbolTable();
    symbolTable.addNewScope("scopeA", Type.INT);
    symbolTable.addNewScope("scopeB", Type.FLOAT);
    symbolTable.addFunctionArgumentType("scopeA", Type.INT);
    symbolTable.addFunctionArgumentType("scopeB", Type.INT);
    symbolTable.addFunctionArgumentType("scopeB", Type.FLOAT);
    symbolTable.addSymbolToScope("scopeA", "symbol0", Type.INT);
    symbolTable.addSymbolToScope("scopeA", "symbol1", Type.INT);
    symbolTable.addSymbolToScope("scopeB", "symbol", Type.FLOAT);

    assertEquals(Type.INT, symbolTable.getFunctionReturnType("scopeA"));
    assertEquals(Type.FLOAT, symbolTable.getFunctionReturnType("scopeB"));
    assertEquals(1, symbolTable.getNumberOfArguments("scopeA"));
    assertEquals(2, symbolTable.getNumberOfArguments("scopeB"));

    FunctionArgumentTypeList argumentListA = symbolTable.getFunctionArgumentTypes("scopeA");
    FunctionArgumentTypeList argumentListB = symbolTable.getFunctionArgumentTypes("scopeB");
    assertEquals(Type.INT, argumentListA.getArgumentType(0));
    assertEquals(Type.INT, argumentListB.getArgumentType(0));
    assertEquals(Type.FLOAT, argumentListB.getArgumentType(1));

    assertEquals(0, symbolTable.getVariableNumber("scopeA", "symbol0"));
    assertEquals(1, symbolTable.getVariableNumber("scopeA", "symbol1"));
    assertEquals(0, symbolTable.getVariableNumber("scopeB", "symbol"));
    assertEquals(Type.INT, symbolTable.getSymbolType("scopeA", "symbol0"));
    assertEquals(Type.INT, symbolTable.getSymbolType("scopeA", "symbol1"));
    assertEquals(Type.FLOAT, symbolTable.getSymbolType("scopeB", "symbol"));
    assertEquals(new ArrayList<String>(List.of("scopeA", "scopeB")), symbolTable.getScopeNames());
  }
}
