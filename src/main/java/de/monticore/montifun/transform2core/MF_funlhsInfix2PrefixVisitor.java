/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.montifun._ast.ASTMFPREDEFINEDCONSTRUCTOR.FUNCTION;

public class MF_funlhsInfix2PrefixVisitor implements MontiFunVisitor2 {

  @Override
  public void endVisit(ASTMFFunDecl node) {
    //From: a + b (= ...)
    //To:   (+) a b (= ...)
    if(node.getMF_funlhs() instanceof ASTMFInfixFunLhs){
      ASTMFInfixFunLhs lhs = (ASTMFInfixFunLhs)node.getMF_funlhs();
      ASTMFPrefixFunLhs newLhs = new ASTMFPrefixFunLhsBuilder()
          .setMF_var(lhs.getMF_varop().asASTMF_var())
          .setMF_apatsList(
              lhs.getMF_patList().stream()
                  .map(ASTMF_pat::asASTMF_apat)
                  .collect(Collectors.toList())
          )
          .build();
    }
  }

}
