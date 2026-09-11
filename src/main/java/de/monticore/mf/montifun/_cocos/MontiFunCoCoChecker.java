// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._cocos;

import de.monticore.mf.montifun.cocos.ConstantDeclarationNameAlreadyDefinedInScope;
import de.monticore.mf.montifun.cocos.ConstantDeclarationWithCorrectTypes;
import de.monticore.mf.montifun.cocos.ConstantDependenciesAreAcyclic;
import de.monticore.mf.montifun.cocos.FunctionDeclarationWithCorrectTypes;
import de.monticore.mf.montifun.cocos.FunctionsWithSameNameHaveDifferentTypes;
import de.monticore.ocl.ocl._cocos.ExpressionValidCoCo;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.types.typeparameters.cocos.TypeParameterNoCyclicInheritance;
import de.monticore.types.typeparameters.cocos.TypeParametersHaveUniqueNames;

public class MontiFunCoCoChecker extends MontiFunCoCoCheckerTOP {

  public static MontiFunCoCoChecker getCheckerForAllCoCosPhase1() {
    final MontiFunCoCoChecker checker = new MontiFunCoCoChecker();

    // CoCos that provide target type information to expressions
    // Important: As these CoCos are the ones that assure that
    // correct types are inferred if required,
    // these CoCos must come before CoCos that
    // rely on the types of the same expressions.
    checker.addCoCo(new FunctionDeclarationWithCorrectTypes());
    checker.addCoCo(new ConstantDeclarationWithCorrectTypes());

    return checker;
  }

  // CoCos that may rely on expression types
  public static MontiFunCoCoChecker getCheckerForAllCoCosPhase2() {
    final MontiFunCoCoChecker checker = new MontiFunCoCoChecker();

    // should not matter, but included for security
    checker.addCoCo(new ExpressionValidCoCo());
    checker.addCoCo(new TypeParametersHaveUniqueNames());
    checker.addCoCo(new TypeParameterNoCyclicInheritance());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());
    checker.addCoCo(new ConstantDependenciesAreAcyclic());
    checker.addCoCo(new FunctionsWithSameNameHaveDifferentTypes());
    checker.addCoCo(new ConstantDeclarationNameAlreadyDefinedInScope());

    return checker;
  }
}
