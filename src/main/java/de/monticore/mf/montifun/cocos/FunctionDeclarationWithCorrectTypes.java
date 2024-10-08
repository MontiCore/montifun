/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._cocos.MontiFunASTMFFunctionDeclarationCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo to test that function declarations have the correct type
 * E.g. int x() = 2;
 */
public class FunctionDeclarationWithCorrectTypes
    implements MontiFunASTMFFunctionDeclarationCoCo {

  @Override
  public void check(ASTMFFunctionDeclaration node) {
    if(!node.isPresentMCReturnType()) {
      // nothing to check
      return;
    }
    SymTypeExpression returnType =
        TypeCheck3.symTypeFromAST(node.getMCReturnType());
    if (returnType.isObscureType()) {
      // error already logged
      return;
    }
    SymTypeExpression exprType =
        TypeCheck3.typeOf(node.getExpression(), returnType);
    if (exprType.isObscureType()) {
      // error already logged
      return;
    }
    if (!SymTypeRelations.isCompatible(returnType, exprType)) {
      Log.error("0xF2006 unable to assign expression of type"
              + " \""
              + exprType.printFullName()
              + "\" to \""
              + returnType.printFullName()
              + "\"",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }
}
