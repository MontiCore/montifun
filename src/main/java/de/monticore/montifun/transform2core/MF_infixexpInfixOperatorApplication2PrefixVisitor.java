/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.Arrays;

public class MF_infixexpInfixOperatorApplication2PrefixVisitor implements MontiFunVisitor2 {

  @Override
  public void endVisit(ASTMF_infixexp node) {
    //From: a - b
    //To:   (-) a b
    if (node.isPresentMFInfixOperatorApplication()) {
      ASTMF_lexp left = node.getMFInfixOperatorApplication().getMF_lexp();
      ASTMF_qop qop = node.getMFInfixOperatorApplication().getMF_qop();
      ASTMF_infixexp right = node.getMFInfixOperatorApplication().getMF_infixexp();

      node.setMFInfixOperatorApplicationAbsent();
      node.setMF_lexp(
          new ASTMF_lexpBuilder()
              .setMF_fexp(
                  new ASTMF_fexpBuilder()
                      .setMF_aexpsList(
                          Arrays.asList(
                              qop.asASTMF_aexp(),
                              left.asASTMF_aexp(),
                              right.asASTMF_aexp()
                          )
                      )
                      .build()
              )
              .build()
      );
    }
  }
}
