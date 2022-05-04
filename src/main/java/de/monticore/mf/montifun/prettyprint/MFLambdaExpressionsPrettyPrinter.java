// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.mf.mflambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.mf.mflambdaexpressions._ast.ASTLambdaParameters;
import de.monticore.mf.mflambdaexpressions._visitor.MFLambdaExpressionsHandler;
import de.monticore.mf.mflambdaexpressions._visitor.MFLambdaExpressionsTraverser;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;

public class MFLambdaExpressionsPrettyPrinter implements MFLambdaExpressionsHandler {

  protected IndentPrinter printer;

  protected MFLambdaExpressionsTraverser traverser;

  public MFLambdaExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTLambdaExpression lambdaExpression) {
    CommentPrettyPrinter.printPreComments(lambdaExpression, getPrinter());
    lambdaExpression.getLambdaParameters().accept(getTraverser());
    getPrinter().print(" -> ");
    lambdaExpression.getExpression().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(lambdaExpression, getPrinter());
  }

  @Override
  public void handle(ASTLambdaParameters lambdaParameters) {
    CommentPrettyPrinter.printPreComments(lambdaParameters, getPrinter());
    if (lambdaParameters.isPresentParenthesis()) {
      getPrinter().print("(");
      printList(lambdaParameters.getLambdaParameterList(), ", ");
      getPrinter().print(")");
    }
    else {
      lambdaParameters.getLambdaParameter(0).accept(getTraverser());
    }
    CommentPrettyPrinter.printPostComments(lambdaParameters, getPrinter());
  }

  protected void printList(List<? extends ASTNode> listToPrint, String separator) {
    for (int i = 0; i < listToPrint.size(); i++) {
      if (i != 0) {
        getPrinter().print(separator);
      }
      listToPrint.get(i).accept(getTraverser());
    }
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override public MFLambdaExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MFLambdaExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
