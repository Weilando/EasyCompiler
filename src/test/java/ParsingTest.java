import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsingTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesFailParser = "src/test/resources/failParser/";

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

  @Test
  public void parseArithmeticComparisons() {
    String[] args = {"-compile", pathTestFilesCorrect + "ArithmeticComparisons.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseBooleanComparisons() {
    String[] args = {"-compile", pathTestFilesCorrect + "BooleanComparisons.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseBooleanExpressions() {
    String[] args = {"-compile", pathTestFilesCorrect + "BooleanExpressions.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseBooleanAnd() {
    String[] args = {"-compile", pathTestFilesCorrect + "BooleanAnd.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseBooleanOr() {
    String[] args = {"-compile", pathTestFilesCorrect + "BooleanOr.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseBooleanVariable() {
    String[] args = {"-compile", pathTestFilesCorrect + "BooleanVariable.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseEmptyStatements() {
    String[] args = {"-compile", pathTestFilesCorrect + "EmptyStatements.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIf() {
    String[] args = {"-compile", pathTestFilesCorrect + "If.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIfElse() {
    String[] args = {"-compile", pathTestFilesCorrect + "IfElse.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIfElseComplex() {
    String[] args = {"-compile", pathTestFilesCorrect + "IfElseComplex.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIfWhileIf() {
    String[] args = {"-compile", pathTestFilesCorrect + "IfWhileIf.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIntVariable() {
    String[] args = {"-compile", pathTestFilesCorrect + "IntVariable.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseIntCalculations() {
    String[] args = {"-compile", pathTestFilesCorrect + "IntCalculations.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parsePrint() {
    String[] args = {"-compile", pathTestFilesCorrect + "Print.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseUnaries() {
    String[] args = {"-compile", pathTestFilesCorrect + "Unaries.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseWhile() {
    String[] args = {"-compile", pathTestFilesCorrect + "While.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  @Test
  public void parseWhileComplex() {
    String[] args = {"-compile", pathTestFilesCorrect + "WhileComplex.easy"};
    EasyCompiler.main(args);
    assertFalse(outContent.toString().contains("Error"));
    assertFalse(errContent.toString().contains("Error"));
    assertTrue(outContent.toString().contains("Successful!"));
  }

  //----------------------------------------------------
  // Throw parser-errors while parsing files with errors
  //----------------------------------------------------
  @Test
  public void errorMissingBrace() {
    String[] args = {"-compile", pathTestFilesFailParser + "FailMissingBrace.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorMissingBraces() {
    String[] args = {"-compile", pathTestFilesFailParser + "FailMissingBraces.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorMissingMainMethod() {
    String[] args = {"-compile", pathTestFilesFailParser + "FailMissingMainMethod.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorMissingParenthesis() {
    String[] args = {"-compile", pathTestFilesFailParser + "FailMissingParenthesis.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }

  @Test
  public void errorMissingSemicolon() {
    String[] args = {"-compile", pathTestFilesFailParser + "FailMissingSemicolon.easy"};
    EasyCompiler.main(args);
    assertTrue(outContent.toString().contains("Parser-Error"));
    assertFalse(outContent.toString().contains("Successful!"));
  }
}
