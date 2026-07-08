// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.expressionsbasis.codegen.javagen;

import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;

import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.typeOf;

/**
 * small fix until we get template based symbol printing
 */
public class MontiFunExpressionsBasisJavaGenVisitor
    extends ExpressionsBasisJavaGenVisitor {

  public MontiFunExpressionsBasisJavaGenVisitor(JavaGenVisitorState state) {
    super(state);
  }

  @Override
  public void traverse(ASTNameExpression node) {
    boolean didPrint = false;
    SymTypeExpression exprType = normalize(typeOf(node));
    if (exprType.isFunctionType()) {
      SymTypeOfFunction funcType = exprType.asFunctionType();
      // hacky workaround until we use proper templates in the symbols:
      // try to figure out if it is a MontiFun function
      if (funcType.hasSymbol() &&
          !(funcType.getSymbol() instanceof MethodSymbol) &&
          funcType.getSymbol().isPresentAstNode()
      ) {
        String funcFullName = funcType.getSymbol().getFullName();
        IScope scope = funcType.getSymbol().getEnclosingScope();
        while (scope != null) {
          if (scope instanceof IArtifactScope) {
            funcFullName = scope.getName() + "::" + funcFullName; // missing intersection types
            break;
          }
        }
        getPrinter().print("((");
        getPrinter().print(getJavaTypePrint(exprType));
        getPrinter().print(") ");
        getPrinter().print(funcFullName);
        getPrinter().print(")");
        didPrint = true;
      }
    }
    if (!didPrint) {
      super.traverse(node);
    }
  }

}
