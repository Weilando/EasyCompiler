import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FileHandlerTest {
  @Test
  public void emptyStringIsNoValidFilePath() {
    String invalidFilePath = "";
    assertFalse(FileHandler.isValidFilePath(invalidFilePath));
  }

  @Test
  public void fileExtensionIsInvalidFilePath() {
    String invalidFilePath = ".easy";
    assertFalse(FileHandler.isValidFilePath(invalidFilePath));
  }

  @Test
  public void nameWithoutFileExtensionIsInvalidFilePath() {
    String invalidFilePath = "Program";
    assertFalse(FileHandler.isValidFilePath(invalidFilePath));
  }

  @Test
  public void nameWithoutLetterIsInvalidFilePath() {
    String invalidFilePath = "1.easy";
    assertFalse(FileHandler.isValidFilePath(invalidFilePath));
  }

  @Test
  public void validFileName() {
    String validFilePath = "Program_1.easy";
    assertTrue(FileHandler.isValidFilePath(validFilePath));
  }

  @Test
  public void validFilePath() {
    String validFilePath = "directory/Program_1.easy";
    assertTrue(FileHandler.isValidFilePath(validFilePath));
  }

  @Test
  public void generateCorrectJasminFileNameAndPath() {
    FileHandler fileHandler = new FileHandler("src/test/resources/correct/Minimal.easy");
    assertEquals("src/test/resources/correct/Minimal.j", fileHandler.getJasminFileNameAndPath());
  }

  @Test
  public void extractCorrectProgramName() {
    FileHandler fileHandler = new FileHandler("src/test/resources/correct/Minimal.easy");
    assertEquals("Minimal", fileHandler.getProgramName());
  }
}
