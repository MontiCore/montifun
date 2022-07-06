// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun;

import de.monticore.io.paths.MCPath;
import de.monticore.mf.AbstractTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MontiFunImportTest extends AbstractTest {

  protected MCPath symbolPath = new MCPath(Paths.get(RELATIVE_SYMBOL_OUTPUT_PATH));

  @BeforeAll
  public static void init2() {
    //generate symbols to load
    MontiFunTool.main(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee.mfun",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee2.mfun",
        "-sym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee.mfsym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee2.mfsym",
    });
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
    MontiFunTool.main(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/a.mfun",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/b.mfun",
        "-sym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/circularDependency/a.mfun",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/circularDependency/b.mfun"
    });
    //then
    assertNoFindings();
  }

}
