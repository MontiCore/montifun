// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MontiFunCoCosTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldAcceptValidModels(String fileName) {
    // todo https://git.rwth-aachen.de/monticore/monticore/-/issues/4320
    assumeFalse(fileName.contains("siunit"));
    getTool().getASTWithSymbolTable(readFile(fileName));
  }

  @ParameterizedTest
  @MethodSource("getInvalidCoCoModels")
  public void shouldRejectInvalidModels(String fileName) {
    // Given -- parse+symtab
    ASTMFCompilationUnit ast = getTool().parse(readFile(fileName));
    getTool().createSymbolTable(ast);
    getTool().completeSymbolTable(ast);
    // When
    checkAllCoCos(ast);

    assertFalse(Log.getFindings().isEmpty(),
        "Expected findings for invalid model: " + fileName);
  }

  protected void checkAllCoCos(ASTMFCompilationUnit ast) {
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase1().checkAll(ast);
    MontiFunCoCoChecker.getCheckerForAllCoCosPhase2().checkAll(ast);
  }

}
