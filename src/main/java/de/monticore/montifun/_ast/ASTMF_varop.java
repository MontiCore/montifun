/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_varop extends ASTMF_varopTOP {

  public String getName() {
    if (isPresentMF_varid()) {
      return getMF_varid().getMFSmallName();
    }
    if (isPresentMF_varsym()) {
      return getMF_varsym().getMFTVarsym();
    }
    Log.error("0xMF002 Unexpected ASTMF_varop");
    return "ERROR NAME ASTMF_varop";
  }

  /**
   * converts this to an ASTMF_var
   * E.g. "+" to "(+)"
   * E.g. "`plus`" to "plus"
   *
   * @return the converted value
   */
  public ASTMF_var asASTMF_var() {
    ASTMF_varBuilder builder = new ASTMF_varBuilder();
    if (isPresentMF_varid()) {
      builder.setMF_varid(getMF_varid());
    }
    if (isPresentMF_varsym()) {
      builder.setMF_varsym(getMF_varsym());
    }
    return builder.build();
  }

}
