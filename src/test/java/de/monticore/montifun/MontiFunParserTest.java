/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun;

import de.monticore.montifun._parser.MontiFunParser;
import de.se_rwth.commons.logging.Log;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class MontiFunParserTest {

  private final MontiFunParser parser = MontiFunMill.parser();
  private static final String MODEL_PATH = "src/test/resources/";

  /**
   * Setup executed before the execution of each test.
   */
  @Before
  public void init() {
    Log.enableFailQuick(false);
  }


  @Test
  public void testParseFibonacci() throws IOException {
    assertTrue(parser.parse(MODEL_PATH + "parser/fibonacci.mfun").isPresent());
  }

  //not implemented yet
  /*
  @Test
  public void testParseADT() throws IOException {
    assertTrue(parser.parse(MODEL_PATH + "parser/tree.mfun").isPresent());
  }
   */

  /*
  @Test
  public void testParseImports() throws IOException {
    assertTrue(parser.parse(MODEL_PATH + "parser/imports.mfun").isPresent());
  }
   */
}
