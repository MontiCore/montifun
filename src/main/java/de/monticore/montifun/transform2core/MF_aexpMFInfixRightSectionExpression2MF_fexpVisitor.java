/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.Arrays;

public class MF_aexpMFInfixRightSectionExpression2MF_fexpVisitor implements MontiFunVisitor2 {

  //used to create unique var ids
  static Integer counter = 0;

  @Override
  public void endVisit(ASTMF_aexp node) {
    //From: (+ a)
    //To:   (\x -> (+) x a)
    if (node.isPresentMFInfixRightSectionExpression()) {
      String uniqueVarName = "varIRSE" + counter++;
      ASTMF_var uniqueVar = new ASTMF_varBuilder()
          .setMF_varid(
              new ASTMF_varidBuilder()
                  //InfixRightSectionExpression
                  .setMFSmallName(uniqueVarName)
                  .build()
          ).build();
      ASTMF_aexp uniqueVarAsAexp = new ASTMF_aexpBuilder()
          .setMF_qvar(
              new ASTMF_qvarBuilder()
                  .setMF_qvarid(
                      new ASTMF_qvaridBuilder()
                          .setMFQSmallName(
                              new ASTMFQSmallNameBuilder()
                                  .setMF_modidAbsent()
                                  .setMFSmallName(uniqueVarName)
                                  .build()
                          ).build()
                  ).build()
          ).build();

      node.setMF_exp(
          new ASTMF_expBuilder()
              .setMF_infixexp(
                  new ASTMF_infixexpBuilder()
                      .setMF_lexp(
                          new ASTMF_lexpBuilder()
                              .setMFLambdaAbstraction(
                                  new ASTMFLambdaAbstractionBuilder()
                                      .addMF_apat(
                                          new ASTMF_apatBuilder()
                                              .setMF_var(
                                                  uniqueVar
                                              ).build()
                                      )
                                      .setMF_exp(new ASTMF_expBuilder()
                                          .setMF_infixexp(
                                              new ASTMF_infixexpBuilder()
                                                  .setMF_lexp(
                                                      new ASTMF_lexpBuilder()
                                                          .setMF_fexp(
                                                              new ASTMF_fexpBuilder()
                                                                  .setMF_aexpsList(
                                                                      Arrays.asList(
                                                                          node.getMFInfixRightSectionExpression().getMF_qop().asASTMF_aexp(),
                                                                          uniqueVarAsAexp,
                                                                          node.getMFInfixRightSectionExpression().getMF_infixexp().asASTMF_aexp()
                                                                      )
                                                                  )
                                                                  .build()
                                                          )
                                                          .build()
                                                  )
                                                  .build()
                                          )
                                          .build()
                                      ).build()
                              )
                              .build()
                      ).build()
              ).build()
      );

      node.setMFInfixRightSectionExpressionAbsent();
    }
  }
}
