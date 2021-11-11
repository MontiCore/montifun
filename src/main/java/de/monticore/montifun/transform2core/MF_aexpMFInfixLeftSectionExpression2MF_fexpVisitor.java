/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.Arrays;

public class MF_aexpMFInfixLeftSectionExpression2MF_fexpVisitor implements MontiFunVisitor2 {

  @Override
  public void endVisit(ASTMF_aexp node) {
    //From: (a +)
    //To:   ((+) a)
    if (node.isPresentMFInfixLeftSectionExpression()) {
      node.setMF_exp(
          new ASTMF_expBuilder()
              .setMF_infixexp(
                  new ASTMF_infixexpBuilder()
                      .setMF_lexp(
                          new ASTMF_lexpBuilder()
                              .setMF_fexp(
                                  new ASTMF_fexpBuilder()
                                      .setMF_aexpsList(
                                          Arrays.asList(
                                              node.getMFInfixLeftSectionExpression().getMF_qop().asASTMF_aexp(),
                                              node.getMFInfixLeftSectionExpression().getMF_infixexp().asASTMF_aexp()
                                          )
                                      )
                                      .build()
                              )
                              .build()
                      )
                      .build()
              )
              .build()
      );
      node.setMFInfixLeftSectionExpressionAbsent();
    }
  }
}
