package symboltable;

import analysis.DepthFirstAdapter;
import node.ABooleanRtp;
import node.ABooleanTp;
import node.ADeclArg;
import node.ADeclStat;
import node.AFloatRtp;
import node.AFloatTp;
import node.AFunc;
import node.AInitStat;
import node.AIntRtp;
import node.AIntTp;
import node.AMain;
import node.ANoneRtp;
import node.AStringRtp;
import node.AStringTp;
import node.PRtp;
import node.PTp;
import node.TIdentifier;

/**
 * Depth first walker for the AST that fills the symbol table with functions and
 * variables. It does not check any types, because it focuses on declarations
 * and function heads.
 */
public class SymbolTableBuilder extends DepthFirstAdapter {
  private final SymbolErrorHandler errorHandler;
  private final SymbolTable symbolTable;

  public SymbolTableBuilder() {
    errorHandler = new SymbolErrorHandler();
    symbolTable = new SymbolTable();
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }

  // Function definitions
  @Override
  public void inAMain(AMain node) {
    symbolTable.addNewScope("main", Type.NONE);
  }

  @Override
  public void inAFunc(AFunc node) {
    TIdentifier id = node.getId();
    String idText = id.getText();
    Type returnType = determineInternalReturnType(node.getReturnType());

    symbolTable.addNewScope(idText, returnType);
  }

  /**
   * Parses a function argument. As this class walks the AST using depth first
   * search, it visits the arguments in the correct order.
   */
  @Override
  public void inADeclArg(ADeclArg node) {
    String scopeName = symbolTable.determineScope(node);
    String argName = node.getId().getText();
    Type argType = determineInternalType(node.getType());

    symbolTable.addFunctionArgumentType(scopeName, argType);
    symbolTable.addSymbolToScope(scopeName, argName, argType);
  }

  // Variable statements
  @Override
  public void caseAInitStat(AInitStat node) {
    TIdentifier id = node.getId();
    String idText = id.getText();

    String scopeName = symbolTable.determineScope(node);
    if (symbolTable.containsSymbol(scopeName, idText)) {
      errorHandler.printAlreadyDefinedError(id);
    } else {
      Type varType = determineInternalType(node.getType());
      symbolTable.addSymbolToScope(scopeName, idText, varType);
    }
  }

  @Override
  public void caseADeclStat(ADeclStat node) {
    TIdentifier id = node.getId();
    String idText = id.getText();

    String scopeName = symbolTable.determineScope(node);
    if (symbolTable.containsSymbol(scopeName, idText)) {
      errorHandler.printAlreadyDefinedError(id);
    } else {
      Type varType = determineInternalType(node.getType());
      symbolTable.addSymbolToScope(scopeName, idText, varType);
    }
  }

  // Helpers
  Type determineInternalType(PTp treeType) {
    if (treeType instanceof ABooleanTp) {
      return Type.BOOLEAN;
    } else if (treeType instanceof AFloatTp) {
      return Type.FLOAT;
    } else if (treeType instanceof AIntTp) {
      return Type.INT;
    } else if (treeType instanceof AStringTp) {
      return Type.STRING;
    } else { // Should never happen
      System.out.println("Error during determination of type.");
      return Type.ERROR;
    }
  }

  Type determineInternalReturnType(PRtp treeReturnType) {
    if (treeReturnType instanceof ABooleanRtp) {
      return Type.BOOLEAN;
    } else if (treeReturnType instanceof AFloatRtp) {
      return Type.FLOAT;
    } else if (treeReturnType instanceof AIntRtp) {
      return Type.INT;
    } else if (treeReturnType instanceof AStringRtp) {
      return Type.STRING;
    } else if (treeReturnType instanceof ANoneRtp) {
      return Type.NONE;
    } else { // Should never happen
      System.out.println("Error during determination of type.");
      return Type.ERROR;
    }
  }

  public boolean errorsOccurred() {
    return this.errorHandler.errorsOccurred();
  }

  public int getErrorNumber() {
    return errorHandler.getErrorNumber();
  }
}
