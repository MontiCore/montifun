// (c) https://github.com/MontiCore/monticore
package de.monticore.mf;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AbstractTest {

  @BeforeAll
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.getFindings().clear();
  }

  protected static final String RELATIVE_MODEL_PATH = "src/test/resources";

  protected static final String RELATIVE_VALID_MODEL_PATH =
      RELATIVE_MODEL_PATH + "/testinput/validGrammarModels";

  public static String[] getParsableModels() {
    File f = new File(RELATIVE_VALID_MODEL_PATH);
    String[] filenames = f.list();
    assertNotNull(filenames);
    filenames = Arrays.stream(filenames)
        .sorted()
        .map(filename -> Paths.get(RELATIVE_VALID_MODEL_PATH, filename).toString())
        .collect(Collectors.toList())
        .toArray(filenames);

    return filenames;
  }

  public static void assertNoFindings() {
    assertTrue(Log.getFindings().isEmpty(),
        Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator()))
    );
  }

  public static ASTMFCompilationUnit parse(String fileName) throws IOException {
    MontiFunParser parser = MontiFunMill.parser();
    Optional<ASTMFCompilationUnit> compilationUnitOpt = parser.parse(fileName);
    assertFalse(parser.hasErrors());
    assertTrue(compilationUnitOpt.isPresent());
    return compilationUnitOpt.get();
  }

}
