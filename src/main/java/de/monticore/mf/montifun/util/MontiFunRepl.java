// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import de.monticore.expressions.bitexpressions.interpreter.BitExpressionsInterpreter;
import de.monticore.expressions.commonexpressions.interpreter.CommonExpressionsInterpreter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionsBasisInterpreter;
import de.monticore.expressions.interpreter.ExpressionsInterpreter;
import de.monticore.expressions.lambdaexpressions.interpreter.LambdaExpressionsInterpreter;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.frames.MIFrame;
import de.monticore.interpreter.setters.MISetter;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.interpreter.values.MCValueFunctionOfModel;
import de.monticore.literals.mccommonliterals.interpreter.MCCommonLiteralsInterpreter;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._ast.ASTMFParameter;
import de.monticore.mf.montifun._ast.ASTMontiFunNode;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun.types3.MontiFunTypeCheck3;
import de.monticore.mf.montifunrepl.MontiFunReplMill;
import de.monticore.mf.montifunrepl._ast.ASTMontiFunREPLInput;
import de.monticore.ocl.optionaloperators.interpreter.OptionalOperatorsInterpreter;
import de.monticore.ocl.setexpressions.interpreter.SetExpressionsInterpreter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.basicsymbols.interpreter.frames.MIFrameLayoutForBasicSymbols;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.values.MCValue;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class MontiFunRepl {

  protected IMontiFunArtifactScope artifactScope;
  protected ITraverser traverser;
  protected InterpreterDataForBasicSymbols interpreterData;
  protected ExpressionsInterpreter interpreter;
  protected Collection<MCValueFunctionOfModel> modelFunctions =
      new ArrayList<>();

  protected boolean timing = false;
  protected boolean exitCalled = false;

  public MontiFunRepl() {
    Log.enableFailQuick(false);
    MontiFunReplMill.init();
    MFSymbolTableUtil.prepareMillWithoutInit();
    MontiFunTypeCheck3.init();

    artifactScope = MontiFunMill.artifactScope();
    artifactScope.setName("");
    // non-modifiable import list by default...
    artifactScope.setImportsList(new ArrayList<>());
    artifactScope.addImports(new ImportStatement("java.lang", true));
    MontiFunMill.globalScope().addSubScope(artifactScope);

    interpreterData = new InterpreterDataForBasicSymbols();
    traverser = createInterpreter(interpreterData);
    interpreter = new ExpressionsInterpreter(traverser, interpreterData);
  }

  protected MontiFunTraverser createInterpreter(InterpreterDataForBasicSymbols iData) {
    MontiFunTraverser traverser = MontiFunMill.inheritanceTraverser();
    traverser.setExpressionsBasisHandler(
        new ExpressionsBasisInterpreter(iData)
    );
    traverser.setCommonExpressionsHandler(
        new CommonExpressionsInterpreter(iData)
    );
    traverser.setMCCommonLiteralsHandler(
        new MCCommonLiteralsInterpreter(iData)
    );
    traverser.setSetExpressionsHandler(
        new SetExpressionsInterpreter(iData)
    );
    traverser.setBitExpressionsHandler(
        new BitExpressionsInterpreter(iData)
    );
    traverser.setOptionalOperatorsHandler(
        new OptionalOperatorsInterpreter(iData)
    );
    traverser.setLambdaExpressionsHandler(
        new LambdaExpressionsInterpreter(iData)
    );
    return traverser;
  }

  public ExpressionsInterpreter getInterpreter() {
    return interpreter;
  }

  public void run() {
    System.out.println("=================================================");
    System.out.println("================= MontiFun-REPL =================");
    System.out.println("=================================================");
    System.out.println("This is a prototype!");
    System.out.println("Some Context-Conditions are missing,");
    System.out.println("thus, specific models can break the REPL.");
    System.out.println("No sandboxing for security has been added.");
    System.out.println();
    System.out.println("* Commands start with \":\".");
    System.out.println("* Type \":help\" for a list of commands.");
    System.out.println("* For multiline input, end the line with \"\\\".");
    System.out.println();

    try (ReplInput replIO = new ReplInput()) {
      do {
        String userInput = replIO.readUserInput();
        // handle the input
        try {
          Log.getFindings().clear();
          evaluateInput(userInput);
        }
        catch (Exception e) {
          if (Log.getErrorCount() == 0) {
            // Nothing logged yet, this is not expected
            Log.error("0xF1777 An internal tool error occurred.", e);
          }
        }
      }
      while (!exitCalled);
    }
  }

  public void evaluateInput(String input) {
    if (input.startsWith(":")) {
      String trimmedInput = input.substring(1).trim();
      String[] command = trimmedInput.split("\\s+");
      if (command.length == 0) {
        command = new String[] { "help" };
      }
      evaluateCommand(command);
    }
    else {
      Optional<ASTMontiFunREPLInput> astOpt = withTiming(() -> {
            try {
              return MontiFunReplMill.parser().parse_StringMontiFunREPLInput(input);
            }
            catch (IOException e) {
              // not expected to ever happen
              Log.error(
                  "0xFD793 IOException while parsing the input: "
                      + e.getMessage()
              );
              return Optional.empty();
            }
          },
          "Parsing"
      );
      ASTMontiFunREPLInput ast;
      if (astOpt.isEmpty()) {
        return;
      }
      else {
        ast = astOpt.get();
      }

      if (ast.isPresentExpression()) {
        evaluateExpression(ast.getExpression());
      }
      else if (ast.isPresentMCImportStatement()) {
        evaluateImport(ast.getMCImportStatement());
      }
      else if (ast.isPresentMFFunctionDeclaration()) {
        evaluateFuncDecl(ast.getMFFunctionDeclaration());
      }
      else {
        evaluateConstDecl(ast.getMFConstantDeclaration());
      }
    }
  }

  protected void evaluateImport(ASTMCImportStatement importStmt) {
    String qName = importStmt.getQName();
    try {
      // check if the import is a Java class
      Class.forName(qName);
      artifactScope.addImports(new ImportStatement(qName, false));
    }
    catch (ClassNotFoundException e) {
      System.out.println("Java class " + qName + " not found.");
      System.out.println("Imports of static fields/methods is not supported");
      System.out.println("Use \":import filename\" to load a model file");
    }
  }

  protected void evaluateExpression(ASTExpression expr) {
    boolean passedChecks = withTiming(() -> {
          MontiFunMill.scopesGenitorDelegator()
              .createFromAST(expr, artifactScope);
          return completeSymTabAndCheckCoCos(expr);
        },
        "SymbolTable + CoCos"
    );
    if (!passedChecks) {
      System.out.println("Cannot evaluate expression");
    }
    MCValue value = withTiming(
        () -> interpreter.interpret(expr),
        "Interpretation"
    );
    printEvaluationResult(value);
  }

  protected void evaluateFuncDecl(ASTMFFunctionDeclaration funcDecl) {
    FunctionSymbol funcSym = MontiFunMill.scopesGenitorDelegator()
        .createFromAST(funcDecl, artifactScope);
    if (!completeSymTabAndCheckCoCos(funcDecl)) {
      System.out.println("Cannot evaluate function declaration");
      artifactScope.remove(funcSym);
      artifactScope.removeSubScope(funcSym.getSpannedScope());
      return;
    }
    registerFunction(funcDecl);
    System.out.println(funcSym.getFullName() + " : "
        + funcSym.getFunctionType().printFullName()
        + " successfully added."
    );
  }

  protected void evaluateConstDecl(ASTMFConstantDeclaration constDecl) {
    // check that the variable is new
    if (BasicSymbolsMill.globalScope().resolveVariable(constDecl.getName()).isPresent()) {
      System.out.println(
          "A variable with the name " + constDecl.getName()
              + " already exists. Please choose a different name."
      );
      return;
    }

    VariableSymbol constSym = MontiFunMill.scopesGenitorDelegator()
        .createFromAST(constDecl, artifactScope);
    if (!completeSymTabAndCheckCoCos(constDecl)) {
      System.out.println("cannot evaluate constant declaration");
      artifactScope.remove(constSym);
      // some subscopes of the expression may have been left over...
      // this _should_ not break, as they ought to be non-exporting
      return;
    }
    MCValue value = interpreter.interpret(constDecl.getExpression());
    if (value.isError()) {
      System.out.println("cannot evaluate constant declaration");
      printEvaluationResult(value);
      artifactScope.remove(constSym);
      return;
    }
    interpreter.addVariable(constSym, value);
    updateFunctionParentFrames();
    System.out.println(
        constSym.getName()
            + " : " + constSym.getType().printFullName()
            + " = " + value.printValue()
    );
  }

  protected void evaluateCommand(String[] command) {
    switch (command[0]) {
      case "help":
        System.out.println(":help – prints this help.");
        System.out.println(":exit – exits the REPL.");
        System.out.println(":import filename – imports a montifun file.");
        System.out.println("        Imports within models are not supported.");
        System.out.println(":vars – lists all variables of the outer frame.");
        System.out.println(":funcs – lists all functions of the outer frame.");
        System.out.println(":imports – lists all imports of java classes.");
        System.out.println(":timing – toggles timing.");
        System.out.println("          Variance is high! This is NOT a benchmark!");
        break;
      case "exit":
        exitCalled = true;
        break;
      case "timing":
        timing = !timing;
        break;
      case "vars":
        printAllVariables();
        break;
      case "funcs":
        printAllFunctions();
        break;
      case "imports":
        printAllImports();
        break;
      case "import":
        importFile(command);
        break;
      default:
        System.out.println("Unknown command: " + command[0]);
        evaluateCommand(new String[] { "help" });
    }
  }

  protected void printAllVariables() {
    System.out.println("Variables:");
    System.out.println(
        getInterpreter().getTopMostFrame().printVariablesForLog()
    );
  }

  protected void printAllFunctions() {
    MIFrameLayoutForBasicSymbols frameLayout =
        interpreter.getTopMostFrame().getFrameLayout();
    List<FunctionSymbol> functions = new ArrayList<>(
        interpreterData.getFunctions().keySet()
    );
    functions.sort(Comparator.comparing(FunctionSymbol::getFullName));

    System.out.println("Functions:");
    for (FunctionSymbol function : functions) {
      System.out.println(
          "  " + function.getFullName()
              + " : " + function.getFunctionType().printFullName()
      );
    }
  }

  protected void printAllImports() {
    System.out.println("Imports:");
    for (ImportStatement impStmt : artifactScope.getImportsList()) {
      System.out.println(
          "  " + impStmt.getStatement() + (impStmt.isStar() ? ".*" : "")
      );
    }
  }

  protected void importFile(String[] command) {
    if (command.length <= 1) {
      System.out.println("No input filename provided");
      return;
    }
    Path path = Paths.get(command[1]);
    for (int i = 2; i < command.length; ++i) {
      path = path.resolve(Paths.get(command[i]));
    }
    importModelFile(path);
  }

  public void importModelFile(Path path) {
    Optional<IMontiFunArtifactScope> artifactScope = Optional.empty();
    boolean errorOccurred = false;
    try {
      String fileStr = path.toFile().getAbsolutePath();
      Optional<ASTMFCompilationUnit> astOpt =
          MontiFunMill.parser().parse(fileStr);
      if (astOpt.isPresent()) {
        ASTMFCompilationUnit ast = astOpt.get();
        artifactScope = Optional.of(MFSymbolTableUtil.runSymTabGenitor(ast));
        if (completeSymTabAndCheckCoCos(ast)) {
          registerImportedDeclarations(ast);
        }
      }
    }
    catch (Exception e) {
      System.out.println("The following internal exception "
          + "occurred during loading of the import: " + e.getMessage()
      );
      errorOccurred = true;
    }
    if (Log.getErrorCount() > 0) {
      errorOccurred = true;
    }
    if (errorOccurred) {
      System.out.println(
          "Unable to load import."
              + " We will try to recover, but the REPL may turn unstable."
              + System.lineSeparator() + "Consider restarting."
      );
      if (artifactScope.isPresent()) {
        MontiFunMill.globalScope().removeSubScope(artifactScope.get());
      }
    }
    else {
      System.out.println("Imported successfully.");
    }
  }

  protected boolean completeSymTabAndCheckCoCos(ASTMontiFunNode ast) {
    MFSymbolTableUtil.runSymTabCompleter(ast);
    if (Log.getErrorCount() > 0) {
      return false;
    }
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase1().checkAll(ast);
    if (Log.getErrorCount() > 0) {
      return false;
    }
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase2().checkAll(ast);
    return Log.getErrorCount() == 0;
  }

  protected boolean completeSymTabAndCheckCoCos(ASTExpression ast) {
    MFSymbolTableUtil.runSymTabCompleter(ast);
    if (Log.getErrorCount() > 0) {
      return false;
    }
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase1().checkAll(ast);
    if (Log.getErrorCount() > 0) {
      return false;
    }
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase2().checkAll(ast);
    return Log.getErrorCount() == 0;
  }

  protected void registerFunction(ASTMFFunctionDeclaration funcDecl) {
    FunctionSymbol funcSym = funcDecl.getSymbol();
    MIFrameLayoutForBasicSymbols funcScopeLayout =
        new MIFrameLayoutForBasicSymbols(
            interpreter.getTopMostFrame().getFrameLayout()
        );
    List<MISetter> paramSetters = new ArrayList<>();
    for (ASTMFParameter par : funcDecl.getMFParameterList()) {
      VariableSymbol parSym = par.getSymbol();
      funcScopeLayout.declareVariable(parSym);
      paramSetters.add(funcScopeLayout.getVariableSetter(parSym));
    }
    MICalculation calc = createCalculation(
        funcDecl.getExpression(),
        funcScopeLayout
    );

    MCValueFunctionOfModel functionValue = new MCValueFunctionOfModel(
        interpreter.getTopMostFrame(),
        funcScopeLayout, paramSetters, calc
    );
    modelFunctions.add(functionValue);
    interpreter.addFunction(funcSym, functionValue);
  }

  protected MICalculation createCalculation(
      ASTExpression expression,
      MIFrameLayoutForBasicSymbols enclosingLayout
  ) {
    interpreterData.reset();
    interpreterData.getFrameLayoutStack().push(enclosingLayout);
    expression.accept(traverser);
    interpreterData.getFrameLayoutStack().pop();
    return interpreterData.popCalculation();
  }

  protected void updateFunctionParentFrames() {
    MIFrame topFrame = interpreter.getTopMostFrame();
    for (MCValueFunctionOfModel functionValue : modelFunctions) {
      functionValue.setNewParentFrame(topFrame);
    }
  }

  protected void registerImportedDeclarations(ASTMFCompilationUnit ast) {
    Map<VariableSymbol, ASTExpression> variables = new LinkedHashMap<>();
    Map<FunctionSymbol, ASTExpression> functions = new LinkedHashMap<>();
    VariableDependencyOrderResolver dependencyResolver =
        new VariableDependencyOrderResolver();
    for (ASTMFConstantDeclaration constDecl
        : ast.getMFArtifact().getMFConstantDeclarationList()) {
      interpreter.addVariable(constDecl.getSymbol());
      variables.put(constDecl.getSymbol(), constDecl.getExpression());
    }
    for (ASTMFFunctionDeclaration funcDecl
        : ast.getMFArtifact().getMFFunctionDeclarationList()) {
      registerFunction(funcDecl);
      functions.put(funcDecl.getSymbol(), funcDecl.getExpression());
    }
    Optional<List<VariableSymbol>> orderedVariables =
        dependencyResolver.getVariableEvaluationOrder(variables, functions);
    if (orderedVariables.isEmpty()) {
      throw new IllegalStateException(
          "Unable to evaluate imported constants in dependency order."
      );
    }
    for (VariableSymbol variable : orderedVariables.get()) {
      MCValue value = interpreter.interpret(variables.get(variable));
      if (value.isError()) {
        throw new IllegalStateException(value.printValue());
      }
      interpreter.setVariable(variable, value);
    }
  }

  protected void printEvaluationResult(MCValue value) {
    if (value.isError()) {
      System.out.println("Evaluation failed: " + value.printValue());
    }
    else {
      System.out.println(value.printValue());
    }
  }

  protected <T> T withTiming(Supplier<T> executee, String phaseName) {
    Instant startTime = Instant.now();
    T res = executee.get();
    Instant endTime = Instant.now();
    if (timing) {
      Duration duration = Duration.between(startTime, endTime);
      double millis = duration.toNanos() / 1_000_000.0;
      String msg = String.format(
          Locale.ROOT,
          phaseName + ": %.3f ms",
          millis
      );
      System.out.println(msg);
    }
    return res;
  }

  protected void withTiming(Runnable executee, String phaseName) {
    withTiming(() -> {
          executee.run();
          return 42;
        },
        phaseName
    );
  }
}
