// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._ast.ASTMFParameter;
import de.monticore.mf.montifun._visitor.MontiFunHandler;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;

import java.util.List;

public class MontiFunPrettyPrinter implements MontiFunHandler {

  protected IndentPrinter printer;

  protected MontiFunTraverser traverser;

  public MontiFunPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public void handle(ASTMFCompilationUnit compilationUnit) {
    CommentPrettyPrinter.printPreComments(compilationUnit, getPrinter());
    if (compilationUnit.isPresentMCPackageDeclaration()) {
      compilationUnit.getMCPackageDeclaration().accept(getTraverser());
      getPrinter().println(2);
    }
    for (ASTMCImportStatement importStatement : compilationUnit.getMCImportStatementList()) {
      importStatement.accept(getTraverser());
      getPrinter().println();
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

  @Override
  public void handle(ASTMFParameter parameter) {
    if (parameter.isPresentMCType()) {
      parameter.getMCType().accept(getTraverser());
      getPrinter().print(" ");
    }
    getPrinter().print(parameter.getName());
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

  @Override public MontiFunTraverser getTraverser() {
    return traverser;
  }

  @Override public void setTraverser(MontiFunTraverser traverser) {
    this.traverser = traverser;
  }
}
