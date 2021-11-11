/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMFQLargeName extends ASTMFQLargeNameTOP {

  public String getName() {
    String name = "";
    if (isPresentMF_modid()) {
      name += getMF_modid().getName();
    }
    return name
        + getMFLargeName();
  }
}
