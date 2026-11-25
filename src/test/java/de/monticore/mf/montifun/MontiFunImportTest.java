// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun;

import de.monticore.io.paths.MCPath;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;

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

  @Test
  public void unidirectionalImportSamePackageTest() {
    ASTMFCompilationUnit ast = getTool().parse(readFile(
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender.mfun"
    ));
    MontiFunMill.globalScope().setSymbolPath(symbolPath);
    getTool().createSymbolTable(ast);
    getTool().completeSymbolTable(ast);
  }

  @Test
  public void unidirectionalImportDifferentPackageTest() {
    ASTMFCompilationUnit ast = getTool().parse(readFile(
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender2.mfun"
    ));
    MontiFunMill.globalScope().setSymbolPath(symbolPath);
    getTool().createSymbolTable(ast);
    getTool().completeSymbolTable(ast);
  }

  @Test
  public void circularDependencyImportTest() {
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
