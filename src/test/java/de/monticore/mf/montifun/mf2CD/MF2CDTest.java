/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.mf2CD;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MF2CDTest extends AbstractTest {

  protected final String OUTPUT_DIR = "target/gen";

  protected final String TEMPLATE_PATH = "src/main/resources";

  protected final MontiFunParser parser = MontiFunMill.parser();

  protected final CD4CodeFullPrettyPrinter cd4cPrettyPrinter = new CD4CodeFullPrettyPrinter();

  GlobalExtensionManagement glex;

  GeneratorSetup generatorSetup;

  @BeforeEach
  public void setup() {
    glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File(OUTPUT_DIR));
    generatorSetup.setTracing(false);
    generatorSetup.setAdditionalTemplatePaths(Arrays.asList(new File(TEMPLATE_PATH)));
    CD4C.init(generatorSetup);
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void createValidCD(String fileName) throws IOException {
    ASTMFCompilationUnit mfCompilationUnit = parser.parse(fileName).get();
    MF2CDConverter mf2CDConverter = new MF2CDConverter();

    ASTCDCompilationUnit cdCompilationUnit = mf2CDConverter.convert(mfCompilationUnit,
        generatorSetup.getGlex());

    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    cdGenerator.generate(cdCompilationUnit);

    String prettyPrintedModel = cd4cPrettyPrinter.prettyprint(cdCompilationUnit);

    // the content is to be checked manually at the moment
    assertTrue(Log.getFindings().isEmpty());
  }

}
