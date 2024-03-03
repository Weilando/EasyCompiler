package livenessanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
import symboltable.Symbol;
import symboltable.SymbolTable;
import symboltable.Type;

public class LivenessAnalyzerTest {
  @Test
  public void getAllUsedSymbols() {
    DataflowNode dataflowGraphStart = new DataflowNode(0, 1, "AMain");
    DataflowNode nodeDeclareUnusedSymbol = new DataflowNode(1, 2, "ADeclStat");
    DataflowNode nodeInitUsedSymbol = new DataflowNode(2, 3, "AInitStat");
    DataflowNode nodePrintUsedSymbol = new DataflowNode(3, 4, "APrintStat");

    dataflowGraphStart.addEdgeTo(nodeDeclareUnusedSymbol);
    nodeDeclareUnusedSymbol.addEdgeTo(nodeInitUsedSymbol);
    nodeInitUsedSymbol.addEdgeTo(nodePrintUsedSymbol);

    SymbolTable symbolTable = new SymbolTable();
    symbolTable.addNewScope("main", Type.NONE);
    symbolTable.addSymbolToScope("main", "unusedSymbol", Type.STRING);
    symbolTable.addSymbolToScope("main", "usedSymbol", Type.BOOLEAN);

    Symbol usedSymbol = symbolTable.getSymbol("main", "usedSymbol");
    nodeInitUsedSymbol.addDef(usedSymbol);
    nodeInitUsedSymbol.addOut(new HashSet<>(Arrays.asList(usedSymbol)));
    nodePrintUsedSymbol.addIn(new HashSet<>(Arrays.asList(usedSymbol)));
    nodePrintUsedSymbol.addUse(usedSymbol);

    List<Symbol> usedSymbols = LivenessAnalyzer.getAllUsedSymbols(dataflowGraphStart);

    assertEquals(1, usedSymbols.size());
    assertEquals(Type.BOOLEAN, usedSymbols.get(0).getType());
    assertEquals(1, usedSymbols.get(0).getVariableNumber());
  }

  @Test
  public void getUnusedVariableValues() {
    DataflowNode dataflowGraphStart = new DataflowNode(0, 1, "AMain");
    DataflowNode nodeInitStat = new DataflowNode(1, 2, "AInitStat");
    DataflowNode nodeAssignStatUsed = new DataflowNode(2, 3, "AAssignStat");
    DataflowNode nodePrintStat = new DataflowNode(3, 4, "APrintStat");
    DataflowNode nodeAssignStatUnused = new DataflowNode(4, 5, "AAssignStat");

    dataflowGraphStart.addEdgeTo(nodeInitStat);
    nodeInitStat.addEdgeTo(nodeAssignStatUsed);
    nodeAssignStatUsed.addEdgeTo(nodePrintStat);
    nodePrintStat.addEdgeTo(nodeAssignStatUnused);

    SymbolTable symbolTable = new SymbolTable();
    symbolTable.addNewScope("main", Type.NONE);
    symbolTable.addSymbolToScope("main", "symbol", Type.BOOLEAN);

    Symbol symbol = symbolTable.getSymbol("main", "symbol");
    nodeInitStat.addDef(symbol);
    nodeInitStat.addDef(symbol);
    nodeAssignStatUsed.addOut(new HashSet<>(Arrays.asList(symbol)));
    nodePrintStat.addUse(symbol);
    nodeAssignStatUsed.addIn(new HashSet<>(Arrays.asList(symbol)));
    nodeAssignStatUnused.addDef(symbol);

    List<UnusedValue> unusedValues = LivenessAnalyzer
        .getUnusedVariableValues(dataflowGraphStart);

    assertEquals(2, unusedValues.size());

    UnusedValue unusedValue = unusedValues.get(0);
    assertEquals(2, unusedValue.getLineNumber());
    assertEquals("AInitStat", unusedValue.getStatementType());
    assertEquals(Type.BOOLEAN, unusedValue.getSymbol().getType());
    assertEquals(0, unusedValue.getSymbol().getVariableNumber());

    unusedValue = unusedValues.get(1);
    assertEquals(5, unusedValue.getLineNumber());
    assertEquals("AAssignStat", unusedValue.getStatementType());
    assertEquals(Type.BOOLEAN, unusedValue.getSymbol().getType());
    assertEquals(0, unusedValue.getSymbol().getVariableNumber());
  }

  @Test
  public void findDefinedButUnusedSymbols() {
    Symbol s0 = new Symbol(Type.STRING, 0);
    Symbol s1 = new Symbol(Type.STRING, 1);
    Symbol s2 = new Symbol(Type.STRING, 2);
    HashSet<Symbol> definedArguments = new HashSet<>();
    definedArguments.add(s2);
    definedArguments.add(s1);
    definedArguments.add(s0);

    HashSet<Symbol> usedArguments = new HashSet<>();
    usedArguments.add(s1);

    List<Symbol> result = LivenessAnalyzer.findDefinedButUnusedSymbols(
        definedArguments, usedArguments);
    assertEquals(result.size(), 2);
    assertEquals(result.get(0), s0);
    assertEquals(result.get(1), s2);
  }
}
