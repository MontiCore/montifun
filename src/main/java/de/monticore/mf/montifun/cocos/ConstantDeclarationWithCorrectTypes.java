/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._cocos.MontiFunASTMFConstantDeclarationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo to test that constant declarations have the correct type
 * E.g. int x = 2;
 */
public class ConstantDeclarationWithCorrectTypes
    implements MontiFunASTMFConstantDeclarationCoCo {

  @Override
  public void check(ASTMFConstantDeclaration node) {
    if (!node.isPresentMCType()) {
      // nothing to check
      return;
    }
    SymTypeExpression constantType =
        TypeCheck3.symTypeFromAST(node.getMCType());
    if (constantType.isObscureType()) {
      // error already logged
      return;
    }
    // use the type of the constant as target type for the expression
    SymTypeExpression exprType =
        TypeCheck3.typeOf(node.getExpression(), constantType);
    if (exprType.isObscureType()) {
      // error already logged
      return;
    }
    if (!SymTypeRelations.isCompatible(constantType, exprType)) {
      Log.error("0xFD007 unable to assign expression of type"
              + " \""
              + exprType.printFullName()
              + "\" to \""
              + constantType.printFullName()
              + "\"",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
