/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun;

import de.monticore.cd.codegen.CDGenerator;
import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun.MF2CD.MF2CDConverter;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.mf.montifun._cocos.MontiFunCoCos;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun.prettyprint.MontiFunFullPrettyPrinter;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MontiFunTool extends MontiFunToolTOP {

  protected static final String SYMBOLS_OUT_DIRECTORY = "target" + File.separator + "symbols";

  public static final String MODEL_FILE_EXT = "mfun";

  public static final String SYMBOL_FILE_EXT = "mfsym";

  @Override
  public void init() {
    super.init();
    MFSymbolTableUtil.prepareMill();
  }

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param args The input parameters for configuring the tool.
   */
  @Override
  public void run(String[] args) {
    init();

    Options options = initOptions();
    try {
      //create CLI Parser and parse input options from commandline
      CommandLineParser cliparser = new DefaultParser();
      CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }

      // -option developer logging
      if (cmd.hasOption("d")) {
        Log.initDEBUG();
      }
      else {
        Log.init();
      }

      //if -i input is missing: also print help and stop
      if (!cmd.hasOption("i")) {
        printHelp(options);
        return;
      }

      //parse input file, now known to be available
      List<ASTMFCompilationUnit> inputMontiFuns = new ArrayList<>();
      for (String inputFileName : cmd.getOptionValues("i")) {
        ASTMFCompilationUnit ast = parse(inputFileName);
        inputMontiFuns.add(ast);
      }

      // -option pretty print
      if (cmd.hasOption("pp")) {
        int ppArgs = cmd.getOptionValues("pp") == null ? 0 : cmd.getOptionValues("pp").length;
        int iArgs = cmd.getOptionValues("i") == null ? 0 : cmd.getOptionValues("i").length;
        if (ppArgs != 0 && ppArgs != iArgs) {
          Log.error("0xOCL31 Number of arguments of -pp (which is " + ppArgs
              + ") must match number of arguments of -i (which is " + iArgs + "). "
              + "Or provide no arguments to print to stdout.");
        }

        String[] paths = cmd.getOptionValues("pp");
        int i = 0;
        for (ASTMFCompilationUnit compUnit : inputMontiFuns) {
          String currentPath = "";
          if (cmd.getOptionValues("pp") != null && cmd.getOptionValues("pp").length != 0) {
            currentPath = paths[i];
            i++;
          }
          prettyPrint(compUnit, currentPath);
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

        if (cmd.hasOption("cd4c")) {
          MFSymbolTableUtil.addCD4CSymbols();
        }

        // Complete symbol table
        for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
          MFSymbolTableUtil.runSymTabGenitor(compilationUnit);
          MFSymbolTableUtil.runSymTabCompleter(compilationUnit);
        }

        // CoCos
        Log.enableFailQuick(false);
        for (ASTMFCompilationUnit compUnit : inputMontiFuns) {
          MontiFunCoCoChecker checker = MontiFunCoCos.getCheckerForAllCoCos();
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
            inputMontiFuns.forEach(this::storeSymbols);
          }
          else if (cmd.getOptionValues("s").length != inputMontiFuns.size()) {
            Log.error(String.format("Received '%s' output files for the storesymbols option. "
                    + "Expected that '%s' many output files are specified. "
                    + "If output files for the storesymbols option are specified, then the number "
                    + " of specified output files must be equal to the number of specified input files.",
                cmd.getOptionValues("s").length, inputMontiFuns.size()));
          }
          else {
            for (int i = 0; i < inputMontiFuns.size(); i++) {
              storeSymbols(
                  (MontiFunArtifactScope) inputMontiFuns.get(i).getEnclosingScope(),
                  cmd.getOptionValues("s")[i]
              );
            }
          }
        }

        // -option generate to CD
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
    catch (ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C73 Could not process CLI parameters: " + e.getMessage());
    }
  }

  public IMontiFunArtifactScope createSymbolTable(ASTMFCompilationUnit compilationUnit) {
    MFSymbolTableUtil.runSymTabGenitor(compilationUnit);
    return (IMontiFunArtifactScope) compilationUnit.getEnclosingScope();
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
   * Stores the symbols for ast in the symbol file default location.
   *
   * @param compilationUnit The ast of the SD.
   */
  protected void storeSymbols(ASTMFCompilationUnit compilationUnit) {
    String fileName = compilationUnit.getMFArtifact().getName().concat(".").concat(SYMBOL_FILE_EXT);
    String packagePath = compilationUnit.isPresentMCPackageDeclaration() ?
        compilationUnit.getMCPackageDeclaration().getMCQualifiedName().getQName()
            .replace('.', '/') :
        "";
    Path filePath = Paths.get(SYMBOLS_OUT_DIRECTORY, packagePath, fileName);
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
    String prettyPrintedAST = new MontiFunFullPrettyPrinter().prettyprint(ast);
    print(prettyPrintedAST, file);
  }

  public void generateJava(ASTMFCompilationUnit ast,
      String outputDirectory,
      String templatePath,
      String handcodedPath) {

    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(new File(outputDirectory));
    generatorSetup.setTracing(false);
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

    CD4C.init(generatorSetup);

    CDGenerator cdGenerator = new CDGenerator(generatorSetup);
    MF2CDConverter mf2CDConverter = new MF2CDConverter();
    ASTCDCompilationUnit cdCompilationUnit = mf2CDConverter.convert(ast, generatorSetup.getGlex());
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
        .build();
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
        .build();
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
        .build();
    options.addOption(cocos);

    // convert to state pattern CD
    options.addOption(Option.builder("gen")
        .longOpt("generate")
        .argName("dir")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc(
            "Prints the montifun model to stdout or the generated java classes to the specified folder (optional)")
        .build());

    // developer level logging
    Option cd4c = new Option("cd4c",
        "Load symbol kinds from CD4C. Shortcut for loading CDTypeSymbol as TypeSymbol, "
            + "CDMethodSignatureSymbol as FunctionSymbol, and FieldSymbol as VariableSymbol. "
    );
    cd4c.setLongOpt("cd4code");
    options.addOption(cd4c);

    return options;
  }

}
