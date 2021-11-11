/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._symboltable;

import de.monticore.montifun._ast.ASTMFFixityBuilder;
import de.monticore.montifun._ast.ASTMF_integerBuilder;
import de.monticore.montifun._ast.ASTOP_ASSOCIATIVITY;

public class MFFunSymbol extends MFFunSymbolTOP {

  public MFFunSymbol(String name){
    super(name);
  }

  @Override
  public de.monticore.montifun._ast.ASTMFFixity getFixity() {
    if (isPresentFixity()) {
      return super.getFixity();
    }
    return new ASTMFFixityBuilder()
        .setAssociativity(ASTOP_ASSOCIATIVITY.LEFT)
        .setPrecedence(new ASTMF_integerBuilder()
            .setMFInteger(9)
            .build()
        )
        .build();
  }

}
