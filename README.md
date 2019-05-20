# MicroInterpreter
Minimalistic interpreter for untitled functional programming language



## How to run it
### Program
You can launch a small demo by running a `main` function
within `src/demo/Demo.kt`. Enter desired program line-by-line
(Ctrl+D to stop) and interpreter will calculate and print
its value immediately.

Demo will also print an error message in case if entered program
is not valid or produces runtime errors.

The example below can be used to calculate a 10th number of Fibonacci:
```
f(x)={[(x>1)]?{h((x-1),1,0)}:{x}}
h(n,y,z)={[n]?{h((n-1),(y+z),y)}:{y}}
f(10)
```


### Unit-tests
Project makes use of JUnit 5. All the tests are placed
within `test/` folder



## Idea behind
### Workflow
1) Scanner reads one line of code and translates it
   into a sequence of lexemes
2) Parser looks through the produced lexemes two times:
   * Firstly, it collects signatures for all functions
     and assigns contiguous numbers for them
   * Secondly, it recursively parse functions' bodies
     and expression (called "program body") at the last line
     and produces AST for each of them
3) Interpreter recursively calculate program body


### Calculation model
Parsed program is represented as a list of functions
and an expression to be calculated. Each function is uniquely
identified with its number. When expression refers to a function,
compiler replaces its name with its number for the sake of efficiency.

The same is true for parameters of functions: symbolic parameter
names are replaced by the compiler with their stack offset.
That makes it possible to calculate parameter's value at runtime
very fast


### Further work
Some optimizations can be performed on analysis of binary expressions.
If both arguments are `ConstantExpression`'s, binary one can be
evaluated at compile-time (the code will be similar to
`evaluateBinaryExpression()` from `Interpreter`).

Function calls can also be reduced if function's body is a constant
expression. But, as I can see, it requires the third pass of
compiler
