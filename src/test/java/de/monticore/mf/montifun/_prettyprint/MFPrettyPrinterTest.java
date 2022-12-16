// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._prettyprint;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.prettyprint.IndentPrinter;
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
    final ASTMFCompilationUnit ast = parse(fileName);
    final MontiFunFullPrettyPrinter prettyPrinter = new MontiFunFullPrettyPrinter(new IndentPrinter());

    // when
    String prettyPrinted = prettyPrinter.prettyprint(ast);

    // then
    Optional<ASTMFCompilationUnit> prettyASTOpt = parser.parse_String(prettyPrinted);
    assertTrue(prettyASTOpt.isPresent());
    assertTrue(ast.deepEquals(prettyASTOpt.get()));
    assertNoFindings();
  }
}
