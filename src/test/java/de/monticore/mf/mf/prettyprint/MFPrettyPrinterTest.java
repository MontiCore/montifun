// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf.prettyprint;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.mf.MFMill;
import de.monticore.mf.mf._ast.ASTMFCompilationUnit;
import de.monticore.mf.mf._parser.MFParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class MFPrettyPrinterTest extends AbstractTest {

  protected final MFParser parser = MFMill.parser();

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void prettyPrintsEquivalent(String fileName) throws IOException {
    // given
    final Optional<ASTMFCompilationUnit> astOpt = parser.parse(fileName);
    assertTrue(astOpt.isPresent());
    final MFFullPrettyPrinter prettyPrinter = new MFFullPrettyPrinter();

    // when
    String prettyPrinted = prettyPrinter.prettyprint(astOpt.get());

    // then
    Optional<ASTMFCompilationUnit> prettyASTOpt = parser.parse_String(prettyPrinted);
    assertTrue(prettyASTOpt.isPresent());
    assertTrue(astOpt.get().deepEquals(prettyASTOpt.get()));
  }
}
