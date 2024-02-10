import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EasyCompilerTest {
  private final String pathMinimalProgram = "src/test/resources/correct/Minimal.easy";

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

  // -----------------
  // Test args-checks
  // -----------------
  @Test
  public void callWithoutArgsShouldFail() {
    String[] args = {};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("usage"));
  }

  @Test
  public void callWithCompileFlagAndMissingFilenameShouldFail() {
    String[] args = { "-compile" };
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("usage"));
  }

  @Test
  public void callWithCompileFlagAndFilenameShouldWork() {
    // Use an incorrect file to avoid any output-files.
    String[] args = { "-compile", "src/test/resources/failParser/FailMissingBrace.easy" };
    EasyCompiler.main(args);
  }

  @Test
  public void callWithLivenessFlagAndFilenameShouldWork() {
    // Use an incorrect file to avoid any output-files.
    String[] args = { "-liveness", pathMinimalProgram };
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Registers: 0"));
  }

  @Test
  public void callWithParseFlagAndFilenameShouldWork() {
    String[] args = { "-parse", pathMinimalProgram };
    EasyCompiler.main(args);
  }

  @Test
  public void callWithTypeCheckFlagAndFilenameShouldWork() {
    String[] args = { "-typeCheck", pathMinimalProgram };
    EasyCompiler.main(args);
  }

  // --------------------
  // Test error-messages
  // --------------------
  @Test
  public void typeErrorMessageShouldBePrinted() {
    String[] args = { "-compile", "src/test/resources/failTypeCheck/FailBadConditions.easy" };
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error at (7,6): "));
    assertTrue(outContent.toString().contains("Type-Error at (14,9): "));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseErrorMessageShouldBePrinted() {
    String[] args = { "-compile", "src/test/resources/failParser/FailMissingBrace.easy" };
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error: "));
    assertFalse(outContent.toString().contains("Successful!"));
  }
}
