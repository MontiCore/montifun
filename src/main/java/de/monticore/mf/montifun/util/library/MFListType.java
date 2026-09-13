// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.ocl.util.library.ListType;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;

/**
 * Adds symbols for MontiFun lists
 */
public class MFListType extends ListType {

  @Override
  public void addListType() {
    typeVarSymbol = BasicSymbolsMill.typeVarSymbolBuilder().setName("X").build();

    SymTypeOfGenerics superType = SymTypeExpressionFactory
        .createGenerics(SymTypeExpressionFactory.createPrimitive("Collection").getTypeInfo(),
            SymTypeExpressionFactory.createTypeVariable(typeVarSymbol));

    listSymbol = BasicSymbolsMill.typeSymbolBuilder()
        .setName("List")
        .setEnclosingScope(BasicSymbolsMill.globalScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .addSuperTypes(superType)
        .build();
    listSymbol.getSpannedScope().setName("List");
    listSymbol.addTypeVarSymbol(typeVarSymbol);

    BasicSymbolsMill.globalScope().add(listSymbol);
    BasicSymbolsMill.globalScope().addSubScope(listSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return BasicSymbolsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(listSymbol.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
  }

}
