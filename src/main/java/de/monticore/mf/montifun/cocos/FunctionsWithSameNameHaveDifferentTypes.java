/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun.cocos;

import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._cocos.MontiFunASTMFFunctionDeclarationCoCo;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.util.SymTypeInferenceVariableReplaceVisitor;
import de.monticore.types3.util.SymTypeCollectionVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CoCo to test that two functions with the same name have different types
 * E.g. int id(int value) = value;
 *      String id(String value) = value;
 * is allowed, while
 *      int id(int value) = value;
 *      int id(int value) = value;
 * is not.
 * <p>
 * The types are compared in their normalized form,
 * e.g., (A | B) is the same type as (B | A),
 * and (A | B) is the same type as A, if B is a subtype of A.
 */
public class FunctionsWithSameNameHaveDifferentTypes
    implements MontiFunASTMFFunctionDeclarationCoCo {

  @Override
  public void check(ASTMFFunctionDeclaration node) {
    FunctionSymbol funcSym = node.getSymbol();
    if (!hasUsableType(funcSym)) {
      // error already logged
      return;
    }
    SymTypeOfFunction funcType = funcSym.getFunctionType();
    List<FunctionSymbol> sameNameFuncs =
        funcSym.getEnclosingScope().resolveFunctionMany(funcSym.getName());
    // only compare to functions declared before this one,
    // to avoid logging an error for each of the two functions
    int index = sameNameFuncs.indexOf(funcSym);
    for (int i = 0; i < index; i++) {
      FunctionSymbol other = sameNameFuncs.get(i);
      if (hasUsableType(other) && isSameType(funcType, other.getFunctionType())) {
        Log.error("0xF2007 function with name \""
                + funcSym.getName()
                + "\" has already been declared with the same type \""
                + funcType.printFullName()
                + "\"",
            node.get_SourcePositionStart(),
            node.get_SourcePositionEnd()
        );
        break;
      }
    }
  }

  /**
   * whether the two types are the same,
   * comparing their normalized forms
   * (each type has a unique normalized representation,
   * which considers, e.g., union types to be unordered,
   * and drops unionized types that are subtypes of other unionized types)
   */
  protected boolean isSameType(
      SymTypeOfFunction typeA,
      SymTypeOfFunction typeB
  ) {
    SymTypeInferenceVariable placeholder =
        SymTypeExpressionFactory.createInferenceVariable(
            SymTypeExpressionFactory.createBottomType(),
            SymTypeExpressionFactory.createTopType(),
            "FV{comparison}"
        );
    return SymTypeRelations.normalize(replaceFreeVars(typeA, placeholder))
        .deepEquals(SymTypeRelations.normalize(replaceFreeVars(typeB, placeholder)));
  }

  /**
   * Raw generic types, e.g., "List", are represented as
   * "List<FV{List.X}#k>" with a fresh free variable per use site,
   * which are equal for our purposes,
   * and are therefore replaced by a shared placeholder.
   */
  protected SymTypeExpression replaceFreeVars(
      SymTypeExpression type,
      SymTypeInferenceVariable placeholder
  ) {
    List<SymTypeExpression> freeVars = new SymTypeCollectionVisitor()
        .calculate(type, SymTypeExpression::isInferenceVariable);
    Map<SymTypeInferenceVariable, SymTypeExpression> replaceMap =
        new HashMap<>();
    freeVars.forEach(variable -> replaceMap.put(
        variable.asInferenceVariable(), placeholder
    ));
    return new SymTypeInferenceVariableReplaceVisitor()
        .calculate(type, replaceMap);
  }

  /**
   * whether the type of the function is known:
   * the return type and the types of all parameters are set and not obscure
   */
  protected boolean hasUsableType(FunctionSymbol funcSym) {
    SymTypeExpression returnType = funcSym.getType();
    if (returnType == null || returnType.isObscureType()) {
      return false;
    }
    for (VariableSymbol parameter : funcSym.getParameterList()) {
      SymTypeExpression parameterType = parameter.getType();
      if (parameterType == null || parameterType.isObscureType()) {
        return false;
      }
    }
    return true;
  }
}
