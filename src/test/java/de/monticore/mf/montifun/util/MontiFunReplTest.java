// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifunrepl.MontiFunReplMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameForBasicSymbols;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.values.MCValue;
import de.monticore.values.MCValueFunction;
import de.monticore.values.MCValueInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// todo cleanup
class MontiFunReplTest extends AbstractTest {

  protected MontiFunRepl repl;

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MontiFunReplMill.reset();
    repl = new MontiFunRepl();
  }

  @Test
  void replTest1() {
    runRepl(
        "int x = 2;",
        "int foo() = x + 1;",
        "int bar(int z) = z + foo();",
        "int y = bar(1);"
    );

    assertEquals(2, readVariable("x").asInt());
    assertEquals(3, callFunction("foo").asInt());
    assertEquals(4, readVariable("y").asInt());
  }

  //should be enabled again after implementing the interpreter for if-then-else
  @Disabled
  @Test
  void replImportFibonacciModelTest() {
    Path modelPath = Paths.get(
        RELATIVE_VALID_MODEL_PATH,
        "fibonacci.mfun"
    ).toAbsolutePath();
    List<String> output = runRepl(":import " + modelPath);

    assertTrue(output.contains("Imported successfully."), String.join("\n", output));
    Optional<FunctionSymbol> fibSym =
        BasicSymbolsMill.globalScope().resolveFunction("fib");
    assertTrue(fibSym.isPresent());
    assertEquals(
        8,
        callFunction(
            "fib",
            new MCValueInt(6)
        ).asInt()
    );
  }

  protected MCValue readVariable(String varName) {
    MIFrameForBasicSymbols scope = repl.getInterpreter().getTopMostFrame();
    MIFrameLayoutForBasicSymbols frameLayout = scope.getFrameLayout();
    Optional<VariableSymbol> varSymOpt =
        BasicSymbolsMill.globalScope().resolveVariable(varName);
    assertTrue(varSymOpt.isPresent());
    VariableSymbol varSym = varSymOpt.get();
    MICalculation getter = frameLayout.getVariableGetter(varSym);
    MCValue value = getter.asCalculationValue().calculate(scope);
    return value;
  }

  protected MCValue callFunction(String funcName, MCValue... args) {
    return callFunction(funcName, java.util.Arrays.asList(args));
  }

  protected MCValue callFunction(String funcName, List<MCValue> args) {
    Map<FunctionSymbol, MCValueFunction> functions = repl.interpreterData.getFunctions();
    for (FunctionSymbol functionSymbol : functions.keySet()) {
      if (functionSymbol.getName().equals(funcName)) {
        return functions.get(functionSymbol)
            .execute(args.toArray(new MCValue[0]));
      }
    }
    fail("No function found in interpreter scope: " + funcName);
    return null;
  }

  protected List<String> runRepl(String... commands) {
    return runRepl(List.of(commands));
  }

  /**
   * runs multiple commands in the REPL
   * and returns the output.
   *
   * @param commands commands to be executed
   * @return output of the REPL
   */
  protected List<String> runRepl(List<String> commands) {
    return captureOutputLines(() -> {
      for (String command : commands) {
        repl.evaluateInput(command);
      }
    });
  }

  /**
   * Blocks and returns the output of the REPL.
   *
   * @param action the action calling the REPL.
   * @return The output to {@link System#out} by the action
   */
  protected List<String> captureOutputLines(Runnable action) {
    PrintStream oldOut = System.out;
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (PrintStream newOut = new PrintStream(
        output, true, StandardCharsets.UTF_8
    )) {
      System.setOut(newOut);
      action.run();
    }
    finally {
      System.setOut(oldOut);
    }
    return output.toString(StandardCharsets.UTF_8)
        .lines()
        .filter(line -> !line.isBlank())
        .toList();
  }

}

