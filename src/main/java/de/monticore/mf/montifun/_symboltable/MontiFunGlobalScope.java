// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;

public class MontiFunGlobalScope extends MontiFunGlobalScopeTOP {
  @Override public MontiFunGlobalScope getRealThis() {
    return this;
  }

  @Override
  public void init() {
    super.init();
    add(MontiFunMill.typeSymbolBuilder()
        .setName(BasicSymbolsMill.NULL)
        .setEnclosingScope(this)
        .setFullName(BasicSymbolsMill.NULL)
        .build());
  }

}
