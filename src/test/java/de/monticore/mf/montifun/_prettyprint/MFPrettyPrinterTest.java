// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._prettyprint;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MFPrettyPrinterTest extends AbstractTest {

  protected final MontiFunParser parser = MontiFunMill.parser();

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.prepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void prettyPrintsEquivalent(String fileName) throws IOException {
    // todo https://git.rwth-aachen.de/monticore/monticore/-/issues/4318
    assumeFalse(fileName.contains("siunit"));

    // given
    final ASTMFCompilationUnit ast = parse(fileName);

    // when
    String prettyPrinted = MontiFunMill.prettyPrint(ast, true);

    // then
    Optional<ASTMFCompilationUnit> prettyASTOpt = parser.parse_String(prettyPrinted);
    assertTrue(prettyASTOpt.isPresent());
    assertTrue(ast.deepEquals(prettyASTOpt.get()));
    assertNoFindings();
  }
}
