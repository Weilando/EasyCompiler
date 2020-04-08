import lexer.Lexer;
import lexer.LexerException;
import node.Start;
import parser.Parser;
import parser.ParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

public class EasyCompiler {
  final private boolean DEBUG = false;
  private final String fileNameAndPath;
  final private String fileName;
  private Start ast;

  public EasyCompiler(String fileName) {
    if (!isValidFileName(fileName)) {
      System.out.println("Invalid file name.");
      System.exit(0);
    }

    this.fileNameAndPath = fileName;
    this.fileName = extractFileName(fileName);

    try {
      this.ast = generateAST(fileNameAndPath);
      printAST();
    } catch (IOException e) {
      System.out.println(String.format("Input-Error: An error occured while reading input file \"%s\".", fileNameAndPath));
      System.out.println(e.toString());
    } catch (LexerException e) {
      System.out.println("Lexer-Error: An error occured while initializing the lexer.\n");
      System.out.println(e.toString());
    } catch (ParserException e) {
      System.out.println(String.format("Parser-Error: %s", e.toString()));
    }
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

  //------------
  // Compilation
  //------------
  public void compile() {
    System.out.println(String.format("Compiling %s", fileName));
    System.out.println("Successful!");
  }

  static boolean isValidFileName(String fileName) {
    File file = new File(fileName);
    return file.getName().matches("[a-zA-Z]\\w*\\.easy");
  }

  //--------
  // Helpers
  //--------
  static String extractFileName(String fileName) { // removes path to the input-file
    return new File(fileName).getName().replaceAll(".easy", "");
  }

  static Start generateAST(String fileName) throws IOException, LexerException, ParserException {
    FileReader fileReader = new FileReader(fileName);
    PushbackReader pushbackReader = new PushbackReader(fileReader);
    Lexer lexer = new Lexer(pushbackReader);
    Parser parser = new Parser(lexer);
    return parser.parse();
  }

  private void printAST() {
    if ((this.ast != null) && DEBUG) {
      ASTPrinter printer = new ASTPrinter();
      this.ast.apply(printer);
    }
  }
}
