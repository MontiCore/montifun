// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.prettyprint;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.prettyprint.BitExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.expressions.prettyprint.LambdaExpressionsPrettyPrinter;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.ocl.oclexpressions.prettyprint.OCLExpressionsPrettyPrinter;
import de.monticore.ocl.optionaloperators.prettyprint.OptionalOperatorsPrettyPrinter;
import de.monticore.ocl.setexpressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFunctionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class MontiFunFullPrettyPrinter {
  protected IndentPrinter printer;

  protected MontiFunTraverser traverser;

  public MontiFunFullPrettyPrinter() {
    this(new IndentPrinter());
  }

  public MontiFunFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    traverser = MontiFunMill.traverser();

    MontiFunPrettyPrinter mfPP = new MontiFunPrettyPrinter(printer);
    MCSimpleGenericTypesPrettyPrinter genericPP = new MCSimpleGenericTypesPrettyPrinter(printer);
    SetExpressionsPrettyPrinter setPP = new SetExpressionsPrettyPrinter(printer);
    LambdaExpressionsPrettyPrinter lExpPP = new LambdaExpressionsPrettyPrinter(printer);
    OCLExpressionsPrettyPrinter oclexPP = new OCLExpressionsPrettyPrinter(printer);
    OptionalOperatorsPrettyPrinter optPP = new OptionalOperatorsPrettyPrinter(printer);
    BitExpressionsPrettyPrinter bitPP = new BitExpressionsPrettyPrinter(printer);

    ExpressionsBasisPrettyPrinter expPP = new ExpressionsBasisPrettyPrinter(printer);
    MCBasicsPrettyPrinter mcbPP = new MCBasicsPrettyPrinter(printer);
    MCCollectionTypesPrettyPrinter ctPP = new MCCollectionTypesPrettyPrinter(printer);
    MCBasicTypesPrettyPrinter btPP = new MCBasicTypesPrettyPrinter(printer);
    MCFunctionTypesPrettyPrinter ftPP = new MCFunctionTypesPrettyPrinter(printer);
    MCCommonLiteralsPrettyPrinter clPP = new MCCommonLiteralsPrettyPrinter(printer);
    CommonExpressionsPrettyPrinter cePP = new CommonExpressionsPrettyPrinter(printer);
    BitExpressionsPrettyPrinter bePP = new BitExpressionsPrettyPrinter(printer);

    traverser.setMontiFunHandler(mfPP);
    traverser.add4MCSimpleGenericTypes(genericPP);
    traverser.setMCSimpleGenericTypesHandler(genericPP);
    traverser.setLambdaExpressionsHandler(lExpPP);
    traverser.setSetExpressionsHandler(setPP);
    traverser.setOCLExpressionsHandler(oclexPP);
    traverser.setOptionalOperatorsHandler(optPP);
    traverser.add4BitExpressions(bitPP);
    traverser.setBitExpressionsHandler(bitPP);
    traverser.add4ExpressionsBasis(expPP);
    traverser.setExpressionsBasisHandler(expPP);
    traverser.add4MCBasics(mcbPP);
    traverser.add4MCCollectionTypes(ctPP);
    traverser.setMCCollectionTypesHandler(ctPP);
    traverser.add4MCBasicTypes(btPP);
    traverser.setMCBasicTypesHandler(btPP);
    traverser.add4MCFunctionTypes(ftPP);
    traverser.setMCFunctionTypesHandler(ftPP);
    traverser.add4MCCommonLiterals(clPP);
    traverser.setMCCommonLiteralsHandler(clPP);
    traverser.add4CommonExpressions(cePP);
    traverser.setCommonExpressionsHandler(cePP);
    traverser.add4BitExpressions(bePP);
    traverser.setBitExpressionsHandler(bePP);
  }

  public String prettyprint(ASTMFCompilationUnit node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTMFArtifact node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public String prettyprint(ASTNode node) {
    getPrinter().clearBuffer();
    node.accept(getTraverser());
    return getPrinter().getContent();
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public MontiFunTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(MontiFunTraverser traverser) {
    this.traverser = traverser;
  }
}
