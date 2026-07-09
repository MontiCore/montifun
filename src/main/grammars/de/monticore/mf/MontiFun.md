<!-- (c) https://github.com/MontiCore/monticore -->

# MontiFun

This documentation is intended for **language engineers**
using or extending the languages.
A detailed documentation for **modelers** who use the languages is located
**[here](../../../../../../README.md)**.
We recommend that language engineers read the documentation for
modelers before reading this documentation.

## Grammars

The grammar [MontiFun.mc4](MontiFun.mc4)
defines the MontiFun language.
It consists of function and constant value definitions.
These in turn are based on side effect free expressions
(thus excluding, e.g., assigment expressions)
with first order functions.

For the expressions, the following
[Literals](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/Literals.md)
grammars are extended

* [MCCommonLiterals](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4)
* [SIUnitLiterals](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/siunit/SIUnitLiterals.mc4)
    * [SIUnits](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/siunit/SIUnits.md)

[Expressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/Expressions.md)
grammars have been extended.
This includes most side effect expressions provided by
[MontiCore](https://github.com/MontiCore/monticore).

* [BitExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/BitExpressions.mc4)
* [OCLExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/OCLExpressions.mc4)
  excluding OCL-specifics, like the `@pre`-qualification
* [OptionalOperators](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/OptionalOperators.mc4)
* [LambdaExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/LambdaExpressions.mc4)
* [SetExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/ocl/SetExpressions.mc4)
* [StreamExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/StreamExpressions.mc4)
* [TupleExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/TupleExpressions.mc4)
* [UglyExpressions](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/expressions/UglyExpressions.mc4)
  have been added for specific cases
  there a type-cast expression is required
  to specify the target type of an expression.

[Types](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/Types.md)
grammars are extended to
(not limited to)
specify the types of the function and constant declarations in MontiFun.

* [MCArrayTypes](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCArrayTypes.mc4)
* [MCFunctionTypes](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCFunctionTypes.mc4)
* [MCSimpleGenericTypes](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCSimpleGenericTypes.mc4)
* [MCStructuralTypes](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/MCStructuralTypes.mc4)
* [RegExTypes](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/regex/RegExType.mc4)
* [SIUnitTypes4Computing](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/siunit/SIUnitTypes4Computing.mc4)
* [SIUnitTypes4Math](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/siunit/SIUnitTypes4Math.mc4)

[TypeParameters](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/types/TypeParameters.mc4)
are extended to support generic function definitions,
e.g., `<T> T id(T t) = t;`.

[OOSymbols][OOSymbolsRef]
are extended to support imported object types
with their (side effect free) methods
and fields.

## Symbols

_Importable_ symbol kinds are

* [TypeSymbol][BasicSymbolsRef]
    * [OOTypeSymbol][OOSymbolsRef]
      -specific behavior is supported in MontiFun.
* [FunctionSymbol][BasicSymbolsRef]
    * [MethodSymbol][OOSymbolsRef]
      -specific behavior is supported in MontiFun.
* [VariableSymbol][BasicSymbolsRef]
    * [FieldSymbol][OOSymbolsRef]
      -specific behavior is supported in MontiFun.

_Exportable_ symbol kinds are

* [DiagramSymbol][BasicSymbolsRef]
  are introduced per MontiFun model;
  each `MontiFunArtifactScope` is associated with exactly one `DiagramSymbol`.
  This scope contains the functions and variables.
* [FunctionSymbol][BasicSymbolsRef]
  Each `MFFunctionDeclaration` is associated with exactly one `FunctionSymbol`.
* [VariableSymbol][BasicSymbolsRef]
  Each `MFConstantDeclaration` is associated with exactly one `VariableSymbol`.
  As these are constants, they are `readOnly`.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [List of languages](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [MontiCore Core Grammar Library](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
* [Type System](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/java/de/monticore/types3/TypeSystem3.md)

[BasicSymbolsRef]:https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/symbols/BasicSymbols.mc4

[OOSymbolsRef]:https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/symbols/OOSymbols.mc4
