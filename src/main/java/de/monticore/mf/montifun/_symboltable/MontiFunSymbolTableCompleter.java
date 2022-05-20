// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._ast.ASTMFParameter;
import de.monticore.mf.montifun._visitor.MontiFunHandler;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun._visitor.MontiFunVisitor2;
import de.monticore.ocl.types.check.OCLDeriver;
import de.monticore.ocl.types.check.OCLSynthesizer;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

public class MontiFunSymbolTableCompleter implements MontiFunVisitor2, MontiFunHandler {

  protected static final String UNKNOWN_TYPE_ERROR = "0xDC193 unknown type";

  protected MontiFunTraverser traverser;

  protected OCLDeriver deriver;

  protected OCLSynthesizer synthesizer;

  /**
   * sets the return types for functions that do have them set explicitly
   *
   * @param node the function
   */
  @Override
  public void visit(ASTMFFunctionDeclaration node) {
    // return value
    if (node.isPresentMCReturnType()) {
      TypeCheckResult type = getSynthesizer().synthesizeType(node.getMCReturnType());
      if (type.isPresentResult()) {
        node.getSymbol().setType(type.getResult());
      }
      else {
        Log.error(UNKNOWN_TYPE_ERROR, node.getMCReturnType().get_SourcePositionStart());
      }
    }
  }

  @Override
  public void visit(ASTMFParameter node) {
    if (node.isPresentMCType()) {
      TypeCheckResult type = getSynthesizer().synthesizeType(node.getMCType());
      if (type.isPresentResult()) {
        node.getSymbol().setType(type.getResult());
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
          TypeCheckResult type = deriver.deriveType(fun.getExpression());
          if (type.isPresentResult()) {
            fun.getSymbol().setType(type.getResult());
            derivedNewType = true;
          }
        }
      }
    } while (derivedNewType);
  }

  public OCLDeriver getDeriver() {
    return deriver;
  }

  public void setDeriver(OCLDeriver deriver) {
    this.deriver = deriver;
  }

  public OCLSynthesizer getSynthesizer() {
    return synthesizer;
  }

  public void setSynthesizer(OCLSynthesizer synthesizer) {
    this.synthesizer = synthesizer;
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
