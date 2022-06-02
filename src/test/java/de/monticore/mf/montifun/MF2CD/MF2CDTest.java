/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class MF2CDTest extends AbstractTest {

  protected final String OUTPUT_DIR = RELATIVE_JAVA_OUTPUT_PATH + "/MF2CDTest";

  protected final String TEMPLATE_PATH = "src/main/resources";

  GlobalExtensionManagement glex;

  GeneratorSetup generatorSetup;

  @BeforeEach
  public void setup() {
    super.setup();
    glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File(OUTPUT_DIR));
    generatorSetup.setTracing(false);
    generatorSetup.setAdditionalTemplatePaths(Arrays.asList(new File(TEMPLATE_PATH)));
    CD4C.init(generatorSetup);
    MFSymbolTableUtil.prepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void createValidCD(String fileName) throws IOException {
    //not yet supported
    assumeFalse(fileName.contains("genericFunctions"));
    assumeFalse(fileName.contains("lambdas"));

    // load model
    ASTMFCompilationUnit mfCompilationUnit = loadASTWithSymbols(fileName);

    // MontiFun -> CD, CD -> Java
    // note: creating CDGenerator BEFORE MF2CDConverter::convert!
    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    MF2CDConverter mf2CDConverter = new MF2CDConverter();
    ASTCDCompilationUnit cdCompilationUnit = mf2CDConverter.convert(mfCompilationUnit,
        generatorSetup.getGlex());
    cdGenerator.generate(cdCompilationUnit);

    // test Java compilation:
    Optional<File> javaFileOpt = findOutputFile(mfCompilationUnit.getMFArtifact().getName());
    assertTrue(javaFileOpt.isPresent());
    compile(javaFileOpt.get());

    assertNoFindings();
  }

  protected ASTMFCompilationUnit loadASTWithSymbols(String fileName) throws IOException {
    ASTMFCompilationUnit ast = parse(fileName);
    MFSymbolTableUtil.runSymTabGenitor(ast);
    MFSymbolTableUtil.runSymTabCompleter(ast);
    return ast;
  }

  /**
   * tries to compile the file
   * fails the test if there were compilation errors
   *
   * @param file to be compiled
   * @throws IOException file exception
   */
  protected void compile(File file) throws IOException {
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
      Iterable<? extends JavaFileObject> units = fileManager.getJavaFileObjectsFromFiles(
          Collections.singleton(file));
      JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnosticsCollector,
          options, null,
          units);
      if (!task.call()) {
        fail(diagnostics2String(
            Collections.unmodifiableList(diagnosticsCollector.getDiagnostics())));
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
