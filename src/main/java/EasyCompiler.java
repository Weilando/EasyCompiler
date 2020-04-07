import java.io.File;

public class EasyCompiler {
  final private boolean DEBUG = false;
  private final String fileNameAndPath;
  final private String fileName;

  public EasyCompiler(String fileName) {
    if (!isValidFileName(fileName)) {
      System.out.println("Invalid file name.");
      System.exit(0);
    }

    this.fileNameAndPath = fileName;
    this.fileName = extractFileName(fileName);
  }

  public static void main(String[] args) {
    final String correctCall = "java EasyCompiler -compile <Filename.easy>";
    EasyCompiler easyCompiler;

    if (args.length == 2) {
      easyCompiler = new EasyCompiler(args[1]);

      switch (args[0]) {
        case "-compile":
          easyCompiler.compile();
          break;
        default:
          System.out.println(correctCall);
      }
    } else {
      System.out.println(correctCall);
    }
  }

  //--------
  // Helpers
  //--------
  static boolean isValidFileName(String fileName) {
    File file = new File(fileName);
    return file.getName().matches("[a-zA-Z]\\w*\\.easy");
  }

  static String extractFileName(String fileName) { // removes path to the input-file
    return new File(fileName).getName().replaceAll(".easy", "");
  }

  //------------
  // Compilation
  //------------
  public void compile() {
    System.out.println(String.format("Compiling %s", fileName));
  }
}
