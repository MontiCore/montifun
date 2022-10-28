package de.monticore.mf.montifun._cocos;

import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
import de.se_rwth.commons.logging.Log;

/**
 * CoCo to test that function declarations have the correct type
 * E.g. int x() = 2;
 */
public class FunctionDeclarationWithCorrectTypes implements MontiFunASTMFFunctionDeclarationCoCo {

  protected static final String ERROR_UNABLE_TO_CALC_TYPE =
      "0xF2005 unable to calculate Type";

  protected static final String ERROR_UNABLE_TO_ASSIGN_TYPE =
      "0xF2006 unable to assign Type";

  IDerive deriver;

  ISynthesize synthesizer;

  public FunctionDeclarationWithCorrectTypes(IDerive deriver, ISynthesize synthesizer) {
    this.deriver = deriver;
    this.synthesizer = synthesizer;
  }

  @Override
  public void check(ASTMFFunctionDeclaration node) {
    TypeCheckResult right = deriver.deriveType(node.getExpression());
    if (!right.isPresentResult() || right.getResult().isObscureType()) {
      Log.error(ERROR_UNABLE_TO_CALC_TYPE,
          node.getExpression().get_SourcePositionStart(),
          node.getExpression().get_SourcePositionEnd()
      );
      return;
    }
    if (node.isPresentMCReturnType()) {
      TypeCheckResult left = synthesizer.synthesizeType(node.getMCReturnType());
      if (!left.isPresentResult() || left.getResult().isObscureType()) {
        Log.error(ERROR_UNABLE_TO_CALC_TYPE,
            node.getMCReturnType().get_SourcePositionStart(),
            node.getMCReturnType().get_SourcePositionEnd()
        );
        return;
      }
      else {
        if (!TypeCheck.compatible(left.getResult(), right.getResult())) {
          Log.error(ERROR_UNABLE_TO_ASSIGN_TYPE
                  + " \""
                  + right.getResult().printFullName()
                  + "\" to \""
                  + left.getResult().printFullName()
                  + "\"",
              node.get_SourcePositionStart(),
              node.get_SourcePositionEnd()
          );
        }
      }
    }
  }
}
