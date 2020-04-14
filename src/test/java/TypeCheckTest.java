import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TypeCheckTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final String pathTestFilesFailTypeCheck = "src/test/resources/failTypeCheck/";

  //-----------------------------------
  // Check correct files without errors
  //-----------------------------------
  @Test
  public void checkMinimalExample() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Minimal.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkArithmeticComparisons() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "ArithmeticComparisons.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkBooleanComparisons() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanComparisons.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkBooleanExpressions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanExpressions.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkBooleanAnd() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanAnd.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkBooleanOr() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanOr.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkBooleanVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "BooleanVariable.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkEmptyStatements() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "EmptyStatements.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkFloatCalculations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "FloatCalculations.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkFloatVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "FloatVariable.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIf() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "If.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIfElse() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfElse.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIfElseComplex() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfElseComplex.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIfWhileIf() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IfWhileIf.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIntVariable() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IntVariable.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkIntCalculations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "IntCalculations.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkPrint() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Print.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkUnaries() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "Unaries.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkWhile() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "While.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  @Test
  public void checkWhileComplex() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesCorrect + "WhileComplex.easy");
    assertTrue(easyCompiler.typeCheck());
  }

  //---------------------------------------------------------
  // Throw type-errors while parsing files with invalid types
  //---------------------------------------------------------
  @Test
  public void errorBadConditions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadConditions.easy");
    assertFalse(easyCompiler.typeCheck());
    assertEquals(8, easyCompiler.getTypeErrorNumber());
  }

  @Test
  public void errorBadExpressions() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadExpressions.easy");
    assertFalse(easyCompiler.typeCheck());
    assertEquals(16, easyCompiler.getTypeErrorNumber());
  }

  @Test
  public void errorBadVariableOperations() {
    EasyCompiler easyCompiler = new EasyCompiler(pathTestFilesFailTypeCheck + "FailBadVariableOperations.easy");
    assertFalse(easyCompiler.typeCheck());
    assertEquals(16, easyCompiler.getTypeErrorNumber());
  }
}
