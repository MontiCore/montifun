/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_qop extends ASTMF_qopTOP {

  public String getName() {
    if (isPresentMF_qvarop()) {
      return getMF_qvarop().getName();
    }
    if (isPresentMF_qconop()) {
      return getMF_qconop().getName();
    }
    Log.error("0xMF017 Unexpected ASTMF_qop");
    return "ERROR NAME ASTMF_qop";
  }

  public boolean isVar() {
    return isPresentMF_qvarop();
  }

  public boolean isCon() {
    return isPresentMF_qconop();
  }

  public ASTMF_qvar asASTMF_qvar() {
    return getMF_qvarop().asASTMF_qvar();
  }

  public ASTMF_qcon asASTMF_qcon() {
    return getMF_qconop().asASTMF_qcon();
  }

  public ASTMF_gcon asASTMF_gcon() {
    return asASTMF_qcon().asASTMF_gcon();
  }

  public ASTMF_aexp asASTMF_aexp() {
    ASTMF_aexpBuilder builder = new ASTMF_aexpBuilder();
    if (isVar()) {
      builder.setMF_qvar(asASTMF_qvar());
    }
    if (isCon()) {
      builder.setMF_gcon(asASTMF_gcon());
    }
    return builder.build();
  }

}
