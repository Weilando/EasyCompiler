import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

import codegeneration.CodeCache;
import codegeneration.CodeGenerator;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import lexer.Lexer;
import lexer.LexerException;
import lineevaluation.LineEvaluator;
import livenessanalysis.LivenessAnalyzer;
import node.Start;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import parser.Parser;
import parser.ParserException;
import symboltable.SymbolTable;
import symboltable.SymbolTableBuilder;
import typecheck.TypeChecker;

/** Compiler for the Easy language. */
public class EasyCompiler {
  private boolean verbose;
  private final Path sourceFilePath;
  private Start ast;
  private ArrayList<String> code;
  private SymbolTable symbolTable;
  private SymbolTableBuilder symbolTableBuilder;
  private TypeChecker typeChecker;
  private boolean parseErrorOccurred = false;

  /**
   * Constructor for the EasyCompiler class. Please note that each instance
   * handles a single source file.
   *
   * @param sourceFilePath Path to the source file
   */
  public EasyCompiler(String sourceFilePath) {
    this.verbose = false;

    if (!isValidFileName(sourceFilePath)) {
      System.out.println("Invalid file path '%s'.".formatted(sourceFilePath));
      System.exit(0);
    }
    this.sourceFilePath = Paths.get(sourceFilePath);
  }

  /**
   * Constructor for the EasyCompiler class. Please note that each instance
   * handles a single source file.
   *
   * @param sourceFilePath Path to the source file
   * @param verbose        Flag indicating if additional logs occur
   */
  public EasyCompiler(String sourceFilePath, boolean verbose) {
    this.verbose = verbose;

    if (!isValidFileName(sourceFilePath)) {
      System.out.println("Invalid file path '%s'.".formatted(sourceFilePath));
      System.exit(0);
    }
    this.sourceFilePath = Paths.get(sourceFilePath);
  }

  /* Generates options for the command line interface parser. */
  private static Options generateCommandLineOptions() {
    OptionGroup mainCommandGroup = new OptionGroup();
    mainCommandGroup.setRequired(true);
    mainCommandGroup.addOption(Option.builder("c")
        .longOpt("compile")
        .hasArg(true)
        .argName("filePath")
        .desc("Compile the given source file. Includes parsing and type-checking.")
        .build());
    mainCommandGroup.addOption(Option.builder("p")
        .longOpt("parse")
        .hasArg(true)
        .argName("filePath")
        .desc("Parses the source file and checks the syntax.")
        .build());
    mainCommandGroup.addOption(Option.builder("t")
        .longOpt("typeCheck")
        .hasArg(true)
        .argName("filePath")
        .desc("Check types for the source file. Includes parsing.")
        .build());
    mainCommandGroup.addOption(Option.builder("l")
        .longOpt("liveness")
        .hasArg(true)
        .argName("filePath")
        .desc("Liveness analysis for each function in the source file. Includes type checking.")
        .build());
    mainCommandGroup.addOption(Option.builder("h")
        .longOpt("help")
        .hasArg(false)
        .desc("Prints available options and commands.")
        .build());

    Options options = new Options();
    options.addOptionGroup(mainCommandGroup);
    options.addOption("v", "verbose", false,
        "Enables debug outputs.");

    return options;
  }

  /* Prints the correct usage and available command line options. */
  private static void printCorrectCall(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    final String header = "Compiler and analyzer for the Easy language.";

    formatter.printHelp(80, "EasyCompiler", header,
        options, "", true);
  }

  /**
   * Checks command line arguments and starts the EasyCompiler.
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    Options options = generateCommandLineOptions();
    CommandLine parsedOptions;

    try {
      CommandLineParser cliParser = new DefaultParser();
      parsedOptions = cliParser.parse(options, args);

      EasyCompiler easyCompiler;
      String filePath;
      boolean verbose = parsedOptions.hasOption("v");
      if (parsedOptions.hasOption("h")) {
        printCorrectCall(options);
      } else if (parsedOptions.hasOption("c")) {
        filePath = parsedOptions.getOptionValue("c");
        easyCompiler = new EasyCompiler(filePath, verbose);
        easyCompiler.compile();
      } else if (parsedOptions.hasOption("p")) {
        filePath = parsedOptions.getOptionValue("p");
        easyCompiler = new EasyCompiler(filePath, verbose);
        easyCompiler.parse();
      } else if (parsedOptions.hasOption("t")) {
        filePath = parsedOptions.getOptionValue("t");
        easyCompiler = new EasyCompiler(filePath, verbose);
        easyCompiler.typeCheck();
      } else if (parsedOptions.hasOption("l")) {
        filePath = parsedOptions.getOptionValue("l");
        easyCompiler = new EasyCompiler(filePath, verbose);
        easyCompiler.liveness();
      }
    } catch (ParseException e) {
      printCorrectCall(options);
    } catch (Exception e) {
      System.out.println("An unexpected error occurred: %s".formatted(e.getMessage()));
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
          codeCache, getProgramName(), this.symbolTable);
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
      LivenessAnalyzer analyzer = new LivenessAnalyzer(ast, this.symbolTable);

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
      this.ast = generateAbstractSyntaxTree();
      printAbstractSyntaxTree();
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

  boolean buildSymbolTable() {
    if (!parse()) {
      return false;
    } else if (this.symbolTableBuilder != null) {
      return true;
    }

    this.symbolTableBuilder = new SymbolTableBuilder();
    this.ast.apply(symbolTableBuilder);
    this.symbolTable = symbolTableBuilder.getSymbolTable();

    return !this.symbolTableBuilder.errorsOccurred();
  }

  boolean typeCheck() {
    if (!parse() || !buildSymbolTable()) {
      return false;
    } else if (this.typeChecker != null) {
      return true;
    }

    this.typeChecker = new TypeChecker(this.symbolTable);
    this.ast.apply(this.typeChecker);

    return !this.typeChecker.errorsOccurred();
  }

  int getSymbolErrorNumber() {
    buildSymbolTable();
    return symbolTableBuilder.getErrorNumber();
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

  Start generateAbstractSyntaxTree() throws IOException, LexerException, ParserException {
    FileReader fileReader = new FileReader(this.sourceFilePath.toFile());
    PushbackReader pushbackReader = new PushbackReader(fileReader);
    Lexer lexer = new Lexer(pushbackReader);
    Parser parser = new Parser(lexer);
    return parser.parse();
  }

  private void printAbstractSyntaxTree() {
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
