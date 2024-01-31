package codegeneration;

import java.util.ArrayList;
import java.util.List;

/** Cache for generated code lines. */
public class CodeCache {
  private final ArrayList<String> code = new ArrayList<>();

  void addIndentedLine(String line) {
    code.add(String.format("\t%s", line));
  }

  void addLines(List<String> lines) {
    code.addAll(lines);
  }

  void addIndentedLines(List<String> lines) {
    lines.forEach(this::addIndentedLine);
  }

  public ArrayList<String> getCode() {
    return this.code;
  }
}
