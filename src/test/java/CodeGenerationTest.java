import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CodeGenerationTest {
  private final String pathTestFilesCorrect = "src/test/resources/correct/";
  private final File workingDictionary = new File(pathTestFilesCorrect);

  private final Runtime runtime = Runtime.getRuntime();

  private void generateClassFromJasmin(String fileName) {
    try {
      String command = String.format("java -jar ../../../../libs/Jasmin.jar %s.j", fileName);
      Process pr = runtime.exec(command, null, workingDictionary);
      pr.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  public void cleanTestFiles() {
    try {
      Process pr = runtime.exec("sh clean.sh", null, workingDictionary);
      pr.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  //----------------------
  // Test correct snippets
  //----------------------
  @Test
  public void resultArithmeticComparisons() throws IOException, InterruptedException {
    String testName = "ArithmeticComparisons";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("true\nfalse\nfalse\ntrue\nfalse\ntrue\ntrue\nfalse\ntrue\ntrue\nfalse\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\nfalse\nfalse\ntrue\nfalse\ntrue\ntrue\ntrue\nfalse\ntrue\nfalse", inputStream);
  }

  @Test
  public void resultMinimalExample() throws IOException, InterruptedException {
    String testName = "Minimal";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    assertEquals(0, pr.waitFor());
  }

  @Test
  public void resultBooleanAnd() throws IOException, InterruptedException {
    String testName = "BooleanAnd";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("true\nfalse\nfalse\nfalse\ntrue\nfalse\nfalse", inputStream);
  }

  @Test
  public void resultBooleanComparisons() throws IOException, InterruptedException {
    String testName = "BooleanComparisons";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("true\nfalse\nfalse\ntrue\nfalse\ntrue\nfalse\nfalse\ntrue\ntrue\nfalse\ntrue\ntrue\nfalse", inputStream);
  }

  @Test
  public void resultBooleanExpressions() throws IOException, InterruptedException {
    String testName = "BooleanExpressions";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("true\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\ntrue\nfalse\ntrue\nfalse", inputStream);
  }

  @Test
  public void resultBooleanOr() throws IOException, InterruptedException {
    String testName = "BooleanOr";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("true\ntrue\ntrue\nfalse\ntrue\ntrue\ntrue", inputStream);
  }

  @Test
  public void resultIf() throws IOException, InterruptedException {
    String testName = "If";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("10", inputStream);
  }

  @Test
  public void resultIfElse() throws IOException, InterruptedException {
    String testName = "IfElse";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("0", inputStream);
  }

  @Test
  public void resultIfElseComplex() throws IOException, InterruptedException {
    String testName = "IfElseComplex";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("0\n0", inputStream);
  }

  @Test
  public void resultIntCalculations() throws IOException, InterruptedException {
    String testName = "IntCalculations";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("42\n41\n-1\n-19\n35\n35", inputStream);
  }

  @Test
  public void resultUnaries() throws IOException, InterruptedException {
    String testName = "Unaries";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("-2\n3\n0\n0\nfalse\ntrue\n-3\n6\n-3\n3\ntrue\ntrue\nfalse", inputStream);
  }

  @Test
  public void resultPrint() throws IOException, InterruptedException {
    String testName = "Print";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("42\nfalsetrue", inputStream);
  }

  @Test
  public void resultWhile() throws IOException, InterruptedException {
    String testName = "While";
    EasyCompiler easyCompiler = new EasyCompiler(String.format("%s%s%s", pathTestFilesCorrect, testName, ".easy"));
    easyCompiler.compile();
    generateClassFromJasmin(testName);

    Process pr = runtime.exec(String.format("java %s", testName), null, workingDictionary);
    String inputStream = new BufferedReader(new InputStreamReader(pr.getInputStream()))
        .lines().collect(Collectors.joining("\n"));

    assertEquals(0, pr.waitFor());
    assertEquals("4\n3\n2\n1\n0", inputStream);
  }
}
