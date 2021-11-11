/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun.MontiFunMill;
import de.monticore.montifun._ast.ASTMF_module;
import de.monticore.montifun._visitor.MontiFunTraverser;

public class Transform2Core {
  private Transform2Core (){}

  static public void postParse(ASTMF_module ast) {
    MontiFunTraverser traverser = MontiFunMill.traverser();
    traverser.add4MontiFun(new MF_typeInfixFunc2PrefixVisitor());
    traverser.add4MontiFun(new MFPrefixNegatedInfixExpression2LexpVisitor());
    //traverser.add4MontiFun(new MF_funlhsInfix2PrefixVisitor());
    ast.accept(traverser);
  }

  static public void postSymTabCreation(ASTMF_module ast){
    //collect functions
    MontiFunTraverser traverser = MontiFunMill.traverser();
    FunctionCollectingVisitor functionCollector = new FunctionCollectingVisitor();
    traverser.add4MontiFun(functionCollector);
    ast.accept(traverser);
    //add functionSymbols to scopes
    functionCollector.addFunctionSymbolsToScopes();
    //now we have fixity information -> order operators
    traverser = MontiFunMill.traverser();
    traverser.add4MontiFun(new InfixOperatorSortingVisitor());
    //traverser.add4MontiFun(new MF_infixexpInfixOperatorApplication2PrefixVisitor());
    traverser.add4MontiFun(new MFFunDeclInfixOp2PrefixVisitor());
    traverser.add4MontiFun(new MF_aexpMFInfixLeftSectionExpression2MF_fexpVisitor());
    traverser.add4MontiFun(new MF_aexpMFInfixRightSectionExpression2MF_fexpVisitor());
    ast.accept(traverser);
  }

}
