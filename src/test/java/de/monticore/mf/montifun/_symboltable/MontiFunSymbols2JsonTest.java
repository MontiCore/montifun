// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MontiFunSymbols2JsonTest extends AbstractTest {

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.prepareMill();
  }

  @Test
  public void testInit2Json() {
    new MontiFunSymbols2Json().init();
    assertNoFindings();
  }

  @Test
  public void testInit2JsonWithCD4C() {
    MFSymbolTableUtil.addCD4CSymbols();
    new MontiFunSymbols2Json().init();
    assertNoFindings();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldSerializeAndDeserializeSymTab(String fileName) throws IOException {
    // not yet supported
    assumeFalse(fileName.contains("genericFunctions"));
    ASTMFCompilationUnit ast = createASTWithSymTab(fileName);

    // given
    MontiFunSymbols2Json mf2json = new MontiFunSymbols2Json();
    mf2json.init();
    assertNoFindings();

    // when
    String json = mf2json.serialize(ast.getEnclosingScope());
    IMontiFunArtifactScope scope = mf2json.deserialize(json);

    //then
    assertNoFindings();
  }
}

