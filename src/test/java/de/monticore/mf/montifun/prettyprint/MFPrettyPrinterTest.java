// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.prettyprint;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MFPrettyPrinterTest extends AbstractTest {

  protected final MontiFunParser parser = MontiFunMill.parser();

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void prettyPrintsEquivalent(String fileName) throws IOException {
    // given
    final Optional<ASTMFCompilationUnit> astOpt = parser.parse(fileName);
    assertTrue(astOpt.isPresent());
    final MontiFunFullPrettyPrinter prettyPrinter = new MontiFunFullPrettyPrinter();

    // when
    String prettyPrinted = prettyPrinter.prettyprint(astOpt.get());

    // then
    Optional<ASTMFCompilationUnit> prettyASTOpt = parser.parse_String(prettyPrinted);
    assertTrue(prettyASTOpt.isPresent());
    assertTrue(astOpt.get().deepEquals(prettyASTOpt.get()));
    assertTrue(Log.getFindings().isEmpty());
  }
}
