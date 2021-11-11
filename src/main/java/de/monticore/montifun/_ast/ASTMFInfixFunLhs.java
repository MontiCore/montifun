/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFInfixFunLhs extends ASTMFInfixFunLhsTOP {

  @Override
  public String getFunctionName() {
    return getMF_varop().getName();
  }

}
