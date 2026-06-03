// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MontiFunCoCosTest extends AbstractTest {

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.initAndPrepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldAcceptValidModels(String fileName) throws IOException {
    // todo https://git.rwth-aachen.de/monticore/monticore/-/issues/4320
    assumeFalse(fileName.contains("siunit"));

    // Given
    ASTMFCompilationUnit ast = createASTWithSymTab(fileName);
    // When
    checkAllCoCos(ast);

    // Then
    assertNoFindings();
  }

  @ParameterizedTest
  @MethodSource("getInvalidCoCoModels")
  public void shouldRejectInvalidModels(String fileName) throws IOException {
    // Given
    ASTMFCompilationUnit ast = createASTWithSymTab(fileName);
    // When
    checkAllCoCos(ast);

    assertFalse(Log.getFindings().isEmpty(),
        "Expected findings for invalid model: " + fileName);
  }

}
