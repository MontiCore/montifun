// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun;

import de.monticore.io.paths.MCPath;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MontiFunImportTest extends AbstractTest {

  protected MCPath symbolPath = new MCPath(Paths.get(RELATIVE_SYMBOL_OUTPUT_PATH));

  @BeforeAll
  public static void createDependeeSymFiles() {
    // generate symbols to load
    new MontiFunTool().run(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee.mfun",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee2.mfun",
        "-sym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee.mfsym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee2.mfsym",
    });
  }

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.prepareMill();
  }


  @Test
  public void unidirectionalImportSamePackageTest() throws IOException {
    // given
    createASTWithSymTab(
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender.mfun",
        symbolPath
    );
    //then
    assertNoFindings();
  }

  @Test
  public void unidirectionalImportDifferentPackageTest() throws IOException {
    // given / when
    createASTWithSymTab(
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender2.mfun",
        symbolPath
    );
    //then
    assertNoFindings();
  }

  @Test
  public void circularDependencyImportTest() throws IOException {
    // given / when
    new MontiFunTool().run(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/a.mfun",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/b.mfun",
        "-sym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/circularDependency",
    });
    //then
    assertNoFindings();
  }

}
