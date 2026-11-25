/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.mf2cd.MF2CDTestLanguageTool;
import de.monticore.mf.montifun.MontiFunTestLanguageTool;
import de.monticore.mf.montifun.MontiFunTool;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.runtime.junit.TestJavaCompiler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MF2CDTest extends AbstractTest {

  protected final String OUTPUT_DIR = RELATIVE_JAVA_OUTPUT_PATH + "/MF2CDTest";

  protected final String TEMPLATE_PATH = "src/main/resources";

  @Override
  protected MontiFunTestLanguageTool createTool() {
    return new MF2CDTestLanguageTool();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void createValidCD(String fileName) throws Exception {
    //not yet supported
    assumeFalse(fileName.contains("typeInference"));
    assumeFalse(fileName.contains("siunits"));
    assumeFalse(fileName.contains("unions"));

    // load model
    ASTMFCompilationUnit mfCompilationUnit =
        getTool().getASTWithSymbolTable(readFile(fileName));

    // MontiFun -> CD, CD -> Java
    // note: creating CDGenerator BEFORE MF2CDConverter::convert!
    GeneratorSetup generatorSetup = MF2CDConverter.getDefaultGeneratorSetup(
        new File(OUTPUT_DIR),
        List.of(new File(TEMPLATE_PATH))
    );
    MF2CDConverter mf2CDConverter = new MF2CDConverter();
    ASTCDCompilationUnit cdCompilationUnit =
        mf2CDConverter.convert(mfCompilationUnit, generatorSetup);
    assertNoFindings();
    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    cdGenerator.generate(cdCompilationUnit);
    assertNoFindings();

    // test Java compilation:
    Optional<File> javaFileOpt = findOutputFile(mfCompilationUnit.getMFArtifact().getName());
    assertTrue(javaFileOpt.isPresent());
    try (TestJavaCompiler compiler = new TestJavaCompiler(Path.of(OUTPUT_DIR))) {
      compiler.compile(javaFileOpt.get());
    }

    assertNoFindings();
  }

  @Test
  public void createValidCDCircularDependency() throws Exception {
    // todo enable after https://git.rwth-aachen.de/monticore/monticore/-/issues/3319
    assumeFalse(true);
    // given / when
    new MontiFunTool().run(new String[] {
        "-i",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/a.mfun",
        RELATIVE_MODEL_PATH + "/testinput/circularDependency/b.mfun",
        "-gen",
        OUTPUT_DIR,
    });
    // test Java compilation:
    List<File> javaFiles = new ArrayList<>();
    findOutputFile("a.java").ifPresent(javaFiles::add);
    findOutputFile("b.java").ifPresent(javaFiles::add);
    assertEquals(2, javaFiles.size());
    try (TestJavaCompiler compiler = new TestJavaCompiler(Path.of(OUTPUT_DIR))) {
      compiler.compile(javaFiles);
    }
    //then
    assertNoFindings();
  }

  /**
   * finds the generated java file
   *
   * @param modelName name of the montifun model
   * @return the java file if found
   * @throws IOException file error
   */
  protected Optional<File> findOutputFile(String modelName) throws IOException {
    return Files.walk(Paths.get(OUTPUT_DIR))
        .filter(path -> path.toString().endsWith(".java"))
        .filter(path -> path.toString().contains(modelName))
        .map(Path::toFile)
        .findFirst();
  }

}
