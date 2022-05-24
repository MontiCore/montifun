/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunArtifactScope;
import de.monticore.mf.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.mf.montifun.prettyprint.MontiFunFullPrettyPrinter;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MontiFunTool extends MontiFunToolTOP {

  protected static final String SYMBOLS_OUT_DIRECTORY = "target" + File.separator + "symbols";

  @Override
  public void init() {
    super.init();
    MFSymbolTableUtil.prepareMill();
  }

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param args The input parameters for configuring the JSON tool.
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

      // we need the global scope for symbols and cocos
      MCPath symbolPath = new MCPath(Paths.get(""));
      if (cmd.hasOption("p")) {
        symbolPath = new MCPath(Arrays.stream(cmd.getOptionValues("p"))
            .map(x -> Paths.get(x))
            .collect(Collectors.toList())
        );
      }

      //
      // Parsing and pretty printing can be done without a symbol table
      // but executing the following options requires a symbol table
      //

      // do: add cocos and only export symbols if cocos successful

      // store symbols
      if (cmd.hasOption("s")) {
        // Complete symbol table
        for (ASTMFCompilationUnit compilationUnit : inputMontiFuns) {
          MFSymbolTableUtil.runSymTabGenitor(compilationUnit);
          MFSymbolTableUtil.runSymTabCompleter(compilationUnit);
        }
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
   * @param compilationUnit The ast of the SD.
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
    String fileName = compilationUnit.getMFArtifact().getName().concat(".mfsym");
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

}
