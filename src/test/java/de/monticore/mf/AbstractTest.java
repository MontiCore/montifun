// (c) https://github.com/MontiCore/monticore
package de.monticore.mf;

import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.getFindings().clear();
  }

  protected static final String RELATIVE_MODEL_PATH = "src/test/resources";

  protected static final String RELATIVE_VALID_MODEL_PATH =
      RELATIVE_MODEL_PATH + "/testinput/validGrammarModels";

  protected static final String RELATIVE_INVALID_COCO_MODEL_PATH =
      RELATIVE_MODEL_PATH + "/testinput/invalidCoCoModels";

  protected static final String RELATIVE_SYMBOL_OUTPUT_PATH = "target/generated-test-symbols";

  protected static final String RELATIVE_JAVA_OUTPUT_PATH = "target/generated-test-sources";

  protected static String[] getParsableModels() {
    return getModelsInFolder(new File(RELATIVE_VALID_MODEL_PATH));
  }

  protected static String[] getInvalidCoCoModels() {
    return getModelsInFolder(new File(RELATIVE_INVALID_COCO_MODEL_PATH));
  }

  protected static void assertNoFindings() {
    assertTrue(Log.getFindings().isEmpty(),
        Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator()))
    );
  }

  protected static ASTMFCompilationUnit parse(String fileName) throws IOException {
    MontiFunParser parser = MontiFunMill.parser();
    Optional<ASTMFCompilationUnit> compilationUnitOpt = parser.parse(fileName);
    assertNoFindings();
    assertFalse(parser.hasErrors());
    assertTrue(compilationUnitOpt.isPresent());
    return compilationUnitOpt.get();
  }

  protected ASTMFCompilationUnit createASTWithSymTab(String fileName) throws IOException {
    return createASTWithSymTab(fileName, new MCPath());
  }

  protected ASTMFCompilationUnit createASTWithSymTab(String fileName, MCPath symbolPath)
      throws IOException {
    ASTMFCompilationUnit ast = parse(fileName);
    MFSymbolTableUtil.initAndPrepareMill();
    MontiFunMill.globalScope().setSymbolPath(symbolPath);
    MFSymbolTableUtil.runSymTabGenitor(ast);
    MFSymbolTableUtil.runSymTabCompleter(ast);
    return ast;
  }

  protected void checkAllCoCos(ASTMFCompilationUnit ast) {
    checkAllCoCos(Set.of(ast));
  }

  protected void checkAllCoCos(Set<ASTMFCompilationUnit> asts) {
    MontiFunCoCoChecker checker1 =
        MontiFunCoCoChecker.getCheckerForAllCoCosPhase1();
    MontiFunCoCoChecker checker2 =
        MontiFunCoCoChecker.getCheckerForAllCoCosPhase2();
    for (ASTMFCompilationUnit ast : asts) {
      checker1.checkAll(ast);
    }
    for (ASTMFCompilationUnit ast : asts) {
      checker2.checkAll(ast);
    }
  }

  // helper

  protected static String[] getModelsInFolder(File folder) {
    String[] filenames = folder.list();
    assertNotNull(filenames);
    Path folderPath = folder.toPath();
    filenames = Arrays.stream(filenames)
        .sorted()
        .map(filename -> folderPath.resolve(filename).toString())
        .toList()
        .toArray(filenames);
    return filenames;
  }

}
