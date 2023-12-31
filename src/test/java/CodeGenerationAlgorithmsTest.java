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
public class CodeGenerationAlgorithmsTest {
  private final String pathAlgorithms = "src/test/resources/algorithms/";
  private final File workingDirectory = new File(pathAlgorithms);

  private final Runtime runtime = Runtime.getRuntime();
  private Process classProcess;
  private String inputStreamString;

  private void generateJasminFile(String testName) {
    String testFilePath = String.format("%s%s.easy", pathAlgorithms, testName);
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

  // ------------------------
  // Test correct algorithms
  // ------------------------
  @Test
  public void resultBinomial() throws InterruptedException {
    String testName = "Binomial";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("210", inputStreamString);
  }

  @Test
  public void resultEuclid() throws InterruptedException {
    String testName = "Euclid";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("14\ntrue\n3\n4", inputStreamString);
  }

  @Test
  public void resultFibonacci() throws InterruptedException {
    String testName = "Fibonacci";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("0\n1\n1\n2\n3\n5\n8\n13\n21\n34\n55\n89", inputStreamString);
  }

  @Test
  public void resultSarrus() throws InterruptedException {
    String testName = "Sarrus";
    generateJasminFile(testName);
    generateClassFromJasminFile(testName);
    setupClassExecutionProcess(testName);

    assertEquals(0, classProcess.waitFor());
    assertEquals("-8", inputStreamString);
  }
}
