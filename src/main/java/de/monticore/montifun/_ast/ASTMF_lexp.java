/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_lexp extends ASTMF_lexpTOP {

  public ASTMF_aexp asASTMF_aexp() {
    if (isPresentMF_fexp()) {
      return getMF_fexp().asASTMF_aexp();
    } else {
      return new ASTMF_aexpBuilder()
          .setMF_exp(
              new ASTMF_expBuilder()
                  .setMF_infixexp(
                      new ASTMF_infixexpBuilder()
                          .setMF_lexp(this)
                          .build()
                  )
                  .build()
          )
          .build();
    }
  }

}
