// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.MF2CD;

import com.google.common.base.Preconditions;
import de.monticore.cd.codegen.CD2JavaTemplates;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDPackageBuilder;
import de.monticore.codegen.TraverserBasedCodeGenerator;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._ast.ASTMFParameter;
import de.monticore.mf.montifun._visitor.MontiFunVisitor2;
import de.monticore.mf.montifun.codegen.javagen.MontiFunExpressionJavaGenerator;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.typeparameters.TypeParametersMill;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;
import de.monticore.umlmodifier.UMLModifierMill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.CodeGenSymTypeExpressionConverter.printConverted;
import static de.monticore.codegen.javagen.SymTypeExpression2JavaConverter.getJavaTypePrint;
import static de.monticore.types3.SymTypeRelations.normalize;
import static de.monticore.types3.TypeCheck3.symTypeFromAST;
import static de.monticore.types3.TypeCheck3.typeOf;

public class MF2CDVisitor implements MontiFunVisitor2 {

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDClass mainClass;

  protected final CD4C cd4C;

  protected GeneratorSetup generatorSetup;

  protected TraverserBasedCodeGenerator exprGen;

  public MF2CDVisitor(GeneratorSetup setup) {
    generatorSetup = Preconditions.checkNotNull(setup);
    if (!CD4C.isInitialized()) {
      CD4C.init(setup);
    }
    this.cd4C = CD4C.getInstance();
    this.exprGen = new MontiFunExpressionJavaGenerator(new IndentPrinter());
  }

  @Override
  public void visit(ASTMFArtifact mfArtifact) {
    mainClass = CDBasisMill.cDClassBuilder()
        .setName(mfArtifact.getName())
        .setModifier(
            UMLModifierMill.modifierBuilder()
                .PUBLIC()
                .build()
        )
        .build();
  }

  @Override
  public void endVisit(ASTMFCompilationUnit mfCompilationUnit) {
    // (CD) package
    ASTCDPackageBuilder cdPackageBuilder = CDBasisMill.cDPackageBuilder();
    if (mfCompilationUnit.isPresentMCPackageDeclaration()) {
      cdPackageBuilder.setMCQualifiedName(
          mfCompilationUnit.getMCPackageDeclaration().getMCQualifiedName());
    }
    else {
      cdPackageBuilder.setMCQualifiedName(new ASTMCQualifiedNameBuilder()
          .addParts("de")
          .addParts("monticore")
          .addParts("mf")
          .build()
      );
    }
    cdPackageBuilder.addCDElement(getMainClass());
    ASTCDPackage cdPackage = cdPackageBuilder.build();
    // (CD) definition
    ASTCDDefinition cdDefinition = CDBasisMill.cDDefinitionBuilder()
        .setName(getMainClass().getName())
        .setModifier(
            UMLModifierMill.modifierBuilder()
                .PUBLIC()
                .build()
        )
        .addCDElement(cdPackage)
        .build();
    // (CD) compilation unit
    ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CDBasisMill.cDCompilationUnitBuilder();
    if (mfCompilationUnit.isPresentMCPackageDeclaration()) {
      cdCompilationUnitBuilder.setMCPackageDeclaration(mfCompilationUnit.getMCPackageDeclaration());
    }
    cdCompilationUnitBuilder.setCDDefinition(cdDefinition);
    cdCompilationUnit = cdCompilationUnitBuilder.build();
    // imports
    for (ASTMCImportStatement mfImport : mfCompilationUnit.getMCImportStatementList()) {
      String importStr = MontiFunMill.prettyPrint(mfImport, false).stripTrailing(); // ensure no linebreak after ";"
      cd4C.addImport(getMainClass(), importStr);
    }
  }

