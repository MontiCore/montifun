/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.mf;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.mf._parser.MFParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class MFParserTest extends AbstractTest {

  protected final MFParser parser = MFMill.parser();

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldParseValidInput(String fileName) throws IOException {
    assertTrue(parser.parse(fileName).isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }
}
