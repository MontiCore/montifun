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
import static org.junit.jupiter.api.Assertions.assertTrue;
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

  /**
   * The functions in these models have the same name and the same type,
   * although the types are written differently,
   * e.g., (A | B) is the same type as (B | A),
   * and (A | B) is the same type as A, if B is a subtype of A.
   */
  protected static String[] getDuplicateFunctionModels() {
    return new String[] {
        RELATIVE_INVALID_COCO_MODEL_PATH + "/DuplicateFunctions.mfun",
        RELATIVE_INVALID_COCO_MODEL_PATH + "/DuplicateFunctionsWithUnionTypes.mfun",
        RELATIVE_INVALID_COCO_MODEL_PATH + "/DuplicateFunctionsWithSubsumedTypes.mfun",
    };
  }

  @ParameterizedTest
  @MethodSource("getDuplicateFunctionModels")
  public void shouldRejectDuplicateFunctions(String fileName) throws IOException {
    // Given
    ASTMFCompilationUnit ast = createASTWithSymTab(fileName);
    // When
    checkAllCoCos(ast);
    // Then
    assertTrue(Log.getFindings().stream()
            .anyMatch(finding -> finding.buildMsg().contains("0xF2007")),
        "Expected a finding of error code 0xF2007 for model: " + fileName);
  }

  protected static String[] getDuplicateConstantModels() {
    return new String[] {
        RELATIVE_INVALID_COCO_MODEL_PATH + "/DuplicateConstants.mfun",
    };
  }

  @ParameterizedTest
  @MethodSource("getDuplicateConstantModels")
  public void shouldRejectDuplicateConstants(String fileName) throws IOException {
    // Given
    ASTMFCompilationUnit ast = createASTWithSymTab(fileName);
    // When
    checkAllCoCos(ast);
    // Then
    assertTrue(Log.getFindings().stream()
            .anyMatch(finding -> finding.buildMsg().contains("0xA0923")),
        "Expected a finding of error code 0xA0923 for model: " + fileName);
  }

}
