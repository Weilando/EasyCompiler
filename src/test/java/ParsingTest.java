import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsingTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesFailParser = "src/test/resources/failParser/";

  //-----------------------------------
  // Parse correct files without errors
  //-----------------------------------
  @Test
  public void parseMinimalExample() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Minimal.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseArithmeticComparisons() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "ArithmeticComparisons.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseBooleanComparisons() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanComparisons.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseBooleanExpressions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanExpressions.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseBooleanAnd() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanAnd.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseBooleanOr() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanOr.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseBooleanVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanVariable.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseEmptyStatements() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "EmptyStatements.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIf() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "If.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIfElse() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfElse.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIfElseComplex() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfElseComplex.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIfWhileIf() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfWhileIf.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIntVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IntVariable.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseIntCalculations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IntCalculations.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parsePrint() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Print.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseUnaries() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Unaries.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseWhile() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "While.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseWhileComplex() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "WhileComplex.easy");
    assertTrue(easyCompiler.parse());
  }

  //----------------------------------------------------
  // Throw parser-errors while parsing files with errors
  //----------------------------------------------------
  @Test
  public void errorMissingBrace() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailParser + "FailMissingBrace.easy");
    assertFalse(easyCompiler.parse());
  }

  @Test
  public void errorMissingBraces() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailParser + "FailMissingBraces.easy");
    assertFalse(easyCompiler.parse());
  }

  @Test
  public void errorMissingMainMethod() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailParser + "FailMissingMainMethod.easy");
    assertFalse(easyCompiler.parse());
  }

  @Test
  public void errorMissingParenthesis() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailParser + "FailMissingParenthesis.easy");
    assertFalse(easyCompiler.parse());
  }

  @Test
  public void errorMissingSemicolon() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailParser + "FailMissingSemicolon.easy");
    assertFalse(easyCompiler.parse());
  }
}
