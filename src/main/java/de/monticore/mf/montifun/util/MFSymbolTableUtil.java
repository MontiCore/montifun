// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import de.monticore.class2mc.Class2MCResolver;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._symboltable.MontiFunScopesGenitorDelegator;
import de.monticore.mf.montifun._symboltable.MontiFunSymbolTableCompleter;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun.util.library.MFCollectionType;
import de.monticore.mf.montifun.util.library.MFListType;
import de.monticore.mf.montifun.util.library.MFSetType;
import de.monticore.ocl.oclexpressions._symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions._symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.ocl.types.check.OCLDeriver;
import de.monticore.ocl.types.check.OCLSynthesizer;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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

    MontiFunMill.globalScope().setFileExt("mfsym");

    OOSymbolsMill.globalScope().setSymbolPath(new MCPath(Paths.get("")));
    OOSymbolsMill.globalScope().addAdaptedOOTypeSymbolResolver(resolver);
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

  static public void runSymTabGenitor(ASTMFCompilationUnit ast) {
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    genitor.createFromAST(ast);
  }

  static public void runSymTabCompleter(ASTMFCompilationUnit ast) {
    MontiFunSymbolTableCompleter stCompleter = new MontiFunSymbolTableCompleter();
    stCompleter.setSynthesizer(new OCLSynthesizer());
    stCompleter.setDeriver(new OCLDeriver());
    OCLExpressionsSymbolTableCompleter stCompleter2 = new OCLExpressionsSymbolTableCompleter(
        ast.getMCImportStatementList(),
        "unused"
    );
    stCompleter2.setDeriver(new OCLDeriver());
    stCompleter2.setSynthesizer(new OCLSynthesizer());
    SetExpressionsSymbolTableCompleter stCompleter3 = new SetExpressionsSymbolTableCompleter(
        ast.getMCImportStatementList(),
        "unused"
    );
    stCompleter3.setDeriver(new OCLDeriver());
    stCompleter3.setSynthesizer(new OCLSynthesizer());

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

  public static void loadSymbolFile(String filePath) {
    Log.debug("Read symbol file \"" + filePath + "\"", "MFSymbolTableUtil");
    MontiFunSymbols2Json deSer = new MontiFunSymbols2Json();
    MontiFunMill.globalScope().addSubScope(deSer.load(filePath));
  }
}
