import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckTest {
  private final String pathTestFilesFailTypeCheck = "src/test/resources/failTypeCheck/";

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

  //---------------------------------------------------------
  // Throw type-errors while parsing files with invalid types
  //---------------------------------------------------------
  @Test
  public void errorDuplicateDeclaration() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailDuplicateDeclaration.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorNoDeclaration() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailNoDeclaration.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorSeveralTypes() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailSeveralTypes.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorWrongAssignment() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailWrongAssignment.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorWrongWhileHead() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailWrongWhileHead.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void multipleTypeErrors() {
    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailMultipleTypeErrors.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
    assertEquals(9, (outContent.toString().split("Type-Error", -1).length) - 1);
  }
}
