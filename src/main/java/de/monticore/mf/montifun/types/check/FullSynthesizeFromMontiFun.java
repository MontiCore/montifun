// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.types.check;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.types.check.AbstractSynthesize;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCCollectionTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCFunctionTypes;
import de.monticore.types.check.SynthesizeSymTypeFromMCSimpleGenericTypes;

/**
 * use {@link de.monticore.mf.montifun.types3.MontiFunTypeCheck3}
 */
@Deprecated
public class FullSynthesizeFromMontiFun extends AbstractSynthesize {

  public FullSynthesizeFromMontiFun() {
    this(MontiFunMill.traverser());
  }

  public FullSynthesizeFromMontiFun(MontiFunTraverser traverser) {
    super(traverser);
    init(traverser);
  }

  public void init(MontiFunTraverser traverser) {
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
