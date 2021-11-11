/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

import de.se_rwth.commons.logging.Log;

public class ASTMF_infixexp extends ASTMF_infixexpTOP {

  public ASTMF_aexp asASTMF_aexp() {
    if (isPresentMF_lexp()) {
      return getMF_lexp().asASTMF_aexp();
    }
    //any MFInfixOperatorApplication should have been converted by now
    Log.error("FF0018 unexpected MF_infixexp");
    return null;
  }

}
