/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MF2CD.MF2CDConverter;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.monticore.mf.montifun.util.MontiFunRepl;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MontiFunTool extends MontiFunToolTOP {

  protected static final String SYMBOLS_OUT_DIRECTORY = "target" + File.separator + "symbols";

  public static final String MODEL_FILE_EXT = "mfun";

  public static final String SYMBOL_FILE_EXT = "mfsym";

  protected static final String INPUT_FILE_EXT_END = "sym";

  @Override
  public void init() {
    super.init();
    MFSymbolTableUtil.initAndPrepareMill();
  }

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param cmd The command line arguments for configuring the tool.
   */
  @Override
  public void doRun(CommandLine cmd) {
    Options options = initOptions();

    if (cmd.hasOption("interpreter")) {
      runInterpreterMode(cmd);
      return;
    }

    //if -i input is missing: also print help and stop
    if (!cmd.hasOption("i")) {
      printHelp(options);
      return;
    }

    //parse input file, now known to be available
    List<String> inputNames =
        getInputFileNamesFromInputParameter(List.of(cmd.getOptionValues("i")));
    // did we get an input folder?
    if (inputNames.size() == 1 && Paths.get(inputNames.get(0)).toFile().isDirectory()) {
      try (Stream<Path> files = Files.walk(Paths.get(inputNames.get(0)))) {
        inputNames = files
            .filter(path -> path.toString().endsWith("." + MODEL_FILE_EXT)
                || path.toString().endsWith(INPUT_FILE_EXT_END))
            .map(Path::toString)
            .collect(Collectors.toList());
      }
      catch (IOException | UncheckedIOException e) {
        Log.error("0xAC783 Unable to collect MontiFun input files", e);
      }
    }
    // split input into models and symbol files
    List<String> modelInputNames = new ArrayList<>();
    List<String> symbolInputNames = new ArrayList<>();
    for (String inputName : inputNames) {
      if (isLikelySymbolInputFilePath(inputName)) {
        symbolInputNames.add(inputName);
      }
      else {
        modelInputNames.add(inputName);
      }
    }
    if (modelInputNames.isEmpty()) {
      Log.error("0xAC984 -i does not seem to contain any montifun models");
    }

    //parse input files, now known to be available
    List<ASTMFCompilationUnit> inputMontiFuns = new ArrayList<>();
    for (String modelInputName : modelInputNames) {
      ASTMFCompilationUnit ast = parse(modelInputName);
      inputMontiFuns.add(ast);
    }

    // -option pretty print
    if (cmd.hasOption("pp")) {
      if (cmd.getOptionValues("pp") == null || cmd.getOptionValues("pp").length == 0) {
        for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
          System.out.println();
          System.out.println(MontiFunMill.prettyPrint(compilationUnit, true));
        }
      }
      else if (cmd.getOptionValues("pp").length == 1 &&
          isLikelyFolderPath(cmd.getOptionValue("pp"))) {
        for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
          prettyPrintInFolder(compilationUnit, cmd.getOptionValue("pp"));
        }
      }
      else if (cmd.getOptionValues("pp").length == inputMontiFuns.size()
          && cmd.getOptionValues("pp").length == cmd.getOptionValues("pp").length) {
        for (int i = 0; i < inputMontiFuns.size(); i++) {
          prettyPrint(inputMontiFuns.get(i), cmd.getOptionValues("pp")[i]);
        }
      }
      else {
        Log.error(String.format("Received '%s' output files for the prettyprint option. "
                + "Expected that '%s' many output files are specified. "
                + "If output files for the prettyprint option are specified, then the number "
                + "of specified output files must be equal to the number of specified input files, "
                + "or one outputfolder should be specified.",
            cmd.getOptionValues("pp").length, inputMontiFuns.size()));
      }
    }

    //
    // Parsing and pretty printing can be done without a symbol table
    // but executing the following options requires a symbol table
    //

    if (cmd.hasOption("c")
        || cmd.hasOption("s")
        || cmd.hasOption("gen")
    ) {

      // we need the global scope for symbols and cocos
      MCPath symbolPath = new MCPath(Paths.get(""));
      if (cmd.hasOption("p")) {
        symbolPath = new MCPath(Arrays.stream(cmd.getOptionValues("p"))
            .map(Paths::get)
            .collect(Collectors.toList())
        );
      }
      MontiFunMill.globalScope().setSymbolPath(symbolPath);
      MFSymbolTableUtil.addCD4CSymbols();

      //load input symbol tables
      for (String symbolInputName : symbolInputNames) {
        IMontiFunArtifactScope symbolScope = loadSymbols(symbolInputName);
        MontiFunMill.globalScope().addSubScope(symbolScope);
      }

      // Complete symbol table
      for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
        MFSymbolTableUtil.runSymTabGenitor(compilationUnit);
        MFSymbolTableUtil.runSymTabCompleter(compilationUnit);
      }

      // CoCos
      Log.enableFailQuick(false);
      for (ASTMFCompilationUnit compUnit : inputMontiFuns) {
        MontiFunCoCoChecker checker =
            MontiFunCoCoChecker.getCheckerForAllCoCosPhase1();
        checker.checkAll(compUnit);
      }
      for (ASTMFCompilationUnit compUnit : inputMontiFuns) {
        MontiFunCoCoChecker checker =
            MontiFunCoCoChecker.getCheckerForAllCoCosPhase2();
        checker.checkAll(compUnit);
      }
      //to not proceed if CoCos fail
      if (Log.getErrorCount() > 0) {
        Log.warn("encountered errors, will not generate symbol tables/java code/etc.");
        return;
      }
      Log.enableFailQuick(true);

      // store symbols
      if (cmd.hasOption("s")) {
        if (cmd.getOptionValues("s") == null || cmd.getOptionValues("s").length == 0) {
          for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
            storeSymbolsInFolder(compilationUnit, SYMBOLS_OUT_DIRECTORY);
          }
        }
        else if (cmd.getOptionValues("s").length == 1 &&
            isLikelyFolderPath(cmd.getOptionValue("s"))) {
          inputMontiFuns.forEach(
              compUnit -> this.storeSymbolsInFolder(compUnit, cmd.getOptionValue("s")));
        }
        else if (cmd.getOptionValues("s").length == inputMontiFuns.size()
            && cmd.getOptionValues("s").length == cmd.getOptionValues("i").length) {
          for (int i = 0; i < inputMontiFuns.size(); i++) {
            storeSymbols(
                (MontiFunArtifactScope) inputMontiFuns.get(i).getEnclosingScope(),
                cmd.getOptionValues("s")[i]
            );
          }
        }
        else {
          Log.error(String.format("Received '%s' output files for the storesymbols option. "
                  + "Expected that '%s' many output files are specified. "
                  + "If output files for the storesymbols option are specified, then the number "
                  + "of specified output files must be equal to the number of specified input files, "
                  + "or one outputfolder should be specified.",
              cmd.getOptionValues("s").length, inputMontiFuns.size()));
        }
      }

      // -option generate to Java (using CD)
      if (cmd.hasOption("gen")) {
        String path = cmd.getOptionValue("gen", "");
        String templatePath = cmd.getOptionValue("fp", "");
        String handcodedPath = cmd.getOptionValue("hcp", "");

        for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
          generateJava(compilationUnit, path, templatePath, handcodedPath);
        }
      }
    }

  }

  /**
   * CLI-parameter "-i" can take file and directory names
   * this function returns only file names
   *
   * @param inputNames the input given to "-i"
   * @return list of file names
   */
  protected List<String> getInputFileNamesFromInputParameter(List<String> inputNames) {
    List<String> fileNames = new ArrayList<>();
    for (String inputName : inputNames) {
      File input = new File(inputName);
      if (input.isDirectory()) {
        List<File> modelFilesinDir =
            List.of(input.listFiles((dir, name) -> name.endsWith(MODEL_FILE_EXT)));
        for (File modelFile : modelFilesinDir) {
          fileNames.add(modelFile.getAbsolutePath());
        }
      }
      else if (input.isFile()) {
        fileNames.add(input.getAbsolutePath());
      }
      else {
        Log.error("0xAF381 input provided by -i does not seem to be a file or directory: "
            + input.getAbsolutePath());
      }
    }
    return fileNames;
  }

  /**
   * Stores the symbols for ast in the symbol file filename.
   *
   * @param compilationUnit The ast.
   * @param filename        The name of the produced symbol file.
   */
  public void storeSymbols(ASTMFCompilationUnit compilationUnit, String filename) {
    MontiFunSymbols2Json symbols2Json = new MontiFunSymbols2Json();
    String serialized = symbols2Json.serialize(
        (MontiFunArtifactScope) compilationUnit.getEnclosingScope());
    FileReaderWriter.storeInFile(Paths.get(filename), serialized);
  }

  /**
   * Stores the symbols for ast in the specified folder.
   *
   * @param compilationUnit The ast of the SD
   * @param folderPath      The folder to store the symbols in
   */
  protected void storeSymbolsInFolder(ASTMFCompilationUnit compilationUnit, String folderPath) {
    String relativeFilePath = getRelativeFilePath(compilationUnit).concat(".")
        .concat(SYMBOL_FILE_EXT);
    Path filePath = Paths.get(folderPath, relativeFilePath);
    storeSymbols(compilationUnit, filePath.toString());
  }

  /**
   * Loads the symbols from the symbol file filename and returns the symbol table.
   *
   * @param filename Name of the symbol file to load.
   * @return the symbol table
   */
  public IMontiFunArtifactScope loadSymbols(String filename) {
    MontiFunSymbols2Json symbols2Json = new MontiFunSymbols2Json();
    return symbols2Json.load(filename);
  }

  @Override
  public void prettyPrint(ASTMFCompilationUnit ast, String file) {
    String prettyPrintedAST = MontiFunMill.prettyPrint(ast, true);
    print(prettyPrintedAST, file);
  }

  /**
   * Stores the prettyprinted model for ast in the specified folder.
   *
   * @param compilationUnit The ast of the model
   * @param folderPath      The folder to store the symbols in
   */
  protected void prettyPrintInFolder(ASTMFCompilationUnit compilationUnit, String folderPath) {
    String relativeFilePath = getRelativeFilePath(compilationUnit).concat(".")
        .concat(MODEL_FILE_EXT);
    Path filePath = Paths.get(folderPath, relativeFilePath);
    prettyPrint(compilationUnit, filePath.toString());
  }

  /**
   * heuristic to test if the path seems to be a folder path
   *
   * @param pathStr the path to check
   * @return whether we assume it is a path to a folder
   */
  protected boolean isLikelyFolderPath(String pathStr) {
    // if it already exists, check:
    Path path = Paths.get(pathStr);
    File file = path.toFile();
    if (file.exists()) {
      return file.isDirectory();
    }
    // if it does not exist yet,
    // check if the last part ends with an extension
    // note that "a/b/.c" is expected to be a folder,
    // "a/b/c.d" is not expected to be a folder,
    // so we skip the first character
    return !path.getFileName().toString().substring(1).contains(".");
  }

  /**
   * heuristic to test if the path seems to be a file containing a symbol
   *
   * @param pathStr the path to check
   * @return whether we assume it is a path to a folder
   */
  protected boolean isLikelySymbolInputFilePath(String pathStr) {
    // if it already exists, check that it is a file:
    Path path = Paths.get(pathStr);
    File file = path.toFile();
    if (file.exists() && !file.isFile()) {
      return false;
    }
    // check if the last part ends with a corresponding extension
    return path.getFileName().toString().endsWith(INPUT_FILE_EXT_END);
  }

  /**
   * finds the file (without extension) for ast,
   * given its package and name.
   * E.g.: model with qualified name a.b.c
   * "a/b/c"
   *
   * @param compilationUnit The ast of the model
   */
  protected String getRelativeFilePath(ASTMFCompilationUnit compilationUnit) {
    String packagePath = compilationUnit.isPresentMCPackageDeclaration() ?
        compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getQName()
            .replace('.', File.separatorChar) :
        "";
    Path relativeFilePath = Paths.get(packagePath, compilationUnit.getMFArtifact().getName());
    return relativeFilePath.toString();
  }

  public void generateJava(ASTMFCompilationUnit ast,
      String outputDirectory,
      String templatePath,
      String handcodedPath) {

    // switch to CD4C
    CD4CodeMill.reset();
    CD4CodeMill.init();

    GeneratorSetup generatorSetup = MF2CDConverter
        .getDefaultGeneratorSetup(
            new File(outputDirectory),
            Collections.emptyList()
        );
    if (!handcodedPath.isEmpty()) {
      generatorSetup.setHandcodedPath(new MCPath(handcodedPath));
    }
    if (!templatePath.isEmpty()) {
      generatorSetup.setAdditionalTemplatePaths(
          Collections.singletonList((new File(templatePath))));
    }
    if (!outputDirectory.isEmpty()) {
      File targetDir = new File(outputDirectory);
      if (!targetDir.exists()) {
        targetDir.mkdirs();
      }
      generatorSetup.setOutputDirectory(targetDir);
    }

    MF2CDConverter mf2CDConverter = new MF2CDConverter();
    ASTCDCompilationUnit cdCompilationUnit =
        mf2CDConverter.convert(ast, generatorSetup);
    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    cdGenerator.generate(cdCompilationUnit);
  }

  @Override
  public Options addStandardOptions(Options options) {

    // help dialog
    Option help = new Option("h", "Prints this help dialog");
    help.setLongOpt("help");
    options.addOption(help);

    // parse input file
    Option parse = Option.builder("i")
        .longOpt("input")
        .argName("files")
        .hasArgs()
        .desc("Processes the list of input artifacts. " +
            "Argument list is space separated. CoCos are not checked automatically (see -c).")
        .get();
    options.addOption(parse);

    // model paths
    Option path = new Option("p", "Sets the artifact path for imported symbols. "
        + "Directory will be searched recursively for files with the ending "
        + "\".*sym\" (for example \".mfsym\" or \".sym\"). Defaults to the current folder.");
    path.setLongOpt("path");
    path.setArgName("directory");
    path.setOptionalArg(true);
    path.setArgs(1);
    options.addOption(path);

    // pretty print
    Option prettyprint = new Option("pp",
        "Prints the model to stdout or the specified file(s) (optional). "
            + "Multiple files should be separated by spaces and will be used in the same order "
            + "in which the input files (-i option) are provided.");
    prettyprint.setLongOpt("prettyprint");
    prettyprint.setArgName("files");
    prettyprint.setOptionalArg(true);
    prettyprint.setArgs(Option.UNLIMITED_VALUES);
    options.addOption(prettyprint);

    // create and store symboltable
    Option symboltable = Option.builder("s")
        .longOpt("symboltable")
        .optionalArg(true)
        .argName("files")
        .hasArgs()
        .desc("Stores the symbol tables of the input artifacts in the specified files. "
            + "For each input artifact (-i option) please provide one output symbol file "
            + "(using same order in which the input artifacts are provided) to store its symbols in. "
            + "Arguments are separated by spaces. "
            + "If no arguments are given, output is stored to "
            + "'target/symbols/{packageName}/{artifactName}.mfsym'.")
        .get();
    options.addOption(symboltable);
    return options;
  }

  /**
   * Initializes the additional options for the tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addAdditionalOptions(Options options) {

    // check CoCos
    Option cocos = Option.builder("c")
        .longOpt("coco")
        .desc("Checks the CoCos for the input.")
        .get();
    options.addOption(cocos);

    // convert to state pattern CD
    options.addOption(Option.builder("gen")
        .longOpt("generate")
        .argName("dir")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc(
            "Generates java classes to the specified folder (optional)")
        .get());

    Option interpreter = Option.builder()
        .longOpt("interpreter")
        .desc("Starts the interactive interpreter.")
        .get();
    options.addOption(interpreter);

    return options;
  }

  protected void runInterpreterMode(CommandLine cmd) {
    MontiFunRepl repl = new MontiFunRepl();
    if (cmd.hasOption("i")) {
      List<String> inputNames = getInputFileNamesFromInputParameter(
          List.of(cmd.getOptionValues("i"))
      );
      for (String inputName : inputNames) {
        // could be extended to import all at once or circular dependencies
        repl.importModelFile(Paths.get(inputName));
      }
    }
    repl.run();
  }

}
