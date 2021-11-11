/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_qcon extends ASTMF_qconTOP {

  public ASTMF_gcon asASTMF_gcon () {
    return new ASTMF_gconBuilder().setMF_qcon(this).build();
  }

}
