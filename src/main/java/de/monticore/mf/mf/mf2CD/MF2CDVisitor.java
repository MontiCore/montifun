// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.mf.mf2CD;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDCompilationUnitBuilder;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._ast.ASTCDPackageBuilder;
import de.monticore.mf.mf._ast.ASTMFArtifact;
import de.monticore.mf.mf._ast.ASTMFCompilationUnit;
import de.monticore.mf.mf._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.mf._visitor.MFVisitor2;
import de.monticore.mf.mf.prettyprint.MFFullPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedNameBuilder;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.logging.Log;

import java.util.stream.Collectors;

public class MF2CDVisitor implements MFVisitor2 {
  protected final static String ERROR_CODE_NO_MAINCLASS = "0xFF055";

  protected final static String TEMPLATE_STATIC_METHOD = "de.monticore.mf2cd.StaticMethod";

  protected ASTCDCompilationUnit cdCompilationUnit;

  protected ASTCDClass mainClass;

  protected final CD4C cd4C;

  public MF2CDVisitor() {
    this.cd4C = CD4C.getInstance();
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
    cdCompilationUnitBuilder.setMCImportStatementsList(
        mfCompilationUnit.getMCImportStatementList());
    cdCompilationUnitBuilder.setCDDefinition(cdDefinition);
    cdCompilationUnit = cdCompilationUnitBuilder.build();
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
  public void visit(ASTMFFunctionDeclaration mfFunctionDeclaration) {
    MFFullPrettyPrinter prettyPrinter = new MFFullPrettyPrinter();

    //do: no type inference yet
    String returnType;
    if (mfFunctionDeclaration.isPresentMCReturnType()) {
      returnType = prettyPrinter.prettyprint(mfFunctionDeclaration.getMCReturnType());
    }
    else {
      Log.error("0xFF056 no return type given");
      returnType = "TypeNotInfered";
    }

    //do: move to template?
    final String arguments = mfFunctionDeclaration.getMFParameterList().stream()
        //do: add type inference
        .map(parameter -> {
              if (!parameter.isPresentMCType()) {
                Log.error("0xFF057 no parameter type given");
                return "TypeNotInfered " + parameter.getName();
              }
              return prettyPrinter.prettyprint(parameter.getMCType())
                  + " "
                  + parameter.getName();
            }
        ).collect(Collectors.joining(", "));

    getCd4C().addMethod(
        getMainClass(),
        TEMPLATE_STATIC_METHOD,
        mfFunctionDeclaration.getName(),
        returnType,
        arguments,
        "//todo add code"
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
