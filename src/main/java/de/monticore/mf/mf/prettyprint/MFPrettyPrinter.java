// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.mf.mf._ast.ASTMFArtifact;
import de.monticore.mf.mf._ast.ASTMFCompilationUnit;
import de.monticore.mf.mf._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.mf._visitor.MFHandler;
import de.monticore.mf.mf._visitor.MFTraverser;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.List;

public class MFPrettyPrinter implements MFHandler {

  protected IndentPrinter printer;

  protected MFTraverser traverser;

  public MFPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTMFCompilationUnit compilationUnit) {
    CommentPrettyPrinter.printPreComments(compilationUnit, getPrinter());
    if (compilationUnit.isPresentMCPackageDeclaration()) {
      compilationUnit.getMCPackageDeclaration().accept(getTraverser());
    }
    for (ASTMCImportStatement importStatement : compilationUnit.getMCImportStatementList()) {
      importStatement.accept(getTraverser());
    }
    compilationUnit.getMFArtifact().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(compilationUnit, getPrinter());
  }

  @Override
  public void handle(ASTMFArtifact artifact) {
    CommentPrettyPrinter.printPreComments(artifact, getPrinter());
    getPrinter().print("montifun ");
    getPrinter().print(artifact.getName());
    getPrinter().println(" {");
    getPrinter().indent();

    for (ASTMFFunctionDeclaration functionDeclaration : artifact.getMFFunctionDeclarationList()) {
      functionDeclaration.accept(getTraverser());
      getPrinter().println();
    }

    getPrinter().unindent();
    getPrinter().println("}");
    CommentPrettyPrinter.printPostComments(artifact, getPrinter());
  }

  @Override
  public void handle(ASTMFFunctionDeclaration functionDeclaration) {
    CommentPrettyPrinter.printPreComments(functionDeclaration, getPrinter());
    if (functionDeclaration.isPresentMCReturnType()) {
      functionDeclaration.getMCReturnType().accept(getTraverser());
      getPrinter().print(" ");
    }
    getPrinter().print(functionDeclaration.getName());
    if (functionDeclaration.isPresentParenthesis()) {
      getPrinter().print("(");
      printList(functionDeclaration.getMFParameterList(), ", ");
      getPrinter().print(")");
    }
    getPrinter().print(" = ");
    functionDeclaration.getExpression().accept(getTraverser());
    getPrinter().println(";");

    CommentPrettyPrinter.printPostComments(functionDeclaration, getPrinter());
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

  @Override public MFTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MFTraverser traverser) {
    this.traverser = traverser;
  }
}
