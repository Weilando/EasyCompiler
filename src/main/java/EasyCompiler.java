import codegeneration.CodeGenerator;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import symboltable.Symbol;
import symboltable.SymbolTable;
import symboltable.SymbolTableBuilder;
import typecheck.TypeChecker;

/** Compiler for the Easy language. */
public class EasyCompiler {
  private boolean verbose;
  private Start ast;
  ArrayList<String> code;
  private SymbolTable symbolTable;
  private SymbolTableBuilder symbolTableBuilder;
  private TypeChecker typeChecker;
  private LivenessAnalyzer livenessAnalyzer;
  private LineEvaluator lineEvaluator;
  private boolean parseErrorOccurred = false;
  public FileHandler fileHandler;

  /**
   * Constructor for the EasyCompiler class. Please note that each instance
   * handles a single source file.
   *
   * @param sourceFilePath Path to the source file
   */
  public EasyCompiler(String sourceFilePath) {
    this.verbose = false;
    this.fileHandler = new FileHandler(sourceFilePath);
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
    this.fileHandler = new FileHandler(sourceFilePath);
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
        "Enables more detailed outputs.");

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
        easyCompiler.getMinimumRegistersPerFunction();
        easyCompiler.getUnusedArgsPerFunction();
      }
    } catch (ParseException e) {
      printCorrectCall(options);
    } catch (Exception e) {
      System.out.println("An unexpected error occurred: %s".formatted(e.getMessage()));
    }
  }

  // -------
  // Parsing
  // -------

  boolean parse() {
    if ((this.ast == null) && this.parseErrorOccurred) {
      return false;
    } else if (this.ast != null) {
      return true;
    }

    try {
      this.ast = generateAbstractSyntaxTree();
      if (this.verbose) {
        printAbstractSyntaxTree();
      }
      this.lineEvaluator = new LineEvaluator();
      this.ast.apply(this.lineEvaluator);
    } catch (IOException e) {
      String filePath = this.fileHandler.getFilePath();
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

  Start generateAbstractSyntaxTree() throws IOException, LexerException, ParserException {
    PushbackReader fileReader = this.fileHandler.getPushbackReader();
    Lexer lexer = new Lexer(fileReader);
    Parser parser = new Parser(lexer);
    return parser.parse();
  }

  private void printAbstractSyntaxTree() {
    if ((this.ast != null) && verbose) {
      ASTPrinter printer = new ASTPrinter();
      this.ast.apply(printer);
    }
  }

  // -------------
  // Type-Checking
  // -------------
  boolean buildSymbolTable() {
    if (!parse()) {
      return false;
    } else if (this.symbolTableBuilder == null) {
      this.symbolTableBuilder = new SymbolTableBuilder();
      this.ast.apply(symbolTableBuilder);
      this.symbolTable = symbolTableBuilder.getSymbolTable();
    }

    return !this.symbolTableBuilder.errorsOccurred();
  }

  boolean typeCheck() {
    if (!parse()) {
      return false;
    } else if (this.typeChecker == null) {
      buildSymbolTable();
      this.typeChecker = new TypeChecker(this.symbolTable, this.lineEvaluator);
      this.ast.apply(this.typeChecker);
    }

    return !(this.symbolTableBuilder.errorsOccurred() || this.typeChecker.errorsOccurred());
  }

  int getSymbolErrorNumber() {
    buildSymbolTable();
    return symbolTableBuilder.getErrorNumber();
  }

  int getTypeErrorNumber() {
    typeCheck();
    return this.typeChecker.getErrorNumber();
  }

  // -----------------
  // Liveness-Analysis
  // -----------------
  boolean liveness() {
    if (parse() && typeCheck()) {
      if (this.livenessAnalyzer == null) {
        this.livenessAnalyzer = new LivenessAnalyzer(this.ast, this.symbolTable, this.lineEvaluator);
        if (this.verbose) {
          ArrayList<String> functionNames = this.symbolTable.getScopeNames();
          for (String function : functionNames) {
            this.livenessAnalyzer.printDataflowGraph(function);
          }
        }
      }
      return true;
    } else {
      return false;
    }
  }

  HashMap<String, Integer> getMinimumRegistersPerFunction() {
    if (liveness()) {
      HashMap<String, Integer> minRegs = this.livenessAnalyzer.getMinimumRegistersPerFunction();

      System.out.println("Minimum required registers per function:");
      minRegs.forEach((function, registerCount) -> System.out
          .println("Minimum registers for %s: %d".formatted(function, registerCount)));

      return minRegs;
    } else {
      return new HashMap<>();
    }
  }

  HashMap<String, List<Symbol>> getUnusedArgsPerFunction() {
    if (liveness()) {
      HashMap<String, List<Symbol>> unusedArgs = this.livenessAnalyzer.getUnusedArgsPerFunction();

      System.out.println("\nUnused arguments per function:");
      unusedArgs.forEach((function, unusedArguments) -> System.out
          .println("Unused arguments for %s: %s".formatted(function, unusedArguments)));

      return unusedArgs;
    } else {
      return new HashMap<>();
    }
  }

  // ------------
  // Compilation
  // ------------
  void compile() {
    if (generateCode() && fileHandler.writeOutputFile(this.code)) {
      System.out.println("Successful!");
    }
  }

  boolean generateCode() {
    if (parse() && typeCheck()) {
      CodeGenerator codeGenerator = new CodeGenerator(
          fileHandler.getProgramName(), this.symbolTable, this.lineEvaluator);
      ast.apply(codeGenerator);
      this.code = codeGenerator.getCode();

      return true;
    }
    return false;
  }
}
