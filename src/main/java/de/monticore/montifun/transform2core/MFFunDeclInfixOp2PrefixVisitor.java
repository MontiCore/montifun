/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;

import java.util.stream.Collectors;

public class MFFunDeclInfixOp2PrefixVisitor implements MontiFunVisitor2 {

  @Override
  public void visit(ASTMFFunDecl fundecl) {
    //From: a + b
    //To:   (+) a b
    //From: a `plus` b
    //To: plus a b
    ASTMF_funlhs funlhs = fundecl.getMF_funlhs();
    if (fundecl.getMF_funlhs() instanceof ASTMFInfixFunLhs) {
      ASTMFInfixFunLhs infixFunLhs = (ASTMFInfixFunLhs) fundecl.getMF_funlhs();
      ASTMFPrefixFunLhsBuilder builder = new ASTMFPrefixFunLhsBuilder();
      builder.setMF_var(infixFunLhs.getMF_varop().asASTMF_var());
      builder.setMF_apatsList(
          infixFunLhs.streamMF_pats()
              .map(ASTMF_pat::asASTMF_apat)
              .collect(Collectors.toList())
      );
      fundecl.setMF_funlhs(builder.build());
    }
  }

}
