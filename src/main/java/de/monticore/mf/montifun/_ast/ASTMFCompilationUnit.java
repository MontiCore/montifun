/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun._ast;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

public class ASTMFCompilationUnit extends ASTMFCompilationUnitTOP {

  /**
   * gets the artifact's qualified name
   *
   * @return the artifact's qualified name
   */
  public String getQName() {
    ASTMCQualifiedName qName = getMCPackageDeclaration().getMCQualifiedName().deepClone();
    qName.addParts(getMFArtifact().getName());
    return qName.getQName();
  }

}
