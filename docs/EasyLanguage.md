# Description of _easy_
The goal is to write a compiler for a small language _easy_ with a few advanced features like functions and two different types of numbers (i.e. integers and floats).
The compiler should convert the language's source code into Jasmin assembly, which can be translated to JVM byte code.

Some features may not be available yet.
Please check the status in the project's [roadmap](Roadmap.md).

## General concepts
Each line ends with a semicolon `;`.
Programs do not have a specific header.

A correct program requires a main function of the following form once:
```
void main() {
  ...
}
```
This function handles the main flow of the program.

Programs are saved in a file with `.easy`-suffix.
The filename can contain letters, digits and underscores, but needs to begin with a letter.

## Functions
Functions begin with their return type followed by a unique identifier and a pair of parenthesis.
It contains a comma separated list of arguments, each looks like a declaration.
The body is surrounded by braces (i.e. `{...}`).
A `return` statement is necessary, if the return type is different from `void`.
Correct return types are variable types and `void`.
`void` functions can be exited with an empty `return;`, but it is not necessary.

```
int calcSum(int a, int b) {
  return a + b;
}
```

### Built-in functions
#### `print`
`print` puts its argument on the command line.
Versions for all data types are available.
`print(42);` prints _42_.
If `print` is called with an array, the array is printed as a comma-separated list if its values inside brackets.
E.g. `int[3] arr; print(a);` prints _[0, 0, 0]_.

#### `println`
`print` puts its argument on the command line and adds a new line.
It behaves like `print`.


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
Variables may be of one of the following types:

type | description
-----|------------
boolean | Truth values `true` or `false`
int | Integer numbers
float | Floating point numbers
string | Strings in quotes (i.e. `"..."`)

### Declaration
Variables need to be declared before usage by giving the type and a unique identifier.

```
int a;
```

### Initialization and assignment
Variables can be assigned by using the `=` operator with the identifier and the new value.
Initialization and declaration can be performed in one step, but no Initialization is allowed before declaration.

```
int a = 0;
boolean b;
b = true;
```

### Typecast
As the language is strictly type save but two different types of numbers exist, it may be necessary to perform typecasts in some cases.
They are done automatically.


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
If one wants to declare and initialize an array with values different from the standard values, it is possible to use the syntax from initialization and provide a comma-separated list of values in brackets (i.e. `[...]`).
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
The result's type depends on the types inside the expressions.

operator | description
----------|------------
`+` | Addition of two numbers or expressions. Result is `int`, if no `float` is involved, and `float` otherwise.
`-` | Subtraction of two numbers or expressions. Result is `int`, if no `float` is involved, and `float` otherwise.
`*` | Multiplication of two numbers or expressions. Result is `int`, if no `float` is involved, and `float` otherwise.
`/` | Division of two numbers or expressions. Result is `int`, if no `float` is involved, and `float` otherwise.
`\%` | Modulo of two numbers or expressions. Does only work with `int` and result is `int`.

### Logical
The result is always `boolean`.

operator | description
----------|------------
`!` | Logical _negation_ of a boolean expression.
`&&` | Logical _and_ of two boolean expressions.
`\|\|` | Logical _or_ of two boolean expressions.

### Comparison
The result is always `boolean`.

operator | description
----------|------------
`==` | Equality of the results of two expressions from the same class (i.e. number or boolean).
`!=` | Inequality of the results of two expressions from the same class (i.e. number or boolean).
`>` | Check, if the left number is greater than the right number.
`>=` | Check, if the left number is greater than or equal to the right number.
`<` | Check, if the left number is smaller than the right number.
`<=` | Check, if the left number is smaller than or equal to the right number.

### String
operator | description
----------|------------
`§` | Concatenation of two strings (e.g. `"One " § "string"` results in _One string_). Concatenations of strings with other datatypes result in strings, e.g. `"Count=" § 4` results in _Count=4_.

### Parenthesis
Pairs of parenthesis (i.e. `(...)`) can be used to highlight the precedences inside expressions.


## Syntactic sugar and further features
Syntactic sugar may be added as additional features.
A tick indicates it has been added:

- [ ] Short forms for assignment and arithmetic operators (i.e. `id += expr;` instead of `id = id + expr;`)
- [ ] Increment and decrement (i.e. `id++;` instead if `id += 1;` and `id--` instead `id -= 1;`)
- [ ] `for` loops of the form `for(int id=expr; expr; expr) {...}`
- [ ] Additional functions like `min`, `max`, `length`