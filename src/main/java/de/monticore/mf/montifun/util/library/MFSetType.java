// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.ocl.util.library.SetType;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;

/**
 * Adds symbols for MontiFun sets
 */
public class MFSetType extends SetType {

  @Override
  public void addSetType() {
    typeVarSymbol = BasicSymbolsMill.typeVarSymbolBuilder().setName("X").build();

    SymTypeOfGenerics superType = SymTypeExpressionFactory
        .createGenerics(SymTypeExpressionFactory.createPrimitive("Collection").getTypeInfo(),
            SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));

    setSymbol = BasicSymbolsMill.typeSymbolBuilder()
        .setName("Set")
        .setEnclosingScope(BasicSymbolsMill.globalScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .addSuperTypes(superType)
        .build();
    setSymbol.getSpannedScope().setName("Set");
    setSymbol.addTypeVarSymbol(typeVarSymbol);

    BasicSymbolsMill.globalScope().add(setSymbol);
    BasicSymbolsMill.globalScope().addSubScope(setSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return BasicSymbolsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(setSymbol.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
  }

}
