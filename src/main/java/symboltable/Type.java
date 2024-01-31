package symboltable;

/** Definition of all known types in the Easy language. */
public enum Type {
  BOOLEAN, ERROR, FLOAT, INT, NONE, STRING, UNDEFINED;

  /** Translates the Easy type into a JVM type. */
  public String getJvmType() {
    switch (this) {
      case BOOLEAN:
        return "Z";
      case FLOAT:
        return "F";
      case INT:
        return "I";
      case NONE:
        return "V";
      default:
        return "Ljava/lang/String;";
    }
  }
}
