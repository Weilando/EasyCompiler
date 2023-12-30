# _Easy_ Compiler

This project is a compiler for _Easy_, a simple programming language.
The compiler's output is Jasmin assembler, which can be assembled into Java Bytecode, i.e. the programs can be executed on the JVM.

- [Project roadmap](/docs/Roadmap.md) which shows the project's progress
- [Language documentation](/docs/EasyLanguage.md) which describes _Easy_ in greater detail.

## Features

- Compilation of _Easy_ sourcecode into Jasmin assembler, which can be translated into Java Bytecode (the bytecode representation of an _Easy_ program equals a Java class and behaves like it)
- Translation of each statement in the sourcecode into one clear block of Jasmin assembler, beginning with a comment containing its identifier and original line number
- Calculation and limitation of maximum depth of stack and count of local variables during compilation
- Type-checking right after parsing
- Helpful error messages, if applicable with exact line and position in the sourcecode (for parsing- and type-checking-errors)
- Liveness-analysis

## Project Setup

In this section, we describe how to setup this project locally.

### VSCode using Dev-Container

If you use VSCode, you should use our dev-container.
It sets up a working environment with all dependencies for you.

### Other IDEs without a Dev-Container

Install java 17 and gradle 1.7.
Simply run `gradle build` to install most dependencies.

Jasmin is not available on MavenCentral yet.
Please download Jasmin 2.4 from [SourceForge](https://sourceforge.net/projects/jasmin/files/jasmin/jasmin-2.4/) and put the jar into `/libs`.

## Build the _Easy_ Compiler

We use Gradle for dependency-management, building and testing the compiler.
Simply run `gradle build` to create a runnable jar at `/build/libs/EasyCompiler.jar`.

## Use the _Easy_ Compiler

Gradle creates the `EasyCompiler.jar` which compiles an `.easy` file into a `.j` file containing Jasmin assembler code.
In the following example, we describe how to compile an _Easy_ program into Java bytecode.

1. Create the file `hello_world.easy` at root and copy the following program.
    ```
    void main() {
      boolean hello = true;
      println(hello);
    }
    ```
    Once compiled, it will assign a boolean variable and print its value.
2. Run the _Easy_ compiler with `java -jar build/libs/EasyCompiler.jar -compile hello_world.easy`.
    It creates `hello_world.j` which contains a Jasmin assembler representation of our _Easy_ code.
3. Run `java -jar libs/jasmin.jar hello_world.j` to transform `hello_world.j` into `hello_world.class`, which contains Java bytecode.
4. Finally, run `java hello_world` to start the compiled program, which should output `true`.
