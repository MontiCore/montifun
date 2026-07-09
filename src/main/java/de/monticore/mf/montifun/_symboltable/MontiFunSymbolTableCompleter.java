// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._ast.ASTMFParameter;
import de.monticore.mf.montifun._visitor.MontiFunHandler;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun._visitor.MontiFunVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class MontiFunSymbolTableCompleter implements MontiFunVisitor2, MontiFunHandler {

  protected MontiFunTraverser traverser;

  /**
   * sets the return types for functions that do have them set explicitly
   *
   * @param node the function
   */
  @Override
  public void endVisit(ASTMFFunctionDeclaration node) {
    // return value
    SymTypeExpression type;
    if (node.isPresentMCReturnType()) {
      type = TypeCheck3.symTypeFromAST(node.getMCReturnType());
    }
    else {
      type = TypeCheck3.typeOf(node.getExpression());
    }
    if (!type.isObscureType()) {
      node.getSymbol().setType(type);
    }
    // else – error already logged
  }

  @Override
  public void endVisit(ASTMFConstantDeclaration node) {
    SymTypeExpression type;
    if (node.isPresentMCType()) {
      type = TypeCheck3.symTypeFromAST(node.getMCType());
    }
    else {
      type = TypeCheck3.typeOf(node.getExpression());
    }
    if (!type.isObscureType()) {
      node.getSymbol().setType(type);
    }
    node.getSymbol().setIsReadOnly(true);
    // else – error already logged
  }

  @Override
  public void visit(ASTMFParameter node) {
    if (node.isPresentMCType()) {
      SymTypeExpression type = TypeCheck3.symTypeFromAST(node.getMCType());
      if (!type.isObscureType()) {
        node.getSymbol().setType(type);
      }
    }
    if (node.getSymbol().getType() == null) {
      Log.error("0xDC725 unable to determine type of parameter", node.get_SourcePositionStart());
    }
  }

  /**
   * derives the return types for functions that do not have them set explicitly
   * only considers functions in current artifact
   * and does not handle circular dependencies
   * todo: replace with better type deriver (s. e.g. Haskell)?
   *
   * @param node the artifact
   */
  @Override
  public void endVisit(ASTMFArtifact node) {
    boolean derivedNewType;
    do {
      derivedNewType = false;
      for (ASTMFFunctionDeclaration fun : node.getMFFunctionDeclarationList()) {
        if (fun.getSymbol().getType() == null) {
          SymTypeExpression type = TypeCheck3.typeOf(fun.getExpression());
          if (!type.isObscureType()) {
            fun.getSymbol().setType(type);
            derivedNewType = true;
          }
        }
      }
    } while (derivedNewType);
  }

  @Override
  public MontiFunTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MontiFunTraverser traverser) {
    this.traverser = traverser;
  }
}
