// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._cocos;

import de.monticore.mf.montifun.types.check.FullDeriveFromMontiFun;
import de.monticore.mf.montifun.types.check.FullSynthesizeFromMontiFun;
import de.monticore.ocl.ocl._cocos.ExpressionValidCoCo;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;

public class MontiFunCoCos {

  /**
   * @return an object which may be used to check all CoCos
   */
  public static MontiFunCoCoChecker getCheckerForAllCoCos() {
    final MontiFunCoCoChecker checker = new MontiFunCoCoChecker();
    final IDerive derive = new FullDeriveFromMontiFun();
    final ISynthesize synthesize = new FullSynthesizeFromMontiFun();

    checker.addCoCo(new FunctionDeclarationWithCorrectTypes(derive, synthesize));

    checker.addCoCo(new ExpressionValidCoCo());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());

    return checker;
  }
}
