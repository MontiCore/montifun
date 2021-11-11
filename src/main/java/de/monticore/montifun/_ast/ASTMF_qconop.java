/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_qconop extends ASTMF_qconopTOP {

  public String getName() {
    if (isPresentMF_qconid()) {
      return getMF_qconid().getName();
    }
    if (isPresentMF_qconsym()) {
      return getMF_qconsym().getName();
    }
    Log.error("0xMF016 Unexpected ASTMF_qconop");
    return "ERROR NAME ASTMF_qconop";
  }

  /**
   * converts this to an ASTMF_qcon
   *
   * @return the converted value
   */
  public ASTMF_qcon asASTMF_qcon() {
    ASTMF_qconBuilder builder = new ASTMF_qconBuilder();
    if(isPresentMF_qconid()){
      builder.setMF_qconid(getMF_qconid());
    }
    if (isPresentMF_qconsym()) {
      builder.setMF_qconsym(getMF_qconsym());
    }
    return builder.build();
  }

}
