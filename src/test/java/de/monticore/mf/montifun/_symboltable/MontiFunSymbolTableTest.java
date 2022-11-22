// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MontiFunSymbolTableTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldCreateSymTabForValidModels(String fileName) throws IOException {
    // not yet supported
    assumeFalse(fileName.contains("genericFunctions"));
    //todo enable after fix of
    //https://git.rwth-aachen.de/monticore/monticore/-/issues/3302
    assumeFalse(fileName.contains("javaImport"));

    // given / when
    createASTWithSymTab(fileName);

    //then
    assertNoFindings();
  }
}
