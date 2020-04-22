import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class EasyCompilerTest {
  private final String correctCall = "java EasyCompiler [-compile|-liveness|-typeCheck|-parse] <Filename.easy>";

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

  //-----------------
  // Test args-checks
  //-----------------
  @Test
  public void callWithoutArgsShouldFail() {
    String[] args = {};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains(correctCall));
  }

  @Test
  public void callWithCompileFlagAndMissingFilenameShouldFail() {
    String[] args = {"-compile"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains(correctCall));
  }

  @Test
  public void callWithCompileFlagAndFilenameShouldWork() {
    String[] args = {"-compile", "src/test/resources/failParser/FailMissingBrace.easy"}; // An incorrect file was chosen to avoid output-files
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains(correctCall));
  }

  @Test
  public void callWithLivenessFlagAndFilenameShouldWork() {
    String[] args = {"-liveness", pathMinimalProgram}; // An incorrect file was chosen to avoid output-files
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains(correctCall));
    assertTrue(outContent.toString().contains("Registers: 0"));
  }

  @Test
  public void callWithParseFlagAndFilenameShouldWork() {
    String[] args = {"-parse", pathMinimalProgram};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains(correctCall));
  }

  @Test
  public void callWithTypeCheckFlagAndFilenameShouldWork() {
    String[] args = {"-typeCheck", pathMinimalProgram};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains(correctCall));
  }

  //--------------------
  // Test error-messages
  //--------------------
  @Test
  public void typeErrorMessageShouldBePrinted() {
    String[] args = {"-compile", "src/test/resources/failTypeCheck/FailBadConditions.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error at (6,6): "));
    assertTrue(outContent.toString().contains("Type-Error at (13,9): "));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseErrorMessageShouldBePrinted() {
    String[] args = {"-compile", "src/test/resources/failParser/FailMissingBrace.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error: "));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  //---------------------
  // Test isValidFileName
  //---------------------
  @Test
  public void emptyStringIsNoValidFileName() {
    String invalidFileName = "";
    assertFalse(EasyCompiler.isValidFileName(invalidFileName));
  }

  @Test
  public void fileExtensionIsInvalidFileName() {
    String invalidFileName = ".easy";
    assertFalse(EasyCompiler.isValidFileName(invalidFileName));
  }

  @Test
  public void nameWithoutFileExtensionIsInvalidFileName() {
    String invalidFileName = "Program";
    assertFalse(EasyCompiler.isValidFileName(invalidFileName));
  }

  @Test
  public void nameWithoutLetterIsInvalidFileName() {
    String invalidFileName = "1.easy";
    assertFalse(EasyCompiler.isValidFileName(invalidFileName));
  }

  @Test
  public void validFileName() {
    String validFileName = "Program_1.easy";
    assertTrue(EasyCompiler.isValidFileName(validFileName));
  }

  //--------------------
  // Test helper methods
  //--------------------
  @Test
  public void generateCorrectJasminFileNameAndPath() {
    EasyCompiler easyCompiler = new EasyCompiler(pathMinimalProgram);
    assertEquals("src/test/resources/correct/Minimal.j", easyCompiler.getJasminFileNameAndPath());
  }

  @Test
  public void extractCorrectProgramName() {
    EasyCompiler easyCompiler = new EasyCompiler(pathMinimalProgram);
    assertEquals("Minimal", easyCompiler.getProgramName());
  }
}
