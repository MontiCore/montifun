/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun;

import de.monticore.montifun._ast.ASTMF_module;
import de.monticore.montifun._symboltable.IMontiFunArtifactScope;
import de.monticore.montifun._symboltable.MontiFunSymbols2Json;
import de.monticore.montifun.transform2core.*;
import de.monticore.montifun.prettyprinter.CorePrinter;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;


public class MontiFunCLI extends MontiFunCLITOP {

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param args The input parameters for configuring the JSON tool.
   */
  @Override
  public void run(String[] args) {

    init();
    org.apache.commons.cli.Options options = initOptions();
    try {
      //create CLI Parser and parse input options from commandline
      org.apache.commons.cli.CommandLineParser cliparser = new org.apache.commons.cli.DefaultParser();
      org.apache.commons.cli.CommandLine cmd = cliparser.parse(options, args);

      //help: when --help
      if (cmd.hasOption("h")) {
        printHelp(options);
        //do not continue, when help is printed.
        return;
      }

      //if -i input is missing: also print help and stop
      if (!cmd.hasOption("i")) {
        printHelp(options);
        return;
      }

      //parse input file, now known to be available
      ASTMF_module ast = parse(cmd.getOptionValue("i"));
      Transform2Core.postParse(ast);


      //String packageQName = montiFunAST.getMCPackageDeclaration().getMCQualifiedName().getQName();

      //String packagePath = Names.getPathFromQualifiedName(packageQName);


/*
      ASTModule importedASTs[] = spesMLFunctionAST.getMCImportStatementList().stream()
          .map(importStatement -> {
               importStatement.getQName()
              }
          ).toArray()
*/
      //create symbol table
      IMontiFunArtifactScope symtab = createSymbolTable(ast);
      Transform2Core.postSymTabCreation(ast);
      //names are still missing
      //symtab.setPackageName(spesMLFunctionAST.getMCPackageDeclaration().getMCQualifiedName().getQName());
      //symtab.setName(spesMLFunctionAST.getName());

      //pretty print symbol table
      if (cmd.hasOption("s")) {

        String pathStr = cmd.getOptionValue("s", StringUtils.EMPTY);
        storeSymbols(symtab, pathStr);
      }

      //pretty print (core)
      if (cmd.hasOption("pp")) {
        prettyPrint(ast, cmd.getOptionValue("pp", StringUtils.EMPTY));
      }


    } catch (org.apache.commons.cli.ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C01x43786 Could not process SpesMLFunctionsCLI parameters: " + e.getMessage());
    }
  }

  @Override
  public void init() {
    super.init(); // new mill

  }

  @Override
  public org.apache.commons.cli.Options addAdditionalOptions(org.apache.commons.cli.Options options) {
    options.addOption(org.apache.commons.cli.Option.builder("j")
        .longOpt("java")
        .argName("path")
        .hasArg()
        .desc("Constructs the Java class of the given artifact.")
        .build());

    return options;
  }

  @Override
  public void storeSymbols(IMontiFunArtifactScope scope, String pathOut) {
    //"Names" doesn't seem to have an option to create a filename...
    Path filePath = Paths.get(pathOut)
        .resolve(Names.getPathFromPackage(scope.getPackageName()))
        .resolve(Names.getFileName(scope.getName(), "mfunlsym"));
    //changing global state...
    de.monticore.symboltable.serialization.JsonPrinter.disableIndentation();
    new MontiFunSymbols2Json().store(scope, filePath.toString());
  }

  @Override
  public void prettyPrint(ASTMF_module ast, String file) {
    String prettyPrintedAST = CorePrinter.toString(ast);
    print(prettyPrintedAST, file);
  }


}
