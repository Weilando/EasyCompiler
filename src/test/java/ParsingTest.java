import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParsingTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesFailParser = "src/test/resources/failParser/";
  private final String pathTestFilesFailTypeCheck = "src/test/resources/failTypeCheck/";
  private final String pathTestFilesLiveness = "src/test/resources/liveness/";

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
  public void parseEmptyStatements() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "EmptyStatements.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseFloat() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Float.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseFloatWithCast() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "FloatWithCast.easy");
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
  public void parseInt() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Int.easy");
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

  //--------------------------------
  // Parse algorithms without errors
  //--------------------------------
  @Test
  public void parseBinomial() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Binomial.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseEuclid() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Euclid.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseFibonacci() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Fibonacci.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseSarrus() {
    EasyCompiler easyCompiler = new EasyCompiler(pathAlgorithms + "Sarrus.easy");
    assertTrue(easyCompiler.parse());
  }

  //--------------------------------------
  // Parse type-check tests without errors
  //--------------------------------------
  @Test
  public void parseFailBadConditions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadConditions.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseFailBadExpressions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadExpressions.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseFailBadVariableOperations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadVariableOperations.easy");
    assertTrue(easyCompiler.parse());
  }

  //------------------------------------
  // Parse liveness tests without errors
  //------------------------------------
  @Test
  public void parseNoVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "noVariable.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseOneVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "oneVariable.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseTwoVariablesBothLive() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "twoVariablesBothLive.easy");
    assertTrue(easyCompiler.parse());
  }

  @Test
  public void parseTwoVariablesOneLive() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesLiveness + "twoVariablesOneLive.easy");
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
