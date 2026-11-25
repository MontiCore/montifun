// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun;

import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.monticore.runtime.junit.AbstractTestLanguageTool;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;

/**
 * {@link MontiFunTool}, wrapped to simplify writing tests.
 * s.a. {@link AbstractTestLanguageTool}.
 */
public class MontiFunTestLanguageTool
    extends AbstractTestLanguageTool<ASTMFCompilationUnit, IMontiFunArtifactScope> {

  public void initLanguage() {
    MFSymbolTableUtil.initAndPrepareMill(false);
  }

  @Override
  protected Optional<ASTMFCompilationUnit> _parse(String modelStr) throws IOException {
    return MontiFunMill.parser().parse_String(modelStr);
  }

  @Override
  protected IMontiFunArtifactScope _createSymbolTable(ASTMFCompilationUnit ast) {
    return MontiFunMill.scopesGenitorDelegator().createFromAST(ast);
  }

  @Override
  protected void _completeSymbolTable(ASTMFCompilationUnit ast) {
    MFSymbolTableUtil.runSymTabCompleter(ast);
  }

  @Override
  protected void _runPost_completeSymbolTable(ASTMFCompilationUnit ast) {
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase1().checkAll(ast);
    assertNoFindings();
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase2().checkAll(ast);
  }

}
