/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MFPrefixNegatedInfixExpression2LexpVisitor implements MontiFunVisitor2 {

  @Override
  public void endVisit(ASTMF_infixexp node) {
    //From: - a
    //To:   negate a
    //this is done as "- a" is a specific syntax and not just a simple function application
    if (node.isPresentMFPrefixNegatedInfixExpression()) {
      ASTMF_infixexp infixexpUnnegated = node.getMFPrefixNegatedInfixExpression().getMF_infixexp();
      node.setMFPrefixNegatedInfixExpressionAbsent();
      node.setMF_lexp(
          new ASTMF_lexpBuilder()
              .setMF_fexp(
                  new ASTMF_fexpBuilder()
                      .setMF_aexpsList(
                          Arrays.asList(
                              getNegateAexp(),
                              infixexpUnnegated.asASTMF_aexp()
                          )
                      )
                      .build()
              )
              .build()
      );
    }
  }

  ASTMF_aexp getNegateAexp() {
    return new ASTMF_aexpBuilder()
        .setMF_qvar(
            new ASTMF_qvarBuilder()
                .setMF_qvarid(
                    new ASTMF_qvaridBuilder()
                        .setMFQSmallName(
                            new ASTMFQSmallNameBuilder()
                                .setMFSmallName("negate")
                                //might need modid in the future
                                .setMF_modidAbsent()
                                .build()
                        )
                        .build()
                )
                .build()
        )
        .build();

  }


}
