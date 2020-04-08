# Roadmap
- [x] Setup
- [ ] Stage 1: Parsing (basic functionality)
- [ ] Stage 2: Type-checking (basic functionality)
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
- [ ] open source file
- [ ] write SableCC-grammar and generate parser
### Progress parsing
- [ ] main method
- [ ] comment
- [ ] literals
- [ ] operators
  - [ ] unary
  - [ ] arithmetic
  - [ ] comparison
  - [ ] logical
- [ ] expressions
  - [ ] unary
  - [ ] arithmetic
  - [ ] comparison
  - [ ] logical
- [ ] operator precedence
- [ ] assignment
- [ ] blocks
- [ ] control structure (without Dangling-Else!)
  - [ ] if-else
  - [ ] while
- [ ] print
- [ ] generate abstract syntax tree

## Stage 2: Type-checking (basic functionality)
- [ ] Add type-checker
- [ ] Add symbol table
- [ ] Add line-evaluator
- [ ] Add error-handler for type-errors
### Progress type-checking
- [ ] Detect missing declaration before first assignment
- [ ] Detect repeated declarations
- [ ] Evaluate type of expressions
  - [ ] unary
  - [ ] arithmetic
  - [ ] comparison
  - [ ] logical
- [ ] Detect assignments of incompatible types
- [ ] Check conditions of control structures (while and if need boolean expressions)
- [ ] Check content of print statements

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
