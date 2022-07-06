// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._cocos;

import de.monticore.ocl.ocl._cocos.ExpressionValidCoCo;
import de.monticore.ocl.oclexpressions._cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.setexpressions._cocos.SetComprehensionHasGenerator;
import de.monticore.ocl.types.check.OCLDeriver;
import de.monticore.ocl.types.check.OCLSynthesizer;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;

public class MontiFunCoCos {

  /**
   * @return an object which may be used to check all CoCos
   */
  public static MontiFunCoCoChecker getCheckerForAllCoCos() {
    final MontiFunCoCoChecker checker = new MontiFunCoCoChecker();
    final IDerive derive = new OCLDeriver();
    final ISynthesize synthesize = new OCLSynthesizer();

    checker.addCoCo(new FunctionDeclarationWithCorrectTypes(derive, synthesize));

    //todo remove cast as soon as the change of ExpressionValidCoCo is online
    checker.addCoCo(new ExpressionValidCoCo((OCLDeriver) derive));
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());

    return checker;
  }
}
