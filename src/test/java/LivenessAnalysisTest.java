import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

public class LivenessAnalysisTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesLiveness = "src/test/resources/liveness/";

  // ------------------------
  // Tests liveness-analysis
  // ------------------------
  @Test
  public void zeroVariablesNeed0Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "noVariable.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(0, minimumRegisters.get("main"));
  }

  @Test
  public void oneVariableNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "oneVariable.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(1, minimumRegisters.get("main"));
  }

  @Test
  public void oneLiveVariableNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(
        pathTestFilesLiveness + "twoVariablesOneLive.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(1, minimumRegisters.get("main"));
  }

  @Test
  public void twoLiveVariablesNeed2Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(
        pathTestFilesLiveness + "twoVariablesBothLive.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(2, minimumRegisters.get("main"));
  }

  @Test
  public void ifNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "If.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(1, minimumRegisters.get("main"));
  }

  @Test
  public void intNeeds4Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Int.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(4, minimumRegisters.get("main"));
  }

  @Test
  public void binomialNeeds5Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Binomial.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(1, minimumRegisters.keySet().size());
    assertEquals(5, minimumRegisters.get("main"));
  }

  @Test
  public void euclidNeeds5Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");
    HashMap<String, Integer> minimumRegisters = easyCompiler.getMinimumRegistersPerFunction();
    assertEquals(2, minimumRegisters.keySet().size());
    assertEquals(5, minimumRegisters.get("main"));
    assertEquals(1, minimumRegisters.get("abs"));
  }
}
