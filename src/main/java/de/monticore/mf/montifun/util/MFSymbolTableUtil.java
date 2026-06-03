// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import de.monticore.ast.ASTNode;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMontiFunNode;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunScopesGenitorDelegator;
import de.monticore.mf.montifun._symboltable.MontiFunSymbolTableCompleter;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun.util.library.MFCollectionType;
import de.monticore.mf.montifun.util.library.MFListType;
import de.monticore.mf.montifun.util.library.MFSetType;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbolDeSer;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolDeSer;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Paths;

/**
 * Contains helpers that execute MontiCore API that are almost always called together
 */
public class MFSymbolTableUtil {

  static public void initMill() {
    MontiFunMill.reset();
    MontiFunMill.init();
    MontiFunMill.globalScope().clear();
  }

  static public void initAndPrepareMill() {
    initMill();
    prepareMillWithoutInit();
  }

  static public void prepareMillWithoutInit() {
    BasicSymbolsMill.initializePrimitives();
    addClass2MC();
    addCollectionTypes();
  }

  static public void addClass2MC() {
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    OOClass2MCResolver resolver = new OOClass2MCResolver();
    globalScope.addAdaptedOOTypeSymbolResolver(resolver);
    globalScope.addAdaptedTypeSymbolResolver(resolver);
  }

  public static void addCollectionTypes() {
    MFCollectionType c = new MFCollectionType();
    MFListType l = new MFListType();
    MFSetType s = new MFSetType();
    c.addCollectionType();
    l.addListType();
    s.addSetType();
    c.addMethodsAndFields();
    l.addMethodsAndFields();
    s.addMethodsAndFields();
  }

  static public IMontiFunArtifactScope runSymTabGenitor(ASTMFCompilationUnit ast) {
    //todo remove if java.lang.* is default imported in cd4a again
    addDefaultImports(ast);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    return genitor.createFromAST(ast);
  }

  static public void runSymTabCompleter(ASTNode ast) {
    MontiFunTraverser symTabCompleter = MontiFunMill.inheritanceTraverser();

    MontiFunSymbolTableCompleter montiFunCompleter = new MontiFunSymbolTableCompleter();
    symTabCompleter.add4MontiFun(montiFunCompleter);
    symTabCompleter.setMontiFunHandler(montiFunCompleter);

    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    symTabCompleter.setOCLExpressionsHandler(oclExprCompleter);
    symTabCompleter.add4BasicSymbols(oclExprCompleter);
    symTabCompleter.add4OCLExpressions(oclExprCompleter);

    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    symTabCompleter.setSetExpressionsHandler(setExprCompleter);
    symTabCompleter.add4BasicSymbols(setExprCompleter);
    symTabCompleter.add4SetExpressions(setExprCompleter);

    LambdaExpressionsSTCompleteTypes2 lambdaExprCompleter =
        new LambdaExpressionsSTCompleteTypes2();
    symTabCompleter.add4LambdaExpressions(lambdaExprCompleter);

    ast.accept(symTabCompleter);
  }

  protected static void addTypeSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new TypeSymbolDeSer());
  }

  protected static void addOOTypeSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new OOTypeSymbolDeSer());
  }

  protected static void addFunctionSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new FunctionSymbolDeSer());
  }

  protected static void addMethodSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new MethodSymbolDeSer());
  }

  protected static void addVariableSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new VariableSymbolDeSer());
  }

  protected static void addFieldSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new FieldSymbolDeSer());
  }

  public static void addCD4CSymbols() {
    addOOTypeSymbol("de.monticore.cdbasis._symboltable.CDTypeSymbol");
    addMethodSymbol("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol");
    addFieldSymbol("de.monticore.symbols.oosymbols._symboltable.FieldSymbol");
  }

  protected static void addDefaultImports(ASTMFCompilationUnit artifact) {
    artifact.addImportStatement("java.lang.*");
  }

  public static void loadSymbolFile(String filePath) {
    Log.debug("Read symbol file \"" + filePath + "\"", "MFSymbolTableUtil");
    MontiFunSymbols2Json deSer = new MontiFunSymbols2Json();
    MontiFunMill.globalScope().addSubScope(deSer.load(filePath));
  }
}
