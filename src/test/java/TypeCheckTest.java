import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckTest {
  private final String pathTestFilesFailTypeCheck = "src/test/resources/failTypeCheck/";

  //---------------------------------------------------------
  // Throw type-errors while parsing files with invalid types
  //---------------------------------------------------------
  @Test
  public void errorDuplicateDeclaration() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailDuplicateDeclaration.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void errorNoDeclaration() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailNoDeclaration.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void errorSeveralTypes() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailSeveralTypes.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void errorIfConditionIsIntVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailIFConditionIsIntVariable.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void errorWhileConditionIsInt() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailWhileConditionIsInt.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void errorWrongAssignment() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailWrongAssignment.easy");
    assertFalse(easyCompiler.typeCheck());
  }

  @Test
  public void multipleTypeErrors() {
    final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    final PrintStream originalOut = System.out;
    final PrintStream originalErr = System.err;
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));

    String[] args = {"-compile", pathTestFilesFailTypeCheck + "FailMultipleTypeErrors.easy"};
    EasyCompiler.main(args);

    assertTrue(outContent.toString().contains("Type-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
    assertEquals(9, (outContent.toString().split("Type-Error", -1).length) - 1);

    System.setOut(originalOut);
    System.setErr(originalErr);
  }
}
