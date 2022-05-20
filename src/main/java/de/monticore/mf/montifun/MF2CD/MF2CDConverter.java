// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class MF2CDConverter {

  public ASTCDCompilationUnit convert(ASTMFCompilationUnit mfCompilationUnit,
      GlobalExtensionManagement glex) {

    // switch to CD4Code
    CD4CodeMill.reset();
    CD4CodeMill.init();
    BasicSymbolsMill.initializePrimitives();

    MontiFunTraverser traverser = MontiFunMill.traverser();
    MF2CDVisitor mf2CDVisitor = new MF2CDVisitor();
    traverser.add4MontiFun(mf2CDVisitor);
    mfCompilationUnit.accept(traverser);

    return mf2CDVisitor.getCdCompilationUnit();
  }

}
