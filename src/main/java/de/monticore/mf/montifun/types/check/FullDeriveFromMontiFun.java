// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.types.check;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.types.check.DeriveSymTypeOfCommonExpressions;
import de.monticore.ocl.types.check.DeriveSymTypeOfOCLExpressions;
import de.monticore.ocl.types.check.DeriveSymTypeOfOptionalOperators;
import de.monticore.ocl.types.check.DeriveSymTypeOfSetExpressions;
import de.monticore.types.check.AbstractDerive;
import de.monticore.types.check.AbstractSynthesize;
import de.monticore.types.check.DeriveSymTypeOfExpression;
import de.monticore.types.check.DeriveSymTypeOfLambdaExpressions;
import de.monticore.types.check.DeriveSymTypeOfLiterals;
import de.monticore.types.check.DeriveSymTypeOfMCCommonLiterals;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCCollectionTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCFunctionTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;

public class FullDeriveFromMontiFun extends AbstractDerive {

  public FullDeriveFromMontiFun() {
    this(MontiFunMill.traverser());
  }

  public FullDeriveFromMontiFun(MontiFunTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(MontiFunTraverser traverser) {
    AbstractSynthesize synthesizer = new FullSynthesizeFromMontiFun();

    DeriveSymTypeOfExpression deriveSymTypeOfExpression = new DeriveSymTypeOfExpression();
    deriveSymTypeOfExpression.setTypeCheckResult(typeCheckResult);
    traverser.add4ExpressionsBasis(deriveSymTypeOfExpression);
    traverser.setExpressionsBasisHandler(deriveSymTypeOfExpression);

    DeriveSymTypeOfCommonExpressions deriveSymTypeOfCommonExpressions = new DeriveSymTypeOfCommonExpressions();
    deriveSymTypeOfCommonExpressions.setTypeCheckResult(typeCheckResult);
    traverser.add4CommonExpressions(deriveSymTypeOfCommonExpressions);
    traverser.setCommonExpressionsHandler(deriveSymTypeOfCommonExpressions);

    DeriveSymTypeOfSetExpressions deriveSymTypeOfSetExpressions = new DeriveSymTypeOfSetExpressions();
    deriveSymTypeOfSetExpressions.setTypeCheckResult(typeCheckResult);
    traverser.add4SetExpressions(deriveSymTypeOfSetExpressions);
    traverser.setSetExpressionsHandler(deriveSymTypeOfSetExpressions);

    DeriveSymTypeOfOCLExpressions deriveSymTypeOfOCLExpressions = new DeriveSymTypeOfOCLExpressions();
    deriveSymTypeOfOCLExpressions.setTypeCheckResult(typeCheckResult);
    traverser.setOCLExpressionsHandler(deriveSymTypeOfOCLExpressions);

    DeriveSymTypeOfLambdaExpressions deriveSymTypeOfLambdaExpressions = new DeriveSymTypeOfLambdaExpressions();
    deriveSymTypeOfLambdaExpressions.setSynthesize(synthesizer);
    deriveSymTypeOfLambdaExpressions.setTypeCheckResult(typeCheckResult);
    traverser.setLambdaExpressionsHandler(deriveSymTypeOfLambdaExpressions);

    DeriveSymTypeOfLiterals deriveSymTypeOfLiterals = new DeriveSymTypeOfLiterals();
    deriveSymTypeOfLiterals.setTypeCheckResult(typeCheckResult);
    traverser.add4MCLiteralsBasis(deriveSymTypeOfLiterals);

    DeriveSymTypeOfMCCommonLiterals deriveSymTypeOfMCCommonLiterals = new DeriveSymTypeOfMCCommonLiterals();
    deriveSymTypeOfMCCommonLiterals.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCommonLiterals(deriveSymTypeOfMCCommonLiterals);

    DeriveSymTypeOfOptionalOperators deriveSymTypeOfOptionalOperators = new DeriveSymTypeOfOptionalOperators();
    deriveSymTypeOfOptionalOperators.setTypeCheckResult(typeCheckResult);
    traverser.setOptionalOperatorsHandler(deriveSymTypeOfOptionalOperators);

    SynthesizeSymTypeFromMCSimpleGenericTypes synthesizeSymTypeFromMCSimpleGenericTypes = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    synthesizeSymTypeFromMCSimpleGenericTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCSimpleGenericTypes(synthesizeSymTypeFromMCSimpleGenericTypes);
    traverser.setMCSimpleGenericTypesHandler(synthesizeSymTypeFromMCSimpleGenericTypes);

    SynthesizeSymTypeFromMCBasicTypes synthesizeSymTypeFromMCBasicTypes = new SynthesizeSymTypeFromMCBasicTypes();
    synthesizeSymTypeFromMCBasicTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCBasicTypes(synthesizeSymTypeFromMCBasicTypes);
    traverser.setMCBasicTypesHandler(synthesizeSymTypeFromMCBasicTypes);

    SynthesizeSymTypeFromMCFunctionTypes synthesizeSymTypeFromMCFunctionTypes = new SynthesizeSymTypeFromMCFunctionTypes();
    synthesizeSymTypeFromMCFunctionTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCFunctionTypes(synthesizeSymTypeFromMCFunctionTypes);
    traverser.setMCFunctionTypesHandler(synthesizeSymTypeFromMCFunctionTypes);

    SynthesizeSymTypeFromMCCollectionTypes synthesizeSymTypeFromMCCollectionTypes = new SynthesizeSymTypeFromMCCollectionTypes();
    synthesizeSymTypeFromMCCollectionTypes.setTypeCheckResult(typeCheckResult);
    traverser.add4MCCollectionTypes(synthesizeSymTypeFromMCCollectionTypes);
    traverser.setMCCollectionTypesHandler(synthesizeSymTypeFromMCCollectionTypes);
  }

}
