/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun._parser;

import de.monticore.mf.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

public class MFParserTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldParseValidInput(String fileName) throws IOException {
    parse(fileName);
    assertNoFindings();
  }
}
