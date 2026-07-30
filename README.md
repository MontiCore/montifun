<!-- (c) https://github.com/MontiCore/monticore -->

# MontiFun

A functional programming language for the JVM.
It allows for defining constants as well as functions.

This documentation is intended for **modelers** who use the languages.
A detailed documentation for **language engineers**
using or extending the languages is located
**[here](src/main/grammars/de/monticore/mf/MontiFun.md)**.
We recommend that language engineers read this documentation
before reading the detailed documentation.

## An Example Model

```montifun
montifun Math {

  // constant definition
  double PI = 3.141592653589793;

  // function definition
  int fibonacci(int n) =
    if n == 0 then 0 else (
      if n == 1 then 1 else
        fib(n - 1) + fib(n - 2)
    );

}
```

To define constants and functions,
most (side effect free) [expressions][expressions] are supported.   
Similarly, most [types][types] provided by MontiCore are available.

## Command Line Tool

The command line tool of MontiFun provides functionality such as

* parsing
* coco-checking
* pretty-printing
* loading and storing symbol tables
* a REPL (read-eval-print-loop) for interactive use (alpha)
* code generation (alpha)

<!--
### Downloading the Latest Version of the Tool as a JAR

The latest version of the tool can be downloaded
in the form of an executable JAR file at
[**this download link**][tool-jar].

TODO: publish the tool and add the link
-->
### Building the Tool from Source

You can build the tool yourself.
This requires

* Java 21
* Gradle 8.14

First, clone the repository:

```bash
git clone https://git.rwth-aachen.de/monticore/languages/montifun.git
```

Change the directory to the root directory of the cloned sources:

```bash
cd montifun
```

Then build the project using Gradle:

```bash
gradle build
```

You can now find the executable JAR file located at
`target/libs/MCMontiFun.jar`.

### Command Line Options

The command line tool can be executed with

```bash
java -jar MCMontiFun.jar <options>
```

| Option                  | Argument                              | Description                                                                                                                                    |
|-------------------------|---------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| `-h`, `--help`          |                                       | Prints the list of command line options.                                                                                                       |
| `-i`, `--input`         | `<files>` (space separated)           | Processes the input artifacts. Without further options, they are only parsed.                                                                  |
| `-p`, `--path`          | `<directory>`                         | Sets the artifact path for imported symbols to be searched recursively for files ending in ".*sym". Defaults to the current working directory. |
| `--pp`, `--prettyprint` | `<files>` (space separated, optional) | Prints the input artifacts to stdout or into the provided files.                                                                               |
| `-c`,`--coco`           |                                       | Checks the context conditions for the input artifacts.                                                                                         |
| `-s`, `--symboltable`   | `<files>` (space separated, optional) | Prints the symbol tables of the input artifacts in the specified files.                                                                        |
| `-gen`, `--generate`    | `<directory>` (optional)              | Generates Java classes from the input artifacts into the specified directory (alpha).                                                          |
| `--interpreter`         |                                       | Starts the interactive interpreter (REPL) (alpha). S. below.                                                                                   |

### REPL – Interactive Interpreter (alpha)

MontiFun provides a REPL (**r**ead-**e**val-**p**rint-**l**oop).   
It can be run using

```bash
java -jar MCMontiFun.jar --interpreter
```

It provides several commands starting with `:`

| Command    | Argument     | Description                                                           |
|------------|--------------|-----------------------------------------------------------------------|
| `:help`    |              | Prints all commands                                                   |
| `:exit`    |              | Exits the REPL                                                        |
| `:import`  | `<filename>` | imports a montifun file                                               |
| `:vars`    |              | lists all variables in the outermost frame                            |
| `:funcs`   |              | lists all functions                                                   |
| `:imports` |              | lists all imports *of Java-classes*                                   |
| `:timing`  |              | toggles timing information. This **cannot** be used for benchmarking! |

Input lines start with `>`.
Output is printed below.

One can write expressions to have them evaluated:

```
> 2 + 3
5
```

Constants can be defined:

```
> int age = 44;
> age + 1
45
```

Functions can be defined:

```
> int f(int n) = n > 0 ? f(n - 1) + n : 0;
> f(5)
15
```

MontiFun files can be imported in some (few!) cases
(imports within the models are not supported).  
These imports start with `:`.
Here we import a file defining the function `even`.

```
> :import circularDependecy.mfun
> even(4)
true
```

Java classes can be imported.
Note that these imports are without `:`.

```
> import java.util.List;
> List<int> l = [1,2];
```

An input can span multiple lines by ending the lines with `\`

```
> int f(int n) = n > 0 ? \
                 f(n - 1) + n : \
                 0;
```

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [List of languages](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [MontiCore Core Grammar Library](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
* [Type System](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/java/de/monticore/types3/TypeSystem3.md)

[expressions]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/Expressions.md

[types]: https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/Types.md
