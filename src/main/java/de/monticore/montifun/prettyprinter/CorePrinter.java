/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.prettyprinter;

import de.monticore.ast.ASTNode;
import de.monticore.montifun.MontiFunMill;
import de.monticore.montifun._ast.*;
import de.monticore.montifun._visitor.MontiFunHandler;
import de.monticore.montifun._visitor.MontiFunTraverser;
import de.monticore.montifun._visitor.MontiFunVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

import java.util.List;
import java.util.stream.Collectors;

public class CorePrinter implements MontiFunVisitor2, MontiFunHandler {
  protected MontiFunTraverser traverser;
  protected IndentPrinter printer;

  public CorePrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MontiFunTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MontiFunTraverser traverser) {
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  protected void print(String s) {
    getPrinter().print(s);
  }

  protected void println(String s) {
    getPrinter().println(s);
  }

  @Override
  public void handle(ASTMF_module module) {
    CommentPrettyPrinter.printPreComments(module, getPrinter());
    print("module "
        + module.getName()
    );
    // exports
    if (!module.isEmptyMF_exports()) {
      print(" (");
      printList(module.getMF_exportList(), ", ");
      handle(module.getMF_export(0));
      for (int i = 1; i < module.sizeMF_exports(); i++) {
        print(", ");
        handle(module.getMF_export(i));
      }
      print(")");
    }
    //body
    println(" {");
    //getPrinter().indent();
    module.getMF_body().accept(getTraverser());
    //getPrinter().unindent();
    println("}");
  }

  @Override
  public void endVisit(ASTMF_topdecl node) {
    println(";");
  }

  @Override
  public void handle(ASTMFPrefixFunLhs node) {
    node.getMF_var().accept(getTraverser());
    node.forEachMF_apats(apat -> {
      print(" ");
      apat.accept(getTraverser());
    });
  }

  @Override
  public void handle(ASTMFMultiFunLhs node) {
    print("(");
    node.getMF_funlhs().accept(getTraverser());
    print(")");
    node.forEachMF_apats(apat -> {
      print(" ");
      apat.accept(getTraverser());
    });
  }

  @Override
  public void visit(ASTMF_rhs node) {
    if (node.isPresentMF_exp()) {
      print(" = ");
    }
  }

  @Override
  public void handle(ASTMF_exp node) {
    node.getMF_infixexp().accept(getTraverser());
    if (node.isPresentMF_type()) {
      print(" :: ");
      if (node.isPresentMF_context()) {
        node.getMF_context().accept(getTraverser());
        print(" => ");
      }
      node.getMF_type().accept(getTraverser());
    }
  }

  @Override
  public void visit(ASTMFPrefixNegatedInfixExpression node) {
    print("- ");
  }

  @Override
  public void handle(ASTMFLambdaAbstraction node) {
    //"\\" MF_apat+ "->" MF_exp;
    print("\\");
    printList(node.getMF_apatList(), " ");
    print(" -> ");
    node.getMF_exp().accept(getTraverser());
  }

  @Override
  public void handle(ASTMF_fexp node){
    printList(node.getMF_aexpList(), " ");
  }

  @Override
  public void visit(ASTMF_aexp node) {
    if (node.isPresentMF_exp()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_aexp node) {
    if (node.isPresentMF_exp()) {
      print(")");
    }
  }

  @Override
  public void handle(ASTMFTupleExpression node) {
    print("(");
    printList(node.getMF_expList(), ", ");
    print(")");
  }

  @Override
  public void handle(ASTMFListExpression node) {
    print("[");
    printList(node.getMF_expList(), ", ");
    print("]");
  }

  @Override
  public void handle(ASTMFInfixLeftSectionExpression node) {
    print("(");
    node.getMF_infixexp().accept(getTraverser());
    print(" ");
    node.getMF_qop().accept(getTraverser());
    print(")");
  }

  @Override
  public void handle(ASTMFInfixRightSectionExpression node) {
    print("(");
    node.getMF_qop().accept(getTraverser());
    print(" ");
    node.getMF_infixexp().accept(getTraverser());
    print(")");
  }

  @Override
  public void handle(ASTMFTypeSignatureGenDecl node) {
    printList(node.getMFSVarList(), ", ");
    print(" :: ");
    if (node.isPresentMF_context()) {
      node.getMF_context().accept(getTraverser());
      print(" => ");
    }
    node.getMF_type().accept(getTraverser());
  }

  @Override
  public void handle(ASTMFFixityDecl node){
    node.getMFFixity().accept(getTraverser());
    print(" ");
    printList(node.getMF_opList(), ", ");
  }

  @Override
  public void handle(ASTMFFixity node){
    switch(node.getAssociativity()){
      case LEFT:
        print("infixl");
        break;
      case RIGHT:
        print("infixr");
        break;
      case NONE:
        print("infix ");
        break;
    }
    print(" ");
    node.getPrecedence().accept(getTraverser());
  }

  @Override
  public void handle(ASTMF_type node) {
    printList(node.getMF_atypeList(), " ");
    if (node.isPresentMF_type()) {
      print(" -> ");
      node.getMF_type().accept(getTraverser());
    }
  }

  @Override
  public void visit(ASTMF_atype node) {
    if (node.isPresentMF_type()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_atype node) {
    if (node.isPresentMF_type()) {
      print(")");
    }
  }

  @Override
  public void handle(ASTMFTupleType node) {
    print("(");
    printList(node.getMF_typeList(), ", ");
    print(")");
  }

  @Override
  public void visit(ASTMFListType node) {
    print("[");
  }

  @Override
  public void endVisit(ASTMFListType node) {
    print("]");
  }

  @Override
  public void visit(ASTMF_gtycon node) {
    //default version does not handle the enum
    if (node.isPresentPredefinedConstructor())
      print(node.getPredefinedConstructor());
  }

  @Override
  public void visit(ASTMF_gcon node) {
    //default version does not handle the enum
    if (node.isPresentPredefinedConstructor())
      print(node.getPredefinedConstructor());
  }

  public void print(ASTMFPREDEFINEDCONSTRUCTOR node) {
    switch (node) {
      case EMPTYTUPLE:
        print("()");
        break;
      case EMPTYLIST:
        print("[]");
        break;
      case FUNCTION:
        print("(->)");
        break;
    }
  }

  @Override
  public void handle(ASTMFTuplingConstructor node) {
    print("(");
    node.forEachComma(c -> print(","));
    print(")");
  }

  @Override
  public void handle(ASTMFPrefixConstrutorPattern node) {
    node.getMF_gcon().accept(getTraverser());
    print(" ");
    printList(node.getMF_apatList(), " ");
  }

  @Override
  public void visit(ASTMFNegativeNumber node) {
    print("-");
  }

  @Override
  public void visit(ASTMF_apat node) {
    if (node.isPresentMF_pat()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_apat node) {
    if (node.isPresentMF_pat()) {
      print(")");
    }
  }

  @Override
  public void handle(ASTMFAsPattern node) {
    node.getMF_var().accept(getTraverser());
    print("@");
    node.getMF_apat().accept(getTraverser());
  }

  @Override
  public void handle(ASTMFTuplePattern node) {
    print("(");
    printList(node.getMF_patList(), ", ");
    print(")");
  }

  @Override
  public void handle(ASTMFListPattern node) {
    print("[");
    printList(node.getMF_patList(), ", ");
    print("]");
  }

  @Override
  public void visit(ASTMF_wildcard node) {
    print("_");
  }

  @Override
  public void visit(ASTMF_var node) {
    if (node.isPresentMF_varsym()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_var node) {
    if (node.isPresentMF_varsym()) {
      print(")");
    }
  }

  @Override
  public void visit(ASTMF_qvar node) {
    if (node.isPresentMF_qvarsym()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_qvar node) {
    if (node.isPresentMF_qvarsym()) {
      print(")");
    }
  }

  @Override
  public void visit(ASTMF_con node) {
    if (node.isPresentMF_consym()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_con node) {
    if (node.isPresentMF_consym()) {
      print(")");
    }
  }

  @Override
  public void visit(ASTMF_qcon node) {
    if (node.isPresentMF_qconsym()) {
      print("(");
    }
  }

  @Override
  public void endVisit(ASTMF_qcon node) {
    if (node.isPresentMF_qconsym()) {
      print(")");
    }
  }

  @Override
  public void visit(ASTMF_varop node) {
    if (node.isPresentMF_varid()) {
      print("`");
    }
  }

  @Override
  public void endVisit(ASTMF_varop node) {
    if (node.isPresentMF_varid()) {
      print("`");
    }
  }

  @Override
  public void visit(ASTMF_conop node) {
    if (node.isPresentMF_conid()) {
      print("`");
    }
  }

  @Override
  public void endVisit(ASTMF_conop node) {
    if (node.isPresentMF_conid()) {
      print("`");
    }
  }

  @Override
  public void visit(ASTMF_gconsym node) {
    if (node.isPresentColon()) {
      print(":");
    }
  }

  @Override
  public void handle(ASTMF_varid node) {
    print(node.getMFSmallName());
  }

  @Override
  public void handle(ASTMF_conid node) {
    print(node.getMFLargeName());
  }

  @Override
  public void handle(ASTMF_tyvar node) {
    print(node.getMFSmallName());
  }

  @Override
  public void handle(ASTMF_tycon node) {
    print(node.getMFLargeName());
  }

  @Override
  public void handle(ASTMF_tycls node) {
    print(node.getMFLargeName());
  }

  @Override
  public void handle(ASTMF_modid node) {
    print(
        node.streamMFLargeNames()
            .collect(Collectors.joining("."))
    );
  }

  @Override
  public void handle(ASTMFQSmallName node) {
    if (node.isPresentMF_modid()) {
      node.getMF_modid().accept(getTraverser());
      print(".");
    }
    print(node.getMFSmallName());
  }

  @Override
  public void handle(ASTMFQLargeName node) {
    if (node.isPresentMF_modid()) {
      node.getMF_modid().accept(getTraverser());
      print(".");
    }
    print(node.getMFLargeName());
  }

  @Override
  public void handle(ASTMF_qvarsym node) {
    if (node.isPresentMF_modid()) {
      node.getMF_modid().accept(getTraverser());
      print(".");
    }
    node.getMF_varsym().accept(getTraverser());
  }

  @Override
  public void handle(ASTMF_qconsym node) {
    if (node.isPresentMF_modid()) {
      node.getMF_modid().accept(getTraverser());
      print(".");
    }
    node.getMF_consym().accept(getTraverser());
  }

  @Override
  public void handle(ASTMF_varsym node) {
    print(node.getMFTVarsym());
  }

  @Override
  public void handle(ASTMF_consym node) {
    print(node.getMFTConsym());
  }

  @Override
  public void handle(ASTMF_integer node) {
    print(String.valueOf(node.getMFInteger()));
  }

  @Override
  public void handle(ASTMF_float node) {
    print(node.getNum_Float());
  }

  @Override
  public void handle(ASTMF_char node) {
    print(node.getChar());
  }

  @Override
  public void handle(ASTMF_string node) {
    print(node.getString());
  }


  protected <T extends ASTNode> void printList(List<T> items, String delimiter) {
    if (items.isEmpty()) {
      return;
    }
    items.get(0).accept(getTraverser());
    for (int i = 1; i < items.size(); i++) {
      print(delimiter);
      items.get(i).accept(getTraverser());
    }
  }

  static public String toString(ASTNode node) {
    CorePrinter corePrinter = new CorePrinter(new IndentPrinter());
    MontiFunTraverser traverser = MontiFunMill.traverser();
    traverser.add4MontiFun(corePrinter);
    traverser.setMontiFunHandler(corePrinter);
    node.accept(traverser);
    return corePrinter.getPrinter().getContent();
  }

  //todo exports, MF_impdecl
}
