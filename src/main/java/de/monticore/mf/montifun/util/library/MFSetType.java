// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.ocl.util.library.SetType;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;

/**
 * Adds symbols for MontiFun sets
 */
public class MFSetType extends SetType {

  @Override
  public void addSetType() {
    typeVarSymbol = MontiFunMill.typeVarSymbolBuilder().setName("X").build();

    SymTypeOfGenerics superType = SymTypeExpressionFactory
        .createGenerics(SymTypeExpressionFactory.createTypeConstant("Collection").getTypeInfo(),
            SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));

    setSymbol = MontiFunMill.typeSymbolBuilder()
        .setName("Set")
        .setEnclosingScope(MontiFunMill.globalScope())
        .setSpannedScope(MontiFunMill.scope())
        .addSuperTypes(superType)
        .build();
    setSymbol.getSpannedScope().setName("Set");
    setSymbol.addTypeVarSymbol(typeVarSymbol);

    MontiFunMill.globalScope().add(setSymbol);
    MontiFunMill.globalScope().addSubScope(setSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return MontiFunMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(setSymbol.getSpannedScope())
        .setSpannedScope(MontiFunMill.scope())
        .build();
  }

}
