/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFPrefixFunLhs extends ASTMFPrefixFunLhsTOP {

  @Override
  public String getFunctionName() {
    return getMF_var().getName();
  }

}
