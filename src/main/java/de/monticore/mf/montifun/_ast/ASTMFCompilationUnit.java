/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun._ast;

import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

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

  /**
   * adds an import statement
   *
   * @param importSignature the signature of the import statement to be added
   */
  public void addImportStatement(String importSignature) {
    if (!importSignature.startsWith("import ")) {
      importSignature = "import " + importSignature;
    }
    if (!importSignature.endsWith(";")) {
      importSignature += ";";
    }
    try {
      Optional<ASTMCImportStatement> importStatementOpt = new MontiFunParser()
          .parseMCImportStatement(new StringReader(importSignature));
      this.addMCImportStatement(importStatementOpt.get());
    }
    catch (IOException e) {
      Log.error("0x12002: can't parse '" + importSignature + "': ", e);
    }
  }

}
