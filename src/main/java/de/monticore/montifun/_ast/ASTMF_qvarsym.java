/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_qvarsym extends ASTMF_qvarsymTOP {

  public String getName() {
    String name = "";
    if (isPresentMF_modid()) {
      name += getMF_modid().getName();
    }
    return name
        + getMF_varsym().getName();
  }

}
