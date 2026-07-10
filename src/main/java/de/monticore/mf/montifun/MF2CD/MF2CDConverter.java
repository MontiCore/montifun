// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd.codegen.CdUtilsPrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;

import java.io.File;
import java.util.List;

public class MF2CDConverter {

  public ASTCDCompilationUnit convert(
      ASTMFCompilationUnit mfCompilationUnit,
      GeneratorSetup generatorSetup) {

    // switch to CD4Code
    CD4CodeMill.reset();
    CD4CodeMill.init();

    MontiFunTraverser traverser = MontiFunMill.traverser();
    MF2CDVisitor mf2CDVisitor = new MF2CDVisitor(generatorSetup);
    traverser.add4MontiFun(mf2CDVisitor);
    mfCompilationUnit.accept(traverser);

    return mf2CDVisitor.getCdCompilationUnit();
  }

  public static GeneratorSetup getDefaultGeneratorSetup(
      File outputPath,
      List<File> templatePaths
  ) {
    GlobalExtensionManagement glex = new GlobalExtensionManagement();
    glex.setGlobalValue("cdPrinter", new CdUtilsPrinter());
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setGlex(glex);
    generatorSetup.setOutputDirectory(outputPath);
    generatorSetup.setTracing(false);
    generatorSetup.setAdditionalTemplatePaths(templatePaths);
    return generatorSetup;
  }

}
