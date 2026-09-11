/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._cocos.MontiFunASTMFConstantDeclarationCoCo;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks whether the constant name has already been defined in the scope.
 * In an ordered scope, an error will not be logged for the first defining constant.
 */
public class ConstantDeclarationNameAlreadyDefinedInScope
    implements MontiFunASTMFConstantDeclarationCoCo {

  /**
   * Indicates that the name of the constant has already been used in the scope
   */
  public static final String ERROR_CODE = "0xA0923";

  public static final String ERROR_MSG_FORMAT = "Constant '%s' is already defined in the scope.";

  public static final String ERROR_CODE_MISSING_SYMBOL = "0xA0924";

  @Override
  public void check(ASTMFConstantDeclaration node) {
    if (!node.isPresentSymbol()) {
      Log.error(String.format(ERROR_CODE_MISSING_SYMBOL + " Could not find a symbol for constant '%s', thus can not check coco '%s'. Check " +
              "whether you have run the symbol table creation before running this coco.",
          node.getName(), this.getClass().getSimpleName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return;
    }

    List<VariableSymbol> matchingCandidates = node.getEnclosingScope().resolveVariableMany(
        node.getName()
    );

    if (matchingCandidates.stream().anyMatch(v -> alreadyDefined(node, v))) {
      Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
          node.getName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  protected boolean alreadyDefined(ASTMFConstantDeclaration node, VariableSymbol o) {
    if (node.getSymbol() == o) return false;
    // MontiFun scopes are always ordered:
    // if both symbols are in the same scope, the first defining location is not considered already defined
    if (node.getEnclosingScope() == o.getEnclosingScope()) {
      return !o.isPresentAstNode()
          || !o.getAstNode().isPresent_SourcePositionStart()
          || !node.isPresent_SourcePositionStart()
          || o.getAstNode().get_SourcePositionStart().compareTo(node.get_SourcePositionStart()) < 0;
    } else {
      // if the symbols are in different scopes the name is already defined
      return true;
    }
  }
}
