// (c) https://github.com/MontiCore/monticore
package de.monticore.mf;

import de.monticore.mf.montifun.MontiFunTestLanguageTool;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.getFindings().clear();
    tool = createTool();
    tool.initLanguage();
  }

  protected static final String RELATIVE_MODEL_PATH = "src/test/resources";

  protected static final String RELATIVE_VALID_MODEL_PATH =
      RELATIVE_MODEL_PATH + "/testinput/validGrammarModels";

  protected static final String RELATIVE_INVALID_COCO_MODEL_PATH =
      RELATIVE_MODEL_PATH + "/testinput/invalidCoCoModels";

  protected static final String RELATIVE_SYMBOL_OUTPUT_PATH = "target/generated-test-symbols";

  protected static final String RELATIVE_JAVA_OUTPUT_PATH = "target/generated-test-sources";

  protected MontiFunTestLanguageTool tool;

  protected static String[] getParsableModels() {
    return getModelsInFolder(new File(RELATIVE_VALID_MODEL_PATH));
  }

  protected static String[] getInvalidCoCoModels() {
    return getModelsInFolder(new File(RELATIVE_INVALID_COCO_MODEL_PATH));
  }

  protected static String readFile(String fileName) {
    return readFile(Path.of(fileName));
  }

  protected static String readFile(Path filePath) {
    try {
      return Files.readString(filePath);
    }
    catch (IOException e) {
      return fail(e);
    }
  }

  protected MontiFunTestLanguageTool createTool() {
    return new MontiFunTestLanguageTool();
  }

  protected MontiFunTestLanguageTool getTool() {
    return tool;
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
