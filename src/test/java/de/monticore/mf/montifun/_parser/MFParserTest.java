/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun._parser;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

public class MFParserTest extends AbstractTest {

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.initAndPrepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldParseValidInput(String fileName) throws IOException {
    parse(fileName);
    assertNoFindings();
  }
}
