/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunTool;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MF2CDTest extends AbstractTest {

  protected final String OUTPUT_DIR = RELATIVE_JAVA_OUTPUT_PATH + "/MF2CDTest";

  protected final String TEMPLATE_PATH = "src/main/resources";

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MFSymbolTableUtil.initAndPrepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  @Disabled // TODO: The mill-switch is not properly done!
  // 0x70000x83115 AST node type ASTSetEnumeration expected a visitor of type de.monticore.ocl.setexpressions._visitor.SetExpressionsTraverser, but got de.monticore.visitor.ITraverser....
  public void createValidCD(String fileName) throws IOException {
    //not yet supported
    assumeFalse(fileName.contains("typeInference"));
    assumeFalse(fileName.contains("siunits"));
    assumeFalse(fileName.contains("unions"));

    // load model
    ASTMFCompilationUnit mfCompilationUnit = loadASTWithSymbols(fileName);

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
    compile(Collections.singleton(javaFileOpt.get()));

    assertNoFindings();
  }

  @Test
  public void createValidCDCircularDependency() throws IOException {
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
    Collection<File> javaFiles = new ArrayList<>();
    findOutputFile("a.java").ifPresent(javaFiles::add);
    findOutputFile("b.java").ifPresent(javaFiles::add);
    assertEquals(2, javaFiles.size());
    compile(javaFiles);
    //then
    assertNoFindings();
  }

  protected ASTMFCompilationUnit loadASTWithSymbols(String fileName) throws IOException {
    ASTMFCompilationUnit ast = parse(fileName);
    assertNoFindings();
    MFSymbolTableUtil.runSymTabGenitor(ast);
    assertNoFindings();
    MFSymbolTableUtil.runSymTabCompleter(ast);
    assertNoFindings();
    checkAllCoCos(ast);
    assertNoFindings();
    return ast;
  }

  /**
   * tries to compile the files
   * fails the test if there were compilation errors
   *
   * @param files to be compiled
   * @throws IOException file exception
   */
  protected void compile(Collection<File> files) throws IOException {
    List<String> options = Arrays.asList(
        "-d",
        OUTPUT_DIR
    );
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    DiagnosticCollector<JavaFileObject> diagnosticsCollector = new DiagnosticCollector<>();
    try (
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticsCollector,
            null, null);
    ) {
      Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(files);
      JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector,
          options, null,
          units);
      if (!task.call()) {
        String result = diagnostics2String(
            unmodifiableList(diagnosticsCollector.getDiagnostics())
        );
        for (JavaFileObject unit : units) {
          result += System.lineSeparator()
              + System.lineSeparator()
              + System.lineSeparator()
              + "***** " + unit.getName() + " *****"
              + System.lineSeparator()
              + unit.getCharContent(false);
        }
        fail(result);
      }
    }
  }

  /**
   * returns a string describing the compilation errors
   * used for more readable test results given failing tests.
   *
   * @param diagnostics The compilation diagnostic results
   * @return the compilation errors as String
   */
  protected String diagnostics2String(final List<Diagnostic<?>> diagnostics) {
    return diagnostics.stream().map(d ->
        new StringBuilder()
            .append(d.getLineNumber())
            .append(":")
            .append(d.getColumnNumber())
            .append(": ")
            .append(d.getMessage(null))
            .toString()
    ).collect(Collectors.joining(System.lineSeparator()));
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
