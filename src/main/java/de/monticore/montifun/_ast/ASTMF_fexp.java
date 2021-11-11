/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun._ast;

public class ASTMF_fexp extends ASTMF_fexpTOP {

  public ASTMF_aexp asASTMF_aexp() {
    //function application? -> wrap in a new MF_aexp
    //e.g. "fib n" to "(fib n)"
    return getMF_aexpList().size() == 1 ?
        getMF_aexpList().get(0) :
        new ASTMF_aexpBuilder()
            .setMF_exp(
                new ASTMF_expBuilder()
                    .setMF_infixexp(
                        new ASTMF_infixexpBuilder()
                            .setMF_lexp(
                                new ASTMF_lexpBuilder()
                                    .setMF_fexp(
                                        this
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build();
  }

}
