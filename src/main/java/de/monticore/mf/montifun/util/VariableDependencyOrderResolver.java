// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.util;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis.ExpressionsBasisMill;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisTraverser;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor2;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Calculates dependencies between variables,
 * including models functions.
 * <p>
 * Note: this could be extended/restructured to provide more information
 * if required.
 */
public class VariableDependencyOrderResolver {

  protected static final String LOG_NAME =
      VariableDependencyOrderResolver.class.getSimpleName();

  /**
   * Calculates an order to evaluate initial values of variables,
   * e.g., this does not have an order {@code
   * int x = foo();
   * int y = x;
   * int foo() = y;
   * }
   * <p>
   * Logs an error if there is no proper order.
   *
   * @param var2Node  VariableSymbols and their initialization expression.
   * @param func2Node FunctionSymbols and their
   *                  expression/statement-block
   * @return An order to evaluate the initial values of variables in,
   *     or empty, if no such order exists.
   */
  public Optional<List<VariableSymbol>> getVariableEvaluationOrder(
      Map<? extends VariableSymbol, ? extends ASTNode> var2Node,
      Map<? extends FunctionSymbol, ? extends ASTNode> func2Node
  ) {
    Preconditions.checkNotNull(var2Node);
    Preconditions.checkNotNull(func2Node);
    List<VariableSymbol> varOrder = new ArrayList<>(var2Node.size());
    Map<VariableSymbol, VarVisitState> var2Visited = new HashMap<>();
    Map<FunctionSymbol, Set<VariableSymbol>> func2Vars = new HashMap<>();
    for (VariableSymbol currentVar : var2Node.keySet()) {
      if (variableHasCircularDependency(
          var2Node, func2Node, func2Vars, var2Visited, varOrder, currentVar
      )) {
        return Optional.empty();
      }
    }
    return Optional.of(varOrder);
  }

  protected boolean variableHasCircularDependency(
      Map<? extends VariableSymbol, ? extends ASTNode> var2Node,
      Map<? extends FunctionSymbol, ? extends ASTNode> func2Node,
      // just a cache
      Map<FunctionSymbol, Set<VariableSymbol>> func2Vars,
      Map<VariableSymbol, VarVisitState> var2Visited,
      List<VariableSymbol> varOrder,
      VariableSymbol currentVariable
  ) {
    boolean res;
    VarVisitState currentState =
        var2Visited.getOrDefault(currentVariable, VarVisitState.TO_BE_VISITED);
    if (currentState == VarVisitState.VISITED) {
      // skipping, as we checked this already
      res = false;
    }
    else if (currentState == VarVisitState.VISITING) {
      Log.error("0xFD542 found a circular dependency"
              + "for the initialization of variable "
              + currentVariable.getFullName(),
          currentVariable.getSourcePosition()
      );
      res = true;
    }
    else {
      res = false;
      var2Visited.put(currentVariable, VarVisitState.VISITING);
      Set<VariableSymbol> dependencies = visitVarNodeToFindDependencies(
          var2Node.get(currentVariable), var2Node.keySet(),
          func2Node, func2Vars, new HashSet<>()
      );
      for (VariableSymbol depVar : dependencies) {
        if (!variableHasCircularDependency(
            var2Node, func2Node, func2Vars, var2Visited, varOrder, depVar
        )) {
          res = true;
        }
      }
      var2Visited.put(currentVariable, VarVisitState.VISITED);
      varOrder.add(currentVariable);
    }
    return res;
  }

  protected Set<VariableSymbol> visitVarNodeToFindDependencies(
      ASTNode varNode,
      Set<? extends VariableSymbol> knownVariables,
      Map<? extends FunctionSymbol, ? extends ASTNode> func2Node,
      Map<FunctionSymbol, Set<VariableSymbol>> func2Vars,
      Set<FunctionSymbol> visitingFunctions
  ) {
    final Set<VariableSymbol> dependencies = new HashSet<>();
    ExpressionsBasisTraverser traverser =
        ExpressionsBasisMill.inheritanceTraverser();
    ExpressionsBasisVisitor2 dependencyCollector =
        new ExpressionsBasisVisitor2() {
          @Override
          public void visit(ASTExpression node) {
            SymTypeExpression exprType = TypeCheck3.typeOf(node);
            Optional<ISymbol> sourceSymbolOpt =
                exprType.getSourceInfo().getSourceSymbol();
            if (sourceSymbolOpt.isEmpty()) {
              return;
            }
            ISymbol sourceSymbol = sourceSymbolOpt.get();
            if (BasicSymbolsMill.typeDispatcher()
                .isBasicSymbolsVariable(sourceSymbol)
            ) {
              VariableSymbol variableSymbol = BasicSymbolsMill
                  .typeDispatcher().asBasicSymbolsVariable(sourceSymbol);
              if (knownVariables.contains(variableSymbol)) {
                dependencies.add(variableSymbol);
              }
              return;
            }
            if (BasicSymbolsMill.typeDispatcher()
                .isBasicSymbolsFunction(sourceSymbol)
            ) {
              FunctionSymbol functionSymbol = BasicSymbolsMill
                  .typeDispatcher()
                  .asBasicSymbolsFunction(sourceSymbol);
              dependencies.addAll(getFunc2VarDependencies(
                  functionSymbol,
                  knownVariables,
                  func2Node,
                  func2Vars,
                  visitingFunctions
              ));
            }
          }
        };
    traverser.add4ExpressionsBasis(dependencyCollector);
    varNode.accept(traverser);
    return dependencies;
  }

  protected Set<VariableSymbol> getFunc2VarDependencies(
      FunctionSymbol func,
      Set<? extends VariableSymbol> knownVariables,
      Map<? extends FunctionSymbol, ? extends ASTNode> func2Node,
      Map<FunctionSymbol, Set<VariableSymbol>> func2Vars,
      Set<FunctionSymbol> funcsToBeVisited
  ) {
    Set<VariableSymbol> varDeps;
    if (!func2Node.containsKey(func)) {
      // we cannot take this function into consideration
      Log.info(
          "Skipping unknown function " + func.getFullName(),
          LOG_NAME
      );
      varDeps = new HashSet<>();
    }
    else if (func2Vars.containsKey(func)) {
      varDeps = new HashSet<>(func2Vars.get(func));
    }
    else if (funcsToBeVisited.contains(func)) {
      // break recursion
      varDeps = new HashSet<>();
    }
    else {
      funcsToBeVisited.add(func);
      Set<VariableSymbol> funcDeps = visitVarNodeToFindDependencies(
          func2Node.get(func),
          knownVariables,
          func2Node,
          func2Vars,
          funcsToBeVisited
      );
      funcsToBeVisited.remove(func);
      func2Vars.put(func, funcDeps);
      varDeps = new HashSet<>(funcDeps);
    }
    return varDeps;
  }

  protected enum VarVisitState {
    TO_BE_VISITED,
    VISITING,
    VISITED
  }

}




