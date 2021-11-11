/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_conop extends ASTMF_conopTOP {

  public String getName() {
    if (isPresentMF_conid()) {
      return getMF_conid().getMFLargeName();
    }
    if (isPresentMF_consym()) {
      return getMF_consym().getMFTConsym();
    }
    Log.error("0xMF015 Unexpected ASTMF_conop");
    return "ERROR NAME ASTMF_conop";
  }

  /**
   * converts this to an ASTMF_con
   * E.g. "+" to "(+)"
   * E.g. "`plus`" to "plus"
   *
   * @return the converted value
   */
  public ASTMF_con asASTMF_con() {
    ASTMF_conBuilder builder = new ASTMF_conBuilder();
    if(isPresentMF_conid()){
      builder.setMF_conid(getMF_conid());
    }
    if (isPresentMF_consym()) {
      builder.setMF_consym(getMF_consym());
    }
    return builder.build();
  }

}
