// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf.mf2CD;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.mf.mf.MontiFunMill;
import de.monticore.mf.mf._ast.ASTMFCompilationUnit;
import de.monticore.mf.mf._visitor.MFTraverser;

public class MF2CDConverter {

  public ASTCDCompilationUnit convert(ASTMFCompilationUnit mfCompilationUnit,
      GlobalExtensionManagement glex) {
    CD4CodeMill.init();

    MontiFunTraverser traverser = MontiFunMill.traverser();
    MF2CDVisitor mf2CDVisitor = new MF2CDVisitor();
    traverser.add4MontiFun(mf2CDVisitor);
    mfCompilationUnit.accept(traverser);

    return mf2CDVisitor.getCdCompilationUnit();
    //switch to CD4Code language?
    //CD4CodeMill.init();

  }
}
