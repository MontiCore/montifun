/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFQSmallName extends ASTMFQSmallNameTOP {

  public String getName() {
    String name = "";
    if (isPresentMF_modid()) {
      name += getMF_modid().getName();
    }
    return name
        + getMFSmallName();
  }
}
