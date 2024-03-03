
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import livenessanalysis.UnusedValue;
import org.junit.jupiter.api.Test;
import symboltable.Symbol;
import symboltable.Type;

public class UnusedVariableAnalysisTest {
  private final String pathTestFiles = "src/test/resources/liveness/";

  @Test
  public void severalVariablesAndArgumentsButAllUsed() {
    final String pathAlgorithms = "src/test/resources/algorithms/";
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");

    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgumentsPerFunction();
    assertEquals(2, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
    assertEquals(0, unusedArguments.get("abs").size());

    HashMap<String, List<Symbol>> unusedDeclarations = easyCompiler
        .getUnusedVariableDeclarationsPerFunction();
    assertEquals(2, unusedDeclarations.keySet().size());
    assertEquals(0, unusedDeclarations.get("main").size());
    assertEquals(0, unusedDeclarations.get("abs").size());

    HashMap<String, List<UnusedValue>> unusedValues = easyCompiler
        .getUnusedVariableValuesPerFunction();
    assertEquals(2, unusedValues.keySet().size());
    assertEquals(1, unusedValues.get("main").size()); // limitation in loops
    assertEquals(0, unusedValues.get("abs").size());

    UnusedValue unusedVariable = unusedValues.get("main").get(0);
    assertEquals(33, unusedVariable.getLineNumber());
    assertEquals("AAssignStat", unusedVariable.getStatementType());
    Symbol unusedSymbol = unusedVariable.getSymbol();
    assertEquals(1, unusedSymbol.getVariableNumber());
    assertEquals(Type.INT, unusedSymbol.getType());
  }

  @Test
  public void unusedArguments() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFiles + "unusedArguments.easy");

    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgumentsPerFunction();

    assertTrue(easyCompiler.liveness());
    assertEquals(2, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
    assertEquals(2, unusedArguments.get("f").size());

    Symbol firstUnusedArgument = unusedArguments.get("f").get(0);
    assertEquals(Type.BOOLEAN, firstUnusedArgument.getType());
    assertEquals(0, firstUnusedArgument.getVariableNumber());

    Symbol secondUnusedArgument = unusedArguments.get("f").get(1);
    assertEquals(Type.STRING, secondUnusedArgument.getType());
    assertEquals(2, secondUnusedArgument.getVariableNumber());

    assertEquals(0, easyCompiler.getUnusedVariableDeclarationsPerFunction().get("f").size());
    assertEquals(0, easyCompiler.getUnusedVariableValuesPerFunction().get("f").size());
  }

  @Test
  public void unusedDeclarations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFiles + "unusedDeclarations.easy");

    HashMap<String, List<Symbol>> unusedDeclarations = easyCompiler
        .getUnusedVariableDeclarationsPerFunction();

    assertTrue(easyCompiler.liveness());
    assertEquals(1, unusedDeclarations.keySet().size());
    assertEquals(1, unusedDeclarations.get("main").size());

    Symbol unusedVariable = unusedDeclarations.get("main").get(0);
    assertEquals(0, unusedVariable.getVariableNumber());
    assertEquals(Type.INT, unusedVariable.getType());

    assertEquals(0, easyCompiler.getUnusedArgumentsPerFunction().get("main").size());
    assertEquals(0, easyCompiler.getUnusedVariableValuesPerFunction().get("main").size());
  }

  @Test
  public void unusedVariableValues() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFiles + "unusedVariableValues.easy");

    HashMap<String, List<UnusedValue>> unusedVariableValues = easyCompiler
        .getUnusedVariableValuesPerFunction();

    assertTrue(easyCompiler.liveness());
    assertEquals(1, unusedVariableValues.keySet().size());
    assertEquals(2, unusedVariableValues.get("main").size());

    UnusedValue firstUnusedVariable = unusedVariableValues.get("main").get(0);
    assertEquals(3, firstUnusedVariable.getLineNumber());
    assertEquals("AInitStat", firstUnusedVariable.getStatementType());
    Symbol firstUnusedSymbol = firstUnusedVariable.getSymbol();
    assertEquals(1, firstUnusedSymbol.getVariableNumber());
    assertEquals(Type.BOOLEAN, firstUnusedSymbol.getType());

    UnusedValue secondUnusedVariable = unusedVariableValues.get("main").get(1);
    assertEquals(5, secondUnusedVariable.getLineNumber());
    assertEquals("AAssignStat", secondUnusedVariable.getStatementType());
    Symbol secondUnusedSymbol = firstUnusedVariable.getSymbol();
    assertEquals(1, secondUnusedSymbol.getVariableNumber());
    assertEquals(Type.BOOLEAN, secondUnusedSymbol.getType());

    assertEquals(0, easyCompiler.getUnusedArgumentsPerFunction().get("main").size());
    assertEquals(0, easyCompiler.getUnusedVariableDeclarationsPerFunction().get("main").size());
  }
}
