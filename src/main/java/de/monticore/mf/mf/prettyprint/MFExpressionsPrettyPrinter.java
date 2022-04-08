// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf.prettyprint;

import de.monticore.mf.mfexpressions._ast.ASTMFParameter;
import de.monticore.mf.mfexpressions._visitor.MFExpressionsHandler;
import de.monticore.mf.mfexpressions._visitor.MFExpressionsTraverser;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class MFExpressionsPrettyPrinter implements MFExpressionsHandler {

  protected IndentPrinter printer;

  protected MFExpressionsTraverser traverser;

  public MFExpressionsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTMFParameter parameter) {
    CommentPrettyPrinter.printPreComments(parameter, getPrinter());
    if (parameter.isPresentMCType()) {
      parameter.getMCType().accept(getTraverser());
      getPrinter().print(" ");
    }
    getPrinter().print(parameter.getName());
    CommentPrettyPrinter.printPostComments(parameter, getPrinter());
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override public MFExpressionsTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MFExpressionsTraverser traverser) {
    this.traverser = traverser;
  }
}
