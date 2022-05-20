// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util.library;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.ocl.util.library.CollectionType;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;

/**
 * Adds symbols for MontiFun collections
 */
public class MFCollectionType extends CollectionType {

  @Override
  public void addCollectionType() {
    collectionSymbol = MontiFunMill.typeSymbolBuilder()
        .setName("Collection")
        .setEnclosingScope(MontiFunMill.globalScope())
        .setSpannedScope(MontiFunMill.scope())
        .build();
    collectionSymbol.getSpannedScope().setName("Collection");
    typeVarSymbol = MontiFunMill.typeVarSymbolBuilder().setName("X").build();
    collectionSymbol.addTypeVarSymbol(typeVarSymbol);

    MontiFunMill.globalScope().add(collectionSymbol);
    MontiFunMill.globalScope().addSubScope(collectionSymbol.getSpannedScope());
  }

  @Override
  protected FunctionSymbol createMethod(String name) {
    return MontiFunMill.functionSymbolBuilder()
        .setName(name)
        .setEnclosingScope(collectionSymbol.getSpannedScope())
        .setSpannedScope(MontiFunMill.scope())
        .build();
  }

}
