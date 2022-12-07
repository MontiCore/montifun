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

    // given / when
    createASTWithSymTab(fileName);

    //then
    assertNoFindings();
  }
}
