// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import de.monticore.class2mc.Class2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunScopesGenitorDelegator;
import de.monticore.mf.montifun._symboltable.MontiFunSymbolTableCompleter;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun.types.check.FullDeriveFromMontiFun;
import de.monticore.mf.montifun.types.check.FullSynthesizeFromMontiFun;
import de.monticore.mf.montifun.util.library.MFCollectionType;
import de.monticore.mf.montifun.util.library.MFListType;
import de.monticore.mf.montifun.util.library.MFSetType;
import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions._symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolDeSer;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolDeSer;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Paths;

/**
 * Contains helpers that execute MontiCore API that are almost always called together
 */
public class MFSymbolTableUtil {

  static public void prepareMill() {
    Class2MCResolver resolver = new Class2MCResolver();

    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    MontiFunMill.reset();
    MontiFunMill.init();
    MontiFunMill.globalScope().clear();

    BasicSymbolsMill.initializePrimitives();

    OOSymbolsMill.globalScope().setSymbolPath(new MCPath(Paths.get("")));
    MontiFunMill.globalScope().addAdaptedTypeSymbolResolver(resolver);

    addCollectionTypes();
  }

  protected static void addCollectionTypes() {
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
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    return genitor.createFromAST(ast);
  }

  static public void runSymTabCompleter(ASTMFCompilationUnit ast) {
    MontiFunSymbolTableCompleter stCompleter = new MontiFunSymbolTableCompleter();
    stCompleter.setDeriver(new FullDeriveFromMontiFun());
    stCompleter.setSynthesizer(new FullSynthesizeFromMontiFun());

    OCLExpressionsSymbolTableCompleter stCompleter2 = new OCLExpressionsSymbolTableCompleter(
        ast.getMCImportStatementList(),
        "unused"
    );
    stCompleter2.setDeriver(new FullDeriveFromMontiFun());
    stCompleter2.setSynthesizer(new FullSynthesizeFromMontiFun());

    SetExpressionsSymbolTableCompleter stCompleter3 = new SetExpressionsSymbolTableCompleter(
        ast.getMCImportStatementList(),
        "unused"
    );
    stCompleter3.setDeriver(new FullDeriveFromMontiFun());
    stCompleter3.setSynthesizer(new FullSynthesizeFromMontiFun());

    MontiFunTraverser t = MontiFunMill.traverser();
    t.add4MontiFun(stCompleter);
    t.setMontiFunHandler(stCompleter);
    t.setOCLExpressionsHandler(stCompleter2);
    t.add4BasicSymbols(stCompleter2);
    t.add4OCLExpressions(stCompleter2);
    t.setSetExpressionsHandler(stCompleter3);
    t.add4BasicSymbols(stCompleter3);
    t.add4SetExpressions(stCompleter3);
    ast.accept(t);
  }

  protected static void addTypeSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new TypeSymbolDeSer());
  }

  protected static void addFunctionSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new FunctionSymbolDeSer());
  }

  protected static void addVariableSymbol(String symbolFqn) {
    MontiFunMill.globalScope().putSymbolDeSer(symbolFqn, new VariableSymbolDeSer());
  }

  public static void addCD4CSymbols() {
    addTypeSymbol("de.monticore.cdbasis._symboltable.CDTypeSymbol");
    addFunctionSymbol("de.monticore.cd4codebasis._symboltable.CDMethodSignatureSymbol");
    addVariableSymbol("de.monticore.symbols.oosymbols._symboltable.FieldSymbol");
  }

  public static void loadSymbolFile(String filePath) {
    Log.debug("Read symbol file \"" + filePath + "\"", "MFSymbolTableUtil");
    MontiFunSymbols2Json deSer = new MontiFunSymbols2Json();
    MontiFunMill.globalScope().addSubScope(deSer.load(filePath));
  }
}
