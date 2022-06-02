// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun;

import de.monticore.io.paths.MCPath;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

public class MontiFunImportTest extends AbstractTest {

  protected MCPath symbolPath = new MCPath(Paths.get(RELATIVE_SYMBOL_OUTPUT_PATH));

  @BeforeAll
  public static void init() {
    //generate symbols to load
    MontiFunTool.main(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee.mfun",
        RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/dependee2.mfun",
        "-sym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee.mfsym",
        RELATIVE_SYMBOL_OUTPUT_PATH + "/pkg/dependee2.mfsym"
    });
  }

  @Test
  public void unidirectionalImportSamePackageTest() throws IOException {
    // given
    String fileName = RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender.mfun";
    createASTWithSymTab(fileName);
    //then
    assertNoFindings();
  }

  @Test
  public void unidirectionalImportDifferentPackageTest() throws IOException {
    // given
    String fileName = RELATIVE_MODEL_PATH + "/testinput/intraModelDependency/depender2.mfun";
    createASTWithSymTab(fileName);
    //then
    assertNoFindings();
  }

  protected ASTMFCompilationUnit createASTWithSymTab(String fileName) throws IOException {
    ASTMFCompilationUnit ast = parse(fileName);
    MFSymbolTableUtil.prepareMill();
    MontiFunMill.globalScope().setSymbolPath(symbolPath);
    MFSymbolTableUtil.runSymTabGenitor(ast);
    MFSymbolTableUtil.runSymTabCompleter(ast);
    return ast;
  }

}
