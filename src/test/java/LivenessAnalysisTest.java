import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LivenessAnalysisTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesLiveness = "src/test/resources/liveness/";

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  @BeforeEach
  public void setUpStreams() {
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @AfterEach
  public void restoreStreams() {
    System.setOut(originalOut);
    System.setErr(originalErr);
  }

  //------------------------
  // Tests liveness-analysis
  //------------------------
  @Test
  public void zeroVariablesNeed0Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "noVariable.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 0\n"));
  }

  @Test
  public void oneVariableNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "oneVariable.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 1\n"));
  }

  @Test
  public void oneLiveVariableNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "twoVariablesOneLive.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 1\n"));
  }

  @Test
  public void twoLiveVariablesNeed2Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "twoVariablesBothLive.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 2\n"));
  }

  @Test
  public void IfNeeds1Register() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "If.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 1\n"));
  }

  @Test
  public void IntCalculationsNeeds3Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IntCalculations.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 3\n"));
  }

  @Test
  public void BinomialNeeds5Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Binomial.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 5\n"));
  }

  @Test
  public void EuclidNeeds5Registers() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");
    easyCompiler.liveness();
    assertFalse(outContent.toString().contains("Type-Error"));
    assertTrue(outContent.toString().contains("Registers: 5\n"));
  }
}
