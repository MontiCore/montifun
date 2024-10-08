// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.montifun._cocos.MontiFunCoCoChecker;
import de.monticore.ocl.ocl._cocos.ExpressionValidCoCo;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.types.typeparameters.cocos.TypeParameterNoCyclicInheritance;
import de.monticore.types.typeparameters.cocos.TypeParametersHaveUniqueNames;

public class MontiFunCoCos {

  /**
   * @return an object which may be used to check all CoCos
   */
  public static MontiFunCoCoChecker getCheckerForAllCoCos() {
    final MontiFunCoCoChecker checker = new MontiFunCoCoChecker();

    // CoCos that provide target type information to expressions
    // Important: As these CoCos are the ones that assure that
    // correct types are inferred if required,
    // these CoCos must come before CoCos that
    // rely on the types of the same expressions.
    // In this case, this requirement holds due to the AST-structure.
    checker.addCoCo(new FunctionDeclarationWithCorrectTypes());
    checker.addCoCo(new ConstantDeclarationWithCorrectTypes());

    // CoCos that may rely on expression types
    checker.addCoCo(new TypeParametersHaveUniqueNames());
    checker.addCoCo(new TypeParameterNoCyclicInheritance());
    checker.addCoCo(new ExpressionValidCoCo());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());

    return checker;
  }
}
