
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import livenessanalysis.LivenessAnalyzer;
import org.junit.jupiter.api.Test;
import symboltable.Symbol;
import symboltable.Type;

public class UnusedVariableAnalysisTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final String pathTestFilesLiveness = "src/test/resources/liveness/";

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
  public void zeroVariablesAndZeroArguments() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "noVariable.easy");
    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();
    assertEquals(1, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
  }

  @Test
  public void oneVariablesAndZeroArguments() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "oneVariable.easy");
    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();
    assertEquals(1, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
  }

  @Test
  public void severalVariablesAndArgumentsButAllUsed() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");
    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();
    assertEquals(2, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
    assertEquals(0, unusedArguments.get("abs").size());
  }

  @Test
  public void twoArgumentsAndOneUnused() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "unusedArguments.easy");

    HashMap<String, List<Symbol>> unusedArguments = easyCompiler.getUnusedArgsPerFunction();

    assertEquals(2, unusedArguments.keySet().size());
    assertEquals(0, unusedArguments.get("main").size());
    assertEquals(2, unusedArguments.get("f").size());

    Symbol firstUnusedArgument = unusedArguments.get("f").get(0);
    assertEquals(firstUnusedArgument.getType(), Type.BOOLEAN);
    assertEquals(firstUnusedArgument.getVariableNumber(), 1);

    Symbol secondUnusedArgument = unusedArguments.get("f").get(1);
    assertEquals(secondUnusedArgument.getType(), Type.STRING);
    assertEquals(secondUnusedArgument.getVariableNumber(), 2);
  }
}
