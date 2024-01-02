# Changelog

This changelog lists [semantic versions](https://semver.org/spec/v2.0.0.html) and related changes.

## 0.5.0: 2023-12-31 (#6)

- :sparkles: add string literals and variables
- :sparkles: add `==` and `!=` support for strings
- :sparkles: implement string concatenations (including type casts for other types) using `§`
- :white_check_mark: extend tests
- :recycle: improve readability and apply minor improvements
- :memo: improve documentation of types

## 0.4.1: 2023-12-29 (#5)

- :sparkles: add dev-container for VSCode users, add checkstyle
- :recycle: improve gradle setup, generate executable jar for _Easy_ compiler
- :fire: remove gradlew
- :white_check_mark: test and document comments
- :memo: improve documentation

## 0.4.0: 2020-04-22 (#3)

- :sparkles: add `float` variables and literals
- :white_check_mark: add tests
- :building_construction: save type information for expressions inside AST-nodes and remove ExpressionTypeCache
- :recycle: apply minor improvements for better testability
- :memo: improve documentation

## 0.3.0: 2020-04-13 (#2)

- :heavy_plus_sign: setup the Jasmin assembler
- :sparkles: implement generation of Jasmin assembly code
    - main method
    - declarations, initializations and assignments
    - expressions: unary, arithmetic, comparison, logical
    - statements: `if`-`else`, `while`, `print`, `println`
- :sparkles: implement liveness analysis for 
- :white_check_mark: add tests for functionality of compiled correct programs

## 0.2.0: 2020-04-09 (#1)

- :sparkles: add type-checker
    - detect missing declaration before first assignment
    - detect repeated declarations
    - evaluate type of expressions
    - detect assignments of incompatible types
    - check conditions of control structures (while and if need boolean expressions)
    - check content of print/println statements
- :sparkles: add symbol table
- :sparkles: add line-evaluator
- :sparkles: add error-handler for type-errors
- :white_check_mark: add tests for type checking

## 0.1.0: 2020-04-08

- :tada: setup git-repository and create gradle-project
- :sparkles: create `EasyCompiler` class and CLI
- :sparkles: implement reader for _Easy_ source files
- :white_check_mark: setup testing
- :memo: create language description of _Easy_
- :heavy_plus_sign: setup SableCC
- :sparkles: add SableCC grammar, generate abstract syntax tree
    - define main method, code blocks and comments
    - define literals, operators and expressions including precedence: unary, arithmetic, comparison and logical
    - define declarations and assignments
    - define control structures (without Dangling-Else!): `if`-`else`, `while`
    - define `print` and `println`

## Unversioned changes

- :bug: improve stack depth evaluation for string concatenations
- :memo: add changelog instead of roadmap
