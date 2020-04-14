# EasyCompiler
This project is a compiler for _Easy_, a simple programming language.
Its description can be found [here](/docs/EasyLanguage.md).
The compiler's output is Jasmin assembler, which can be assembled into Java Bytecode, i.e. the programs can be executed on the JVM.

One can track the project's progress in the [roadmap](/docs/Roadmap.md).

## Features
- Compilation of _Easy_ sourcecode into Jasmin assembler, which can be translated into Java Bytecode (the bytecode representation of an _Easy_ program equals a Java class and behaves like it)
- Translation of each statement in the sourcecode into one clear block of Jasmin assembler, beginning with a comment containing its identifier and original line number
- Calculation and limitation of maximum depth of stack and count of local variables during compilation
- Type-checking right after parsing
- Helpful error messages, if applicable with exact line and position in the sourcecode (for parsing- and type-checking-errors)
- Liveness-analysis

## Build and Run
Gradle is used for dependency-management, building and testing the compiler.
The compiler will be called via `java EasyCompiler -compile <Filename.easy>`.

## Installation
Simply run `gradle build` to install most dependencies.

Jasmin is not available on MavenCentral yet, but I will work on a solution using Gradle soon.
Please download Jasmin 2.4 from [SourceForge](https://sourceforge.net/projects/jasmin/files/jasmin/jasmin-2.4/) and put the jar into `/libs`.