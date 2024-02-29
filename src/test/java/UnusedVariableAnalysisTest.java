
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.Test;
import symboltable.Symbol;
import symboltable.Type;

public class UnusedVariableAnalysisTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final String pathTestFilesLiveness = "src/test/resources/liveness/";

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
