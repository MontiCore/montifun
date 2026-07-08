// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.codegen.javagen;

import com.google.common.base.Preconditions;
import de.monticore.codegen.TraverserBasedCodeGenerator;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.bitexpressions.codegen.javagen.BitExpressionsJavaGenVisitor;
import de.monticore.expressions.commonexpressions.codegen.javagen.CommonExpressionsJavaGenVisitor;
import de.monticore.expressions.expressionsbasis.codegen.javagen.MontiFunExpressionsBasisJavaGenVisitor;
import de.monticore.expressions.lambdaexpressions.codegen.javagen.LambdaExpressionsJavaGenVisitor;
import de.monticore.expressions.tupleexpressions.codegen.javagen.TupleExpressionsJavaGenVisitor;
import de.monticore.expressions.uglyexpressions.codegen.javagen.UglyExpressionsJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._visitor.MontiFunTraverser;
import de.monticore.ocl.oclexpressions.codegen.javagen.OCLExpressionsJavaGenVisitor;
import de.monticore.ocl.optionaloperators.codegen.javagen.OptionalOperatorsJavaGenVisitor;
import de.monticore.ocl.setexpressions.codegen.javagen.SetExpressionsJavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.ITraverser;

/**
 * specifically created for Expressions and Types.
 */
public class MontiFunExpressionJavaGenerator
    implements TraverserBasedCodeGenerator {

  protected IndentPrinter printer;

  protected MontiFunTraverser traverser;

  public MontiFunExpressionJavaGenerator(IndentPrinter printer) {
    this.printer = Preconditions.checkNotNull(printer);
    init();
  }

  public void init() {
    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

    this.traverser = MontiFunMill.inheritanceTraverser();
    JavaGenVisitorState state = new JavaGenVisitorState(printer);

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals =
        new MCCommonLiteralsJavaGenVisitor(state);
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    BitExpressionsJavaGenVisitor visBitExpressions =
        new BitExpressionsJavaGenVisitor(state);
    traverser.setBitExpressionsHandler(visBitExpressions);

    CommonExpressionsJavaGenVisitor visCommonExpressions =
        new CommonExpressionsJavaGenVisitor(state);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    LambdaExpressionsJavaGenVisitor visLambdaExpressions =
        new LambdaExpressionsJavaGenVisitor(state);
    traverser.setLambdaExpressionsHandler(visLambdaExpressions);

    MontiFunExpressionsBasisJavaGenVisitor visExpressionBasis =
        new MontiFunExpressionsBasisJavaGenVisitor(state);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    OCLExpressionsJavaGenVisitor visOCLExpressions =
        new OCLExpressionsJavaGenVisitor(state);
    traverser.setOCLExpressionsHandler(visOCLExpressions);

    OptionalOperatorsJavaGenVisitor visOptionalOperators =
        new OptionalOperatorsJavaGenVisitor(state);
    traverser.setOptionalOperatorsHandler(visOptionalOperators);

    SetExpressionsJavaGenVisitor visSetExpressions =
        new SetExpressionsJavaGenVisitor(state);
    traverser.setSetExpressionsHandler(visSetExpressions);

    TupleExpressionsJavaGenVisitor visTupleExpressions =
        new TupleExpressionsJavaGenVisitor(state);
    traverser.setTupleExpressionsHandler(visTupleExpressions);

    UglyExpressionsJavaGenVisitor visUglyExpressions =
        new UglyExpressionsJavaGenVisitor(state);
    traverser.setUglyExpressionsHandler(visUglyExpressions);
  }

  @Override
  public IndentPrinter getPrinter() {
    return printer;
  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }

}
