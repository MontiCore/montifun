/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_op extends ASTMF_opTOP {

  public String getName() {
    if (isPresentMF_varop()) {
      return getMF_varop().getName();
    }
    if (isPresentMF_conop()) {
      return getMF_conop().getName();
    }
    Log.error("0xFF019 Unexpected ASTMF_op");
    return "ERROR NAME ASTMF_op";
  }

}
