# Description of _easy_

The goal is to write a compiler for a small language _easy_ with a few advanced features like functions and two different types of numbers (i.e., integers and floats).
The compiler should convert the language's source code into Jasmin assembly, which can be translated to JVM byte code.

Some features may not be available yet.
Please check the status in the project's [roadmap](#roadmap).

## General concepts

Each line ends with a semicolon `;`.
Programs do not have a specific header.
Declarations are only allowed at the beginning of any function.

A correct program requires a main function of the following form once:
```
void main() {
  ...
}
```
This function handles the main flow of the program.

We describe an _Easy_ program in a file with `.easy`-suffix.
The filename can contain letters, digits and underscores, but needs to begin with a letter.


## Comments

Single line comments start with two slashes `//` and end with a line ending.
Multi line comments start with `/*` and end with `*/`.
Both comments can start after a command within the same line, but only the multi line comment allows a command after it's end.


## Functions

Functions begin with their return type followed by a unique identifier and a pair of parenthesis.
It contains a comma separated list of arguments, each looks like a declaration.
The body is surrounded by braces (i.e., `{...}`).
A `return` statement is necessary, if the return type is different from `void`.
Correct return types are variable types and `void`.
`void` functions can be exited with an empty `return;`, but it is not necessary.

```
int calcSum(int a, int b) {
  return a + b;
}
```

### `print`

`print` outputs its argument on the command line.
Versions for all data types are available.
`print(42);` prints _42_.
If `print` is called with an array, the array is printed as a comma-separated list if its values inside brackets.
E.g. `int[3] arr; print(a);` prints _[0, 0, 0]_.

### `println`

`println` behaves like `print`, but adds a new line after the output.


## Control flow

`if`, `else if` and `else` are available.
`if` requires a pair of parenthesis with a boolean expression to evaluate.
Braces need to be used around blocks, but can be left out if the block contains just one line.

```
if(a == 42) {
  ...
} else {
  ...
}
```


## Loops

`while` loops are available.
`while` requires a pair of parenthesis with a boolean expression, which is evaluated before each iteration.

```
int a = 10;
while(a > 0) {
  print(a);
  a = a - 1;
}
```


## Variables

Variables can have one of the following types.

type | description
-----|------------
boolean | Truth values `true` or `false`
int | Integer numbers
float | Floating point numbers
string | Strings in quotes (i.e., `"..."`)

### Declaration

Variables need to be declared before usage by giving the type and a unique identifier.

```
int a;
```

### Initialization and assignment

The `=` operator assigns a new value to an identifier.
Initialization is possible in the same step as the declaration, but the initialization of an undeclared variable is prohibited.

```
int a = 0;
boolean b;
b = true;
```

### Typecast

_Easy_ is strictly type save and performs typecasts whenever necessary.


## Arrays

Arrays need to be declared, but not initialized.
Indices start at `0`.

### Declaration

A type, a unique id and length are required.

```
int[3] a;
```

Declarations fill the arrays with the following values:

type | initial value
-----|--------------
`int` | `0`
`float` | `0.0`
`boolean` | `false`

### Initialization

If one wants to declare and initialize an array with values different from the standard values, it is possible to use the syntax from initialization and provide a comma-separated list of values in brackets (i.e., `[...]`).
The length information may be left out or needs to be equal to the length of values in the given list.
Initialized variables of a compatible type may be used in the list.

```
float best = 1.0;
float[] possibleMarks = [best, 1.3, 1.7, 2.0, 2.3, 2.7, 3.0, 3.3, 3.7, 4.0, 5.0];
```

### Accessing elements

Elements can be accessed via an index inside the bracket.

```
int[7] arr;
print(arr[3]);
```


## Operators

### Arithmetic

The types inside the expressions determine the result type.
Usually, the result is `int`, if no `float` is involved, and `float` otherwise.
Please note that modulo does only work with `int` inputs and outputs.

operator | description
---------|------------
`+`      | Addition of two numbers.
`-`      | Subtraction of two numbers.
`*`      | Multiplication of two numbers.
`/`      | Division of two numbers.
`%`      | Modulo of two numbers.

### Logical

The result is always `boolean`.

operator | description
---------|------------
`!`      | Logical _negation_ of a boolean expression.
`&&`     | Logical _and_ of two boolean expressions.
`\|\|`   | Logical _or_ of two boolean expressions.

### Comparison

The result is always `boolean`.

operator | description
---------|------------
`==`     | Equality of the results of two expressions from the same type.
`!=`     | Inequality of the results of two expressions from the same type.
`>`      | Check, if the left number is greater than the right number.
`>=`     | Check, if the left number is greater than or equal to the right number.
`<`      | Check, if the left number is smaller than the right number.
`<=`     | Check, if the left number is smaller than or equal to the right number.

### String

operator  | description
----------|------------
`§`       | Concatenation of two expressions into a string (e.g., `"Route " § 66` results in the string _Route 66_).

### Operator Precedence

_Easy_ evaluates expressions from left to right and uses the following operator precedence.
Pairs of parenthesis (i.e., `(...)`) highlight or change the precedences inside expressions.

precedence | operators   | operator type
-----------|-------------|---------------
high       | `!,+,-`     | Unary
...        | `*,/,%`     | Multiplicative
...        | `+,-,§`     | Additive
...        | `<,<=,>=,>` | Comparison
...        | `==,!=`     | Equality
...        | `&&`        | Conjunction
low        | `\|\|`      | Disjunction

## Roadmap

- [x] Parsing (basic functionality)
- [x] Type-checking (basic functionality)
- [x] Code-generation (basic functionality)
- [x] Implementation of floats
- [x] Implementation of strings
- [ ] Implementation of functions
- [ ] Implementation of arrays
- [ ] Implementation of syntactic sugar

### Implementation of functions

- [ ] extend grammar to parse function heads and bodies
- [ ] extend type-checking, add a symbol table for each function
- [ ] extend code-generation

### Implementation of arrays

- [ ] extend grammar
- [ ] extend type-checking
- [ ] extend code-generation

### Syntactic sugar and further features

Syntactic sugar may be added as additional features.
A tick indicates it has been added:

- [ ] Short forms for assignment and arithmetic operators (i.e., `id += expr;` instead of `id = id + expr;`)
- [ ] Increment and decrement (i.e., `id++;` instead if `id += 1;` and `id--` instead `id -= 1;`)
- [ ] `for` loops of the form `for(int id=expr; expr; expr) {...}`
- [ ] Additional functions like `min`, `max`, `length`