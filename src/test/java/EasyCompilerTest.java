import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EasyCompilerTest {
  private final String correctCall = "java EasyCompiler -compile <Filename.easy>";
  private final String validFileName = "Program_1.easy";

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
    String[] args = {"-compile "};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains(correctCall));
  }

  @Test
  public void callWithFlagAndFilenameShouldWork() {
    String[] args1 = {"-compile", validFileName};
    EasyCompiler.main(args1);
    assertFalse(outContent.toString().contains(correctCall));
  }

  //--------------------------
  // Tests for isValidFileName
  //--------------------------
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
    assertTrue(EasyCompiler.isValidFileName(validFileName));
  }
}
