
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import livenessanalysis.LivenessAnalyzer;
import livenessanalysis.UnusedVariable;
import org.junit.jupiter.api.Test;
import symboltable.Symbol;
import symboltable.Type;

public class UnusedVariableAnalysisTest {
  private final String pathTestFiles = "src/test/resources/liveness/";

  @Test
  public void findDefinedButUnusedSymbols() {
    Symbol s0 = new Symbol(Type.STRING, 0);
    Symbol s1 = new Symbol(Type.STRING, 1);
    Symbol s2 = new Symbol(Type.STRING, 2);
    HashSet<Symbol> definedArguments = new HashSet<>();
    definedArguments.add(s2);
    definedArguments.add(s1);
    definedArguments.add(s0);

    HashSet<Symbol> usedArguments = new HashSet<>();
    usedArguments.add(s1);

    List<Symbol> result = LivenessAnalyzer.findDefinedButUnusedSymbols(
        definedArguments, usedArguments);
    assertEquals(result.size(), 2);
    assertEquals(result.get(0), s0);
    assertEquals(result.get(1), s2);
  }

  @Test
  public void severalVariablesAndArgumentsButAllUsed() {
    final String pathAlgorithms = "src/test/resources/algorithms/";
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");

    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();
    assertEquals(2, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
    assertEquals(0, unusedArguments.get("abs").size());

    HashMap<String, List<UnusedVariable>> unusedValues = easyCompiler
        .getUnusedVariableValuesPerFunction();
    assertEquals(2, unusedValues.keySet().size());
    assertEquals(1, unusedValues.get("main").size()); // limitation in loops
    assertEquals(0, unusedValues.get("abs").size());

    UnusedVariable unusedVariable = unusedValues.get("main").get(0);
    assertEquals(33, unusedVariable.getLineNumber());
    assertEquals("AAssignStat", unusedVariable.getStatementType());
    Symbol unusedSymbol = unusedVariable.getSymbol();
    assertEquals(1, unusedSymbol.getVariableNumber());
    assertEquals(Type.INT, unusedSymbol.getType());
  }

  @Test
  public void unusedArguments() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFiles + "unusedArguments.easy");

    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();

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
  }

  @Test
  public void unusedVariableValues() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFiles + "unusedVariableValues.easy");

    HashMap<String, List<UnusedVariable>> unusedVariableValues = easyCompiler
        .getUnusedVariableValuesPerFunction();

    assertTrue(easyCompiler.liveness());
    assertEquals(1, unusedVariableValues.keySet().size());
    assertEquals(2, unusedVariableValues.get("main").size());

    UnusedVariable firstUnusedVariable = unusedVariableValues.get("main").get(0);
    assertEquals(3, firstUnusedVariable.getLineNumber());
    assertEquals("AInitStat", firstUnusedVariable.getStatementType());
    Symbol firstUnusedSymbol = firstUnusedVariable.getSymbol();
    assertEquals(1, firstUnusedSymbol.getVariableNumber());
    assertEquals(Type.BOOLEAN, firstUnusedSymbol.getType());

    UnusedVariable secondUnusedVariable = unusedVariableValues.get("main").get(1);
    assertEquals(5, secondUnusedVariable.getLineNumber());
    assertEquals("AAssignStat", secondUnusedVariable.getStatementType());
    Symbol secondUnusedSymbol = firstUnusedVariable.getSymbol();
    assertEquals(1, secondUnusedSymbol.getVariableNumber());
    assertEquals(Type.BOOLEAN, secondUnusedSymbol.getType());
  }
}
