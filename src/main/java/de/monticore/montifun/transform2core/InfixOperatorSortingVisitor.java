/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * orders the infix operators using precedence climbing
 *
 * From: a*b+2
 * With: infixl 7 *
 *       infixl 6 +
 * To:   (+) ((*) a b) 2
 */
public class InfixOperatorSortingVisitor implements MontiFunVisitor2 {

  public void visit(ASTMF_infixexp node) {
    if (!node.isPresentMFInfixOperatorApplication()) {
      return;
    }
    ASTMFInfixOperatorApplication infix = node.getMFInfixOperatorApplication();
    //no negated expression yet...
    List<ASTMF_lexp> expressions = new LinkedList<>();
    List<ASTMF_qop> operators = new LinkedList<>();
    Map<ASTMF_qop, ASTMFFixity> operatorFixities = new HashMap<>();
    //collect all expressions and operators. E.g.:
    // lexp0 qop0 lexp1 qop1 lexp2
    while (true) {
      expressions.add(infix.getMF_lexp());
      operators.add(infix.getMF_qop());
      if(infix.getMF_infixexp().isPresentMFInfixOperatorApplication()){
        infix = infix.getMF_infixexp().getMFInfixOperatorApplication();
      } else{
        //last expression
        expressions.add(infix.getMF_infixexp().getMF_lexp());
        break;
      }
    }
    //do we need to sort?
    //if (operators.size() < 2) {
    //  return;
    //}
    //get fixities
    for (ASTMF_qop qop : operators) {
      operatorFixities.put(qop,
          infix.getEnclosingScope()
              .resolveMFFun(qop.getName()).get()
              .getFixity()
      );
    }

    node.setMFInfixOperatorApplicationAbsent();
    ASTMF_lexp newExpression = parse(expressions, operators, operatorFixities, expressions.remove(0), 0);
    node.setMF_lexp(newExpression);

  }

  protected ASTMF_lexp parse(List<ASTMF_lexp> expressions,
                             List<ASTMF_qop> operators,
                             Map<ASTMF_qop, ASTMFFixity> operatorFixities,
                             ASTMF_lexp currentLeft,
                             int minPrecedence) {
    int maxNonAssociativeOpPrecedence = -1;

    //while finding operators "of equal importance" (e.g. "*" == "/")
    //we continue to consume them
    //e.g. a * b / c * d [EXIT HERE] + e
    while (
        !operators.isEmpty() &&
            ((operatorFixities.get(operators.get(0)).getAssociativity() == ASTOP_ASSOCIATIVITY.NONE) ?
            (operatorFixities.get(operators.get(0)).getPrecedence().getMFInteger() > minPrecedence) :
            (operatorFixities.get(operators.get(0)).getPrecedence().getMFInteger() >= minPrecedence))) {

      ASTMF_lexp currentRight = expressions.remove(0);
      ASTMF_qop currentOp = operators.remove(0);
      int currentPrecedence = operatorFixities.get(currentOp).getPrecedence().getMFInteger();

      //find wrong syntax like "a==b==c"
      if(operatorFixities.get(currentOp).getAssociativity() == ASTOP_ASSOCIATIVITY.NONE){
        if(maxNonAssociativeOpPrecedence >= currentPrecedence){
          Log.error("0xFF022 second non-associative operator with bad precedence"
             + " (e.g. \"a==b==c\")",
              currentOp.get_SourcePositionStart()
          );
        }
        maxNonAssociativeOpPrecedence = currentPrecedence;
      }

      //as long as we find "more important" (e.g. "*" > "+") operators,
      //start new parsing steps with a higher level
      //e.g. a + b - c [ENTER HERE] * d / e [ENTER HERE] ^ f
      while (
          !operators.isEmpty() &&
              ((operatorFixities.get(operators.get(0)).getAssociativity() == ASTOP_ASSOCIATIVITY.RIGHT) ?
                      (operatorFixities.get(operators.get(0)).getPrecedence().getMFInteger() >= currentPrecedence) :
                      (operatorFixities.get(operators.get(0)).getPrecedence().getMFInteger() > currentPrecedence))) {
        currentRight = parse(expressions, operators, operatorFixities, currentRight, currentPrecedence+1);
      }

      //apply currentOp
      currentLeft = new ASTMF_lexpBuilder()
          .setMF_fexp(
              new ASTMF_fexpBuilder()
                  .setMF_aexpsList(
                      Arrays.asList(
                          currentOp.asASTMF_aexp(),
                          currentLeft.asASTMF_aexp(),
                          currentRight.asASTMF_aexp()
                      )
                  )
                  .build()
          )
          .build();
    }
    return currentLeft;
  }

}
