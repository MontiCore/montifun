/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_qvarop extends ASTMF_qvaropTOP {

  public String getName() {
    if (isPresentMF_qvarid()) {
      return getMF_qvarid().getName();
    }
    if (isPresentMF_qvarsym()) {
      return getMF_qvarsym().getName();
    }
    Log.error("0xMF018 Unexpected ASTMF_qvarop");
    return "ERROR NAME ASTMF_qvarop";
  }

  public ASTMF_qvar asASTMF_qvar() {
    ASTMF_qvarBuilder builder = new ASTMF_qvarBuilder();
    if(isPresentMF_qvarid()){
      builder.setMF_qvarid(getMF_qvarid());
    }
    if (isPresentMF_qvarsym()) {
      builder.setMF_qvarsym(getMF_qvarsym());
    }
    return builder.build();
  }

}
