import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CodeGenerationTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final File workingDirectory = new File(pathTestFilesCorrect);

  private final Runtime runtime = Runtime.getRuntime();
  private Process classProcess;
  private String inputStreamString;

  private void generateJasminFile(String testName) {
    String testFilePath = String.format("%s%s.easy", pathTestFilesCorrect, testName);
    EasyCompiler easyCompiler = new EasyCompiler(testFilePath);
    easyCompiler.compile();
  }

  private void generateClassFromJasminFile(String testName) {
    try {
      String command = String.format("java -jar ../../../../libs/jasmin.jar %s.j", testName);
      Process generateProcess = runtime.exec(command, null, workingDirectory);
      generateProcess.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void setupClassExecutionProcess(String testName) {
    try {
      String command = String.format("java %s", testName);
      classProcess = runtime.exec(command, null, workingDirectory);
      inputStreamString = new BufferedReader(new InputStreamReader(classProcess.getInputStream()))
          .lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  public void cleanTestFiles() {
    try {
      Process process = runtime.exec("sh clean.sh", null, new File("src/test/resources"));
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  // ----------------------
  // Test correct snippets
  // ----------------------
  @Test
  public void resultArithmeticComparisons() throws InterruptedException {
    String testName = "ArithmeticComparisons";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals(
        "truefalsefalsetruefalse\ntruetruefalsetruetruefalse\ntruefalsefalsetrue\ntruefalsefalsefalse\ntruefalsetruetrue\ntruefalsetruefalse",
        inputStreamString);
  }

  @Test
  public void resultMinimalExample() throws InterruptedException, IOException {
    String testName = "Minimal";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
  }

  @Test
  public void resultBooleanAnd() throws InterruptedException {
    String testName = "BooleanAnd";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("true\nfalse\nfalse\nfalse\ntrue\nfalse\nfalse", inputStreamString);
  }

  @Test
  public void resultBooleanComparisons() throws InterruptedException {
    String testName = "BooleanComparisons";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("truefalsefalsetruefalsetruefalse\nfalsetruetruefalsetruetruefalse",
        inputStreamString);
  }

  @Test
  public void resultBooleanExpressions() throws InterruptedException {
    String testName = "BooleanExpressions";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("truetruetruetrue\ntruetruetruetrue\ntruefalsetruefalse", inputStreamString);
  }

  @Test
  public void resultBooleanOr() throws InterruptedException {
    String testName = "BooleanOr";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("true\ntrue\ntrue\nfalse\ntrue\ntrue\ntrue", inputStreamString);
  }

  @Test
  public void resultComments() throws InterruptedException {
    String testName = "Comments";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("1\n3\n5\n6", inputStreamString);
  }

  @Test
  public void resultFloat() throws InterruptedException {
    String testName = "Float";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("3.1415927\n-2.0\n2.0\n2.0\n3.4\n-1.6\n7.0\n3.5\n-5.4\n3.2\n16.74\n2.7",
        inputStreamString);
  }

  @Test
  public void resultFloatWithCast() throws InterruptedException {
    String testName = "FloatWithCast";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals(
        "3.0\n-2.0\n3.0\n1.5\n6.4\n3.5\n5.0\n-4.0\n10.0\n-0.5\n1414.014.014.014.014.014.014.0",
        inputStreamString);
  }

  @Test
  public void resultFunctionReturnTypes() throws InterruptedException {
    String testName = "FunctionReturnTypes";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals(
        "2\n1.0\nabc\ntrue",
        inputStreamString);
  }

  @Test
  public void resultFunctionArguments() throws InterruptedException {
    String testName = "FunctionArguments";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals(
        "true\n1.0\n2\nabc\ntrue\n1.0\n2\nabc",
        inputStreamString);
  }

  @Test
  public void resultIf() throws InterruptedException {
    String testName = "If";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("10", inputStreamString);
  }

  @Test
  public void resultIfElse() throws InterruptedException {
    String testName = "IfElse";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("0", inputStreamString);
  }

  @Test
  public void resultIfElseComplex() throws InterruptedException {
    String testName = "IfElseComplex";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("0\n0", inputStreamString);
  }

  @Test
  public void resultInt() throws InterruptedException {
    String testName = "Int";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("42-1421\n-21-224-3", inputStreamString);
  }

  @Test
  public void resultUnaries() throws InterruptedException {
    String testName = "Unaries";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("-2\n3\n0\n0\nfalse\ntrue\n-3\n6\n-3\n3\ntrue\ntrue\nfalse", inputStreamString);
  }

  @Test
  public void resultPrint() throws InterruptedException {
    String testName = "Print";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("falsetrue\n3.14162.1\n42", inputStreamString);
  }

  @Test
  public void resultStringComparisons() throws InterruptedException {
    String testName = "StringComparisons";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("falsetruetruefalse\ntruefalsefalsetrue", inputStreamString);
  }

  @Test
  public void resultStringConcats() throws InterruptedException {
    String testName = "StringConcats";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("aaaccacc\natrueafalsea2a3a0.5a0.4\ntrue10.4c\ntrue0.52a", inputStreamString);
  }

  @Test
  public void resultStringExpressions() throws InterruptedException {
    String testName = "StringExpressions";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("Hello world!", inputStreamString);
  }

  @Test
  public void resultWhile() throws InterruptedException {
    String testName = "While";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("4\n3\n2\n1\n0", inputStreamString);
  }
}