  @Override
  public void endVisit(ASTMFFunctionDeclaration mfFunctionDeclaration) {
    try {
      // return type
      SymTypeExpression returnType =
          normalize(mfFunctionDeclaration.getSymbol().getType());
      String returnTypeJavaStr = convertSymTypeExpr2Java(returnType);
      ASTMCReturnType returnTypeJava;
      returnTypeJava = MontiFunMill.parser()
          .parse_StringMCReturnType(returnTypeJavaStr).get();

      List<ASTCDParameter> parsJava = mfFunctionDeclaration
          .streamMFParameters()
          .map(this::convertParam2Java)
          .toList();

      String javaExpr = printExprConverted(
          returnType,
          mfFunctionDeclaration.getExpression()
      );

      ASTCDMethod method = CD4CodeMill.cDMethodBuilder()
          .setModifier(
              CD4CodeMill.modifierBuilder()
                  .PUBLIC()
                  .STATIC()
                  .build()
          )
          .setMCReturnType(returnTypeJava)
          .setName(mfFunctionDeclaration.getName())
          .setCDParametersList(parsJava)
          .build();

      // type parameters
      if (mfFunctionDeclaration.isPresentTypeParameters()) {
        List<ASTTypeParameter> typeParsJava = new ArrayList<>();
        for (ASTTypeParameter typeParAST :
            mfFunctionDeclaration.getTypeParameters().getTypeParameterList()
        ) {
          typeParsJava.add(convertTypeParameter2Java(typeParAST));
        }
        method.setTypeParameters(
            TypeParametersMill.typeParametersBuilder()
                .setTypeParametersList(typeParsJava)
                .build()
        );
      }

      getMainClass().addCDMember(method);

      getGlex().replaceTemplate(
          CD2JavaTemplates.EMPTY_BODY,
          method,
          new StringHookPoint("return " + javaExpr + ";")
      );
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void endVisit(ASTMFConstantDeclaration mfConstantDeclaration) {
    SymTypeExpression constantType =
        normalize(mfConstantDeclaration.getSymbol().getType());
    ASTMCType javaMCType;
    try {
      String javaTypeStr =
          convertSymTypeExpr2Java(constantType);
      javaMCType = MontiFunMill.parser().parse_StringMCType(javaTypeStr).get();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    String javaExpr = printExprConverted(
        constantType,
        mfConstantDeclaration.getExpression()
    );

    ASTCDAttribute constant = CD4CodeMill.cDAttributeBuilder()
        .setName(mfConstantDeclaration.getName())
        .setModifier(
            CD4CodeMill.modifierBuilder()
                .PUBLIC()
                .STATIC()
                .FINAL()
                .build()
        )
        .setMCType(javaMCType)
        .build();
    getMainClass().addCDMember(constant);

    getGlex().replaceTemplate(
        CD2JavaTemplates.VALUE,
        constant,
        new StringHookPoint(" = " + javaExpr)
    );

  }

  public ASTCDCompilationUnit getCdCompilationUnit() {
    return this.cdCompilationUnit;
  }

  protected ASTCDClass getMainClass() {
    return this.mainClass;
  }

  protected GlobalExtensionManagement getGlex() {
    return this.generatorSetup.getGlex();
  }

  protected String convertSymTypeExpr2Java(SymTypeExpression type) {
    Preconditions.checkNotNull(type);
    return getJavaTypePrint(type);
  }

  protected ASTTypeParameter convertTypeParameter2Java(
      ASTTypeParameter modelTypePar
  ) {
    try {
      // simplified implementation ignoring intersection types
      List<ASTMCType> javaBounds = new ArrayList<>();
      for (ASTMCType modelBoundAST : modelTypePar.getMCTypeList()) {
        SymTypeExpression modelBound = normalize(symTypeFromAST(modelBoundAST));
        String javaBoundStr = convertSymTypeExpr2Java(modelBound);
        ASTMCType javaBound = MontiFunMill.parser().parse_StringMCType(javaBoundStr).get();
        javaBounds.add(javaBound);
      }
      return MontiFunMill.typeParameterBuilder()
          .setName(modelTypePar.getName())
          .setMCTypesList(javaBounds)
          .build();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected ASTCDParameter convertParam2Java(ASTMFParameter modelParam) {
    try {
      SymTypeExpression modelType =
          normalize(symTypeFromAST(modelParam.getMCType()));
      String javaTypeStr = convertSymTypeExpr2Java(modelType);
      ASTMCType javaType =
          MontiFunMill.parser().parse_StringMCType(javaTypeStr).get();
      return CD4CodeMill.cDParameterBuilder()
          .setName(modelParam.getName())
          .setMCType(javaType)
          .build();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected String printExprConverted(
      SymTypeExpression modelTargetType,
      ASTExpression expr
  ) {
    SymTypeExpression exprType = normalize(typeOf(expr));
    String javaExprUnconverted = exprGen.generateCode(expr);

    // could be improved
    IndentPrinter pl = new IndentPrinter();
    printConverted(
        pl,
        modelTargetType,
        exprType,
        p -> p.print(javaExprUnconverted)
    );
    return pl.getContent();
  }

}
