// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._cocos.MontiFunASTMFCompilationUnitCoCo;
import de.monticore.mf.montifun.util.VariableDependencyOrderResolver;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

import java.util.HashMap;
import java.util.Map;

/**
 * CoCo to test that constants have no cyclic dependencies, e.g., {@code
 * int x = foo();
 * int y = x;
 * int foo() = y;
 * }
 * <p>
 * WARNING: this can only find cyclic dependencies
 * within one(!) Compilation Unit.
 */
public class ConstantDependenciesAreAcyclic
    implements MontiFunASTMFCompilationUnitCoCo {

  protected final VariableDependencyOrderResolver dependencyResolver =
      new VariableDependencyOrderResolver();

  @Override
  public void check(ASTMFCompilationUnit node) {
    Map<VariableSymbol, ASTExpression> var2Node = new HashMap<>();
    Map<FunctionSymbol, ASTExpression> func2Node = new HashMap<>();
    for (ASTMFConstantDeclaration constDecl :
        node.getMFArtifact().getMFConstantDeclarationList()
    ) {
      var2Node.put(constDecl.getSymbol(), constDecl.getExpression());
    }
    for (ASTMFFunctionDeclaration funcDecl :
        node.getMFArtifact().getMFFunctionDeclarationList()
    ) {
      func2Node.put(funcDecl.getSymbol(), funcDecl.getExpression());
    }

    // will Log an error on a cyclic dependency
    dependencyResolver.getVariableEvaluationOrder(var2Node, func2Node);
  }
}

