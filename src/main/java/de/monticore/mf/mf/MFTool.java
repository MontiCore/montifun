/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.mf;

//import de.monticore.montifun._ast.ASTMF_module;
//import de.monticore.montifun._symboltable.IMontiFunArtifactScope;
//import de.monticore.montifun._symboltable.MontiFunSymbols2Json;
//import de.monticore.montifun.transform2core.*;
//import de.monticore.montifun.montifun.prettyprinter.CorePrinter;

import de.monticore.mf.mf._ast.ASTMFCompilationUnit;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;

public class MFTool extends MFToolTOP {

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
      List<ASTMFCompilationUnit> inputMFs = new ArrayList<>();
      for (String inputFileName : cmd.getOptionValues("i")) {
        ASTMFCompilationUnit ast = parse(inputFileName);
        inputMFs.add(ast);
      }

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
      //IMontiFunArtifactScope symtab = createSymbolTable(ast);
      //Transform2Core.postSymTabCreation(ast);
      //names are still missing
      //symtab.setPackageName(spesMLFunctionAST.getMCPackageDeclaration().getMCQualifiedName().getQName());
      //symtab.setName(spesMLFunctionAST.getName());

      //pretty print symbol table
      //if (cmd.hasOption("s")) {

      //  String pathStr = cmd.getOptionValue("s", StringUtils.EMPTY);
      //  storeSymbols(symtab, pathStr);
      //}

      //pretty print (core)
      //if (cmd.hasOption("pp")) {
      //  prettyPrint(ast, cmd.getOptionValue("pp", StringUtils.EMPTY));
      //}

    }
    catch (org.apache.commons.cli.ParseException e) {
      // e.getMessage displays the incorrect input-parameters
      Log.error("0xA5C01x43786 Could not process CLI parameters: " + e.getMessage());
    }
  }

  //@Override
  //public org.apache.commons.cli.Options addAdditionalOptions(org.apache.commons.cli.Options options) {
  //  options.addOption(org.apache.commons.cli.Option.builder("j")
  //      .longOpt("java")
  //      .argName("path")
  //      .hasArg()
  //      .desc("Constructs the Java class of the given artifact.")
  //      .build());

  //  return options;
  //}

  //@Override
  //public void storeSymbols(IMontiFunArtifactScope scope, String pathOut) {
  //  //"Names" doesn't seem to have an option to create a filename...
  //  Path filePath = Paths.get(pathOut)
  //      .resolve(Names.getPathFromPackage(scope.getPackageName()))
  //      .resolve(Names.getFileName(scope.getName(), "mfunlsym"));
  //  //changing global state...
  //  de.monticore.symboltable.serialization.JsonPrinter.disableIndentation();
  //  new MontiFunSymbols2Json().store(scope, filePath.toString());
  //}

  //@Override
  //public void prettyPrint(ASTMFCompilationUnit ast, String file) {
  //String prettyPrintedAST = CorePrinter.toString(ast);
  //print(prettyPrintedAST, file);
  //}

}
