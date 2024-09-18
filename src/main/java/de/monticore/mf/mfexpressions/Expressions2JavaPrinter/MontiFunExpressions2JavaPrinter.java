// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mfexpressions.Expressions2JavaPrinter;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._prettyprint.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mccommonliterals._prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.mcbasics._prettyprint.MCBasicsPrettyPrinter;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.mf.montifun.types.check.FullDeriveFromMontiFun;
import de.monticore.mf.montifun.types.check.FullSynthesizeFromMontiFun;
import de.monticore.ocl.codegen.util.VariableNaming;
import de.monticore.ocl.codegen.visitors.CommonExpressionsPrinter;
import de.monticore.ocl.codegen.visitors.OCLExpressionsPrinter;
import de.monticore.ocl.codegen.visitors.SetExpressionsPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.mcbasictypes._prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.mccollectiontypes._prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.mcsimplegenerictypes._prettyprint.MCSimpleGenericTypesPrettyPrinter;

public class MontiFunExpressions2JavaPrinter {
  protected MontiFunTraverser traverser;

  protected IndentPrinter printer;

  protected MontiFunTraverser getTraverser() {
    return this.traverser;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public MontiFunExpressions2JavaPrinter() {
    this(new IndentPrinter());
  }

  protected MontiFunExpressions2JavaPrinter(IndentPrinter printer) {
    this(printer, new VariableNaming());
  }

  protected MontiFunExpressions2JavaPrinter(IndentPrinter printer, VariableNaming naming) {
    this(printer, naming, new FullDeriveFromMontiFun(), new FullSynthesizeFromMontiFun());
  }

  protected MontiFunExpressions2JavaPrinter(IndentPrinter printer, VariableNaming naming,
      IDerive deriver, ISynthesize synthesizer) {

    this.printer = printer;
    this.traverser = MontiFunMill.traverser();

    // Expressions
    CommonExpressionsPrinter comExprPrinter =
        new CommonExpressionsPrinter(printer, naming);
    this.traverser.setCommonExpressionsHandler(comExprPrinter);
    this.traverser.add4CommonExpressions(comExprPrinter);
    ExpressionsBasisPrettyPrinter exprBasPrinter = new ExpressionsBasisPrettyPrinter(printer, true);
    this.traverser.setExpressionsBasisHandler(exprBasPrinter);
    this.traverser.add4ExpressionsBasis(exprBasPrinter);
    OCLExpressionsPrinter oclExprPrinter =
        new OCLExpressionsPrinter(printer, naming);
    this.traverser.setOCLExpressionsHandler(oclExprPrinter);
    this.traverser.add4OCLExpressions(oclExprPrinter);
    SetExpressionsPrinter setExprPrinter =
        new SetExpressionsPrinter(printer, naming);
    this.traverser.setSetExpressionsHandler(setExprPrinter);
    this.traverser.add4SetExpressions(setExprPrinter);

    // Types
    MCSimpleGenericTypesPrettyPrinter simpleGenericTypes = new MCSimpleGenericTypesPrettyPrinter(
        printer, true);
    traverser.setMCSimpleGenericTypesHandler(simpleGenericTypes);
    traverser.add4MCSimpleGenericTypes(simpleGenericTypes);
    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer, true);
    traverser.setMCCollectionTypesHandler(collectionTypes);
    traverser.add4MCCollectionTypes(collectionTypes);
    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer, true);
    traverser.setMCBasicTypesHandler(basicTypes);
    traverser.add4MCBasicTypes(basicTypes);
    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer, false);
    traverser.add4MCBasics(basics);

    MCCommonLiteralsPrettyPrinter comLitPrinter = new MCCommonLiteralsPrettyPrinter(printer, false);
    this.traverser.setMCCommonLiteralsHandler(comLitPrinter);
    this.traverser.add4MCCommonLiterals(comLitPrinter);
  }

  public String print2Java(ASTExpression node) {
    this.getPrinter().clearBuffer();
    node.accept(traverser);
    return this.getPrinter().getContent();
  }

}
