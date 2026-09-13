// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.ocl.util.library.CollectionType;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;

/**
 * Adds symbols for MontiFun collections
 */
public class MFCollectionType extends CollectionType {

  @Override
  public void addCollectionType() {
    collectionSymbol = BasicSymbolsMill.typeSymbolBuilder()
        .setName("Collection")
        .setEnclosingScope(BasicSymbolsMill.globalScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
    collectionSymbol.getSpannedScope().setName("Collection");
    typeVarSymbol = BasicSymbolsMill.typeVarSymbolBuilder().setName("X").build();
    collectionSymbol.addTypeVarSymbol(typeVarSymbol);

    BasicSymbolsMill.globalScope().add(collectionSymbol);
    BasicSymbolsMill.globalScope().addSubScope(collectionSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return BasicSymbolsMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(collectionSymbol.getSpannedScope())
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();
  }

}
