// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.ocl.util.library.ListType;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;

/**
 * Adds symbols for MontiFun lists
 */
public class MFListType extends ListType {

  @Override
  public void addListType() {
    typeVarSymbol = MontiFunMill.typeVarSymbolBuilder().setName("X").build();

    SymTypeOfGenerics superType = SymTypeExpressionFactory
        .createGenerics(SymTypeExpressionFactory.createTypeConstant("Collection").getTypeInfo(),
            SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));

    listSymbol = MontiFunMill.typeSymbolBuilder()
        .setName("List")
        .setEnclosingScope(MontiFunMill.globalScope())
        .setSpannedScope(MontiFunMill.scope())
        .addSuperTypes(superType)
        .build();
    listSymbol.getSpannedScope().setName("List");
    listSymbol.addTypeVarSymbol(typeVarSymbol);

    MontiFunMill.globalScope().add(listSymbol);
    MontiFunMill.globalScope().addSubScope(listSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return MontiFunMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(listSymbol.getSpannedScope())
        .setSpannedScope(MontiFunMill.scope())
        .build();
  }

}
