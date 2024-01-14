import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import codegeneration.CodeCache;
import codegeneration.CodeGenerator;
import lexer.Lexer;
import lexer.LexerException;
import lineevaluation.LineEvaluator;
import livenessanalysis.LivenessAnalyzer;
import node.Start;
import parser.Parser;
import parser.ParserException;
import typecheck.TypeChecker;

/** Compiler for the Easy language. */
public class EasyCompiler {
  private final boolean verbose = false;
  private final Path sourceFilePath;
  private Start ast;
  private ArrayList<String> code;
  private TypeChecker typeChecker;
  private boolean parseErrorOccurred = false;

  /**
   * Constructor for the EasyCompiler class. Please note that each instance
   * handles a single source file.
   *
   * @param sourceFilePath Path to the source file
   */
  public EasyCompiler(String sourceFilePath) {
    if (!isValidFileName(sourceFilePath)) {
      System.out.println("Invalid file name.");
      System.exit(0);
    }

    this.sourceFilePath = Paths.get(sourceFilePath);
  }

  /**
   * Checks command line arguments and starts the EasyCompiler.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    final String correctCall = "java EasyCompiler -[compile|liveness|typeCheck|parse] <file>.easy";
    EasyCompiler easyCompiler;

    if (args.length == 2) {
      easyCompiler = new EasyCompiler(args[1]);

      switch (args[0]) {
        case "-compile":
          easyCompiler.compile();
          break;
        case "-liveness":
          easyCompiler.liveness();
          break;
        case "-typeCheck":
          easyCompiler.typeCheck();
          break;
        case "-parse":
          easyCompiler.parse();
          break;
        default:
          System.out.println("Unknown Option. Correct call:");
          System.out.println(correctCall);
      }
    } else {
      System.out.println("Missing argument. Correct call:");
      System.out.println(correctCall);
    }
  }

  // ------------
  // Compilation
  // ------------
  void compile() {
    if (generateCode() && writeOutputFile()) {
      System.out.println("Successful!");
    }
  }

  boolean generateCode() {
    if (parse() && typeCheck()) {
      CodeCache codeCache = new CodeCache();
      CodeGenerator codeGenerator = new CodeGenerator(
          codeCache, getProgramName(), this.typeChecker.getSymbolTable());
      ast.apply(codeGenerator);
      this.code = codeCache.getCode();

      return true;
    }
    return false;
  }

  // ---------
  // Analysis
  // ---------
  void liveness() {
    if (parse() && typeCheck()) {
      LivenessAnalyzer analyzer = new LivenessAnalyzer(ast, this.typeChecker.getSymbolTable());

      if (verbose) {
        analyzer.printGraph();
      }

      System.out.println(String.format("Registers: %d", analyzer.getMinimumRegisters()));
    }
  }

  boolean parse() {
    if ((ast == null) && parseErrorOccurred) {
      return false;
    }

    try {
      this.ast = generateAST();
      printAST();
      LineEvaluator.setLines(this.ast);
    } catch (IOException e) {
      String filePath = this.sourceFilePath.toString();
      System.out.println(
          "Input-Error: An error occurred while reading input file \"%s\".".formatted(filePath));
      System.out.println(e.toString());
      return false;
    } catch (LexerException e) {
      System.out.println("Lexer-Error: An error occurred while initializing the lexer.\n");
      System.out.println(e.toString());
      return false;
    } catch (ParserException e) {
      System.out.println(String.format("Parser-Error: %s", e.toString()));
      parseErrorOccurred = true;
      return false;
    }
    return true;
  }

  boolean typeCheck() {
    if (!parse()) {
      return false;
    } else if (this.typeChecker != null) {
      return true;
    }

    this.typeChecker = new TypeChecker();
    this.ast.apply(this.typeChecker);

    if (!this.typeChecker.errorsOccurred()) {
      if (verbose) {
        this.typeChecker.printSymbolTable();
      }
      return true;
    }
    return false;
  }

  int getTypeErrorNumber() {
    typeCheck();
    return this.typeChecker.getErrorNumber();
  }

  // --------
  // Helpers
  // --------
  String getProgramName() {
    return this.sourceFilePath.getFileName().toString().replaceAll(".easy", "");
  }

  String getJasminFileNameAndPath() {
    return this.sourceFilePath.toString().replace(".easy", ".j");
  }

  static boolean isValidFileName(String fileName) {
    File file = new File(fileName);
    return file.getName().matches("[a-zA-Z]\\w*\\.easy");
  }

  Start generateAST() throws IOException, LexerException, ParserException {
    FileReader fileReader = new FileReader(this.sourceFilePath.toFile());
    PushbackReader pushbackReader = new PushbackReader(fileReader);
    Lexer lexer = new Lexer(pushbackReader);
    Parser parser = new Parser(lexer);
    return parser.parse();
  }

  private void printAST() {
    if ((this.ast != null) && verbose) {
      ASTPrinter printer = new ASTPrinter();
      this.ast.apply(printer);
    }
  }

  boolean writeOutputFile() {
    if (this.code == null) {
      System.out.println("The output file could not be written, because no code was generated.");
      return false;
    }

    try {
      Path jasminFile = Paths.get(getJasminFileNameAndPath());
      Files.write(jasminFile, this.code, StandardCharsets.UTF_8, CREATE, TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.out.println("An error occurred while writing the output file.");
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
