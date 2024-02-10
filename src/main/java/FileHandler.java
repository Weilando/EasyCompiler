import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/** Handler for file operations for a single source file path. */
public class FileHandler {
  private final Path sourceFilePath;

  /**
   * Check if the source file exists and generate a FileHandler object.
   *
   * @param sourceFilePath The path to an Easy source file.
   */
  public FileHandler(String sourceFilePath) {
    if (!isValidFilePath(sourceFilePath)) {
      System.out.println("Invalid file path '%s'.".formatted(sourceFilePath));
      System.exit(0);
    }
    this.sourceFilePath = Paths.get(sourceFilePath);
  }

  /* Check if the given path exists and if the file name is valid. */
  static boolean isValidFilePath(String filePath) {
    File file = new File(filePath);
    return file.getName().matches("[a-zA-Z]\\w*\\.easy");
  }

  /* Get a string representation of the source file path. */
  String getFilePath() {
    return this.sourceFilePath.toString();
  }

  /* Extract the program name, i.e., the file name without suffix. */
  String getProgramName() {
    return this.sourceFilePath.getFileName().toString().replaceAll(".easy", "");
  }

  /* Generate a Jasmin file path, i.e., replace the suffix. */
  String getJasminFileNameAndPath() {
    return this.sourceFilePath.toString().replace(".easy", ".j");
  }

  /* Write the generated Jasmin code into a corresponding file. */
  boolean writeOutputFile(ArrayList<String> code) {
    if (code == null) {
      System.out.println("Cannot write output file, because no code was generated.");
      return false;
    }

    try {
      Path jasminFile = Paths.get(getJasminFileNameAndPath());
      Files.write(jasminFile, code, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.out.println("An error occurred while writing the output file.");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /* Generate a PushbackReader for the source file. */
  PushbackReader getPushbackReader() throws FileNotFoundException {
    FileReader fileReader = new FileReader(this.sourceFilePath.toFile());
    return new PushbackReader(fileReader);
  }
}
