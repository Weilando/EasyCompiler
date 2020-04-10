import lexer.Lexer;
import lexer.LexerException;
import lineevaluation.LineEvaluator;
import node.Start;
import parser.Parser;
import parser.ParserException;
import typecheck.ExpressionCache;
import typecheck.SymbolTable;
import typecheck.TypeChecker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;

public class EasyCompiler {
  final private boolean DEBUG = false;
  private final String fileNameAndPath;
  final private String fileName;
  private Start ast;
  private SymbolTable symbolTable;
  private ArrayList<String> code;
  private ExpressionCache expressionCache;
  private boolean parseErrorOccurred = false;

  public EasyCompiler(String fileName) {
    if (!isValidFileName(fileName)) {
      System.out.println("Invalid file name.");
      System.exit(0);
    }

    this.fileNameAndPath = fileName;
    this.fileName = extractFileName(fileName);
  }

  public static void main(String[] args) {
    final String correctCall = "java EasyCompiler [-compile|-typeCheck|-parse] <Filename.easy>";
    EasyCompiler easyCompiler;

    if (args.length == 2) {
      easyCompiler = new EasyCompiler(args[1]);

      switch (args[0]) {
        case "-compile":
          easyCompiler.compile();
          break;
        case "-typeCheck":
          easyCompiler.typeCheck();
          break;
        case "-parse":
          easyCompiler.parse();
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
  void compile() {
    if (generateCode()) {
      writeOutputFile();
      System.out.println("Successful!");
    }
  }

  boolean generateCode() {
    if (parse() && typeCheck()) {
      System.out.println(String.format("Compiling %s", fileName));
      return true;
    }
    return false;
  }

  //---------
  // Analysis
  //---------
  boolean parse() {
    if ((ast == null) && parseErrorOccurred) {
      return false;
    }

    try {
      this.ast = generateAST(fileNameAndPath);
      printAST();
      LineEvaluator.setLines(this.ast);
    } catch (IOException e) {
      System.out.println(String.format("Input-Error: An error occurred while reading input file \"%s\".", fileNameAndPath));
      System.out.println(e.toString());
      return false;
    } catch (LexerException e) {
      System.out.println("Lexer-Error: An error occurred while initializing the lexer.\n");
      System.out.println(e.toString());
      return false;
    } catch (ParserException e) {
      System.out.println(String.format("Parser-Error: %s", e.toString()));
      return false;
    }
    return true;
  }

  boolean typeCheck() {
    if (!parse()) {
      return false;
    } else if (this.symbolTable != null) {
      return true;
    }

    TypeChecker typeChecker = new TypeChecker();
    this.ast.apply(typeChecker);

    if (!typeChecker.errorsOccurred()) {
      if (DEBUG) {
        typeChecker.printSymbolTable();
      }
      this.symbolTable = typeChecker.getSymbolTable();
      this.expressionCache = typeChecker.getExpressionCache();
      return true;
    }
    return false;
  }

  //--------
  // Helpers
  //--------
  static String extractFileName(String fileName) { // removes path to the input-file
    return new File(fileName).getName().replaceAll(".easy", "");
  }

  static boolean isValidFileName(String fileName) {
    File file = new File(fileName);
    return file.getName().matches("[a-zA-Z]\\w*\\.easy");
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

  boolean writeOutputFile() {
    if (this.code == null) {
      System.out.println("The output file could not be written, because no code was generated.");
      return false;
    }

    return true;
  }
}
