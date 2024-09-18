// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun.MF2CD;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.mf.mfexpressions.Expressions2JavaPrinter.MontiFunExpressions2JavaPrinter;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFArtifact;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._visitor.MontiFunVisitor2;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class MF2CDVisitor implements MontiFunVisitor2 {

  protected final static String TEMPLATE_STATIC_METHOD = "de.monticore.mf2cd.StaticMethod";

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDClass mainClass;

  protected final CD4C cd4C;

  public MF2CDVisitor() {
    this.cd4C = CD4C.getInstance();
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
  public void visit(ASTMFFunctionDeclaration mfFunctionDeclaration) {
    String returnType;
    if (mfFunctionDeclaration.getSymbol().getType() != null) {
      returnType = mfFunctionDeclaration.getSymbol().getType().printFullName();
    }
    else {
      Log.error("0xFF056 no return type given");
      returnType = "TypeNotInferred";
    }

    //do: move to template?
    final String arguments = mfFunctionDeclaration.getMFParameterList().stream()
        .map(parameter -> {
              if (parameter.getSymbol().getType() == null) {
                Log.error("0xFF057 no parameter type given");
                return "TypeNotInferred " + parameter.getName();
              }
              return parameter.getSymbol().getType().printFullName()
                  + " "
                  + parameter.getName();
            }
        ).collect(Collectors.joining(", "));

    String javaExpr = new MontiFunExpressions2JavaPrinter().print2Java(
        mfFunctionDeclaration.getExpression());
    getCd4C().addMethod(
        getMainClass(),
        TEMPLATE_STATIC_METHOD,
        mfFunctionDeclaration.getName(),
        returnType,
        arguments,
        javaExpr
    );
  }

  public ASTCDCompilationUnit getCdCompilationUnit() {
    return this.cdCompilationUnit;
  }

  protected ASTCDClass getMainClass() {
    return this.mainClass;
  }

  protected CD4C getCd4C() {
    return this.cd4C;
  }
}
