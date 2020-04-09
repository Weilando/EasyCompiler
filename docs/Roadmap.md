# Roadmap
- [x] Setup
- [x] Stage 1: Parsing (basic functionality)
- [x] Stage 2: Type-checking (basic functionality)
- [ ] Stage 3: Code-generation (basic functionality)
- [ ] Stage 4: Implementation of floats
- [ ] Stage 5: Implementation of strings
- [ ] Stage 6: Implementation of functions
- [ ] Stage 7: Implementation of arrays

## Setup
- [x] Create git-repository and configure `.gitignore`
- [x] Create language description of _Easy_
- [x] Create gradle-project
- [x] Create main class and implement arguments
- [x] Setup testing

## Stage 1: Parsing (basic functionality)
- [x] Open source file
- [x] Setup SableCC
### Progress parsing
- [x] main method
- [x] comment
- [x] literals
- [x] operators
  - [x] unary
  - [x] arithmetic
  - [x] comparison
  - [x] logical
- [x] expressions
  - [x] unary
  - [x] arithmetic
  - [x] comparison
  - [x] logical
- [x] operator precedence
- [x] assignment
- [x] blocks
- [x] control structure (without Dangling-Else!)
  - [x] if-else
  - [x] while
- [x] print
- [x] generate abstract syntax tree

## Stage 2: Type-checking (basic functionality)
- [x] Add type-checker
- [x] Add symbol table
- [x] Add line-evaluator
- [x] Add error-handler for type-errors
### Progress type-checking
- [x] Detect missing declaration before first assignment
- [x] Detect repeated declarations
- [x] Evaluate type of expressions
  - [x] unary
  - [x] arithmetic
  - [x] comparison
  - [x] logical
- [x] Detect assignments of incompatible types
- [x] Check conditions of control structures (while and if need boolean expressions)
- [x] Check content of print/println statements

## Stage 3: Code-generation (basic functionality)
- [ ] tests
  - [ ] setup 
  - [ ] test of functionality of compiled correct snippets
  - [ ] test of functionality of compiled correct algorithms
- [ ] implementation code-generation
  - [ ] main method
  - [ ] declarations, initializations and assignments
  - [ ] expressions
    - [ ] unary
    - [ ] arithmetic
    - [ ] comparison
    - [ ] logical
  - [ ] statements
    - [ ] if and else
    - [ ] while
    - [ ] print
    
## Stage 4: Implementation of floats
- [ ] extend grammar to parse `float` keyword and literals
- [ ] extend type-checking
- [ ] extend code-generation

## Stage 5: Implementation of strings
- [ ] extend grammar to parse `string` keyword and literals
- [ ] extend type-checking
- [ ] extend code-generation

## Stage 6: Implementation of functions
- [ ] extend grammar to parse function heads and bodies
- [ ] extend type-checking, add a symbol table for each function
- [ ] extend code-generation

## Stage 7: Implementation of arrays
- [ ] extend grammar
- [ ] extend type-checking
- [ ] extend code-generation
