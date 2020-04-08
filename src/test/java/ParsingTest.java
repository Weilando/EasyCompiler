import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsingTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";

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

  //-----------------------------------
  // Parse correct files without errors
  //-----------------------------------
  @Test
  public void parseMinimalExample() {
    String[] args = {"-compile", pathTestFilesCorrect + "Minimal.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }
}
