// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MontiFunSymbolTableTest extends AbstractTest {

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.prepareMill();
  }

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
