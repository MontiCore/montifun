/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFMultiFunLhs extends ASTMFMultiFunLhsTOP {

  @Override
  public String getFunctionName() {
    return getMF_funlhs().getFunctionName();
  }

}
