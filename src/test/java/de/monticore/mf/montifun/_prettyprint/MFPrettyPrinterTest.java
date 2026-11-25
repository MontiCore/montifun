// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._prettyprint;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.runtime.junit.PrettyPrinterTester;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MFPrettyPrinterTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void prettyPrintsEquivalent(String fileName) {
    String model = readFile(fileName);
    MontiFunParser parser = MontiFunMill.parser();

    PrettyPrinterTester.testPrettyPrinter(
        model,
        parser,
        parser::parse_String,
        ast -> MontiFunMill.prettyPrint(ast, true)
    );
  }
}
