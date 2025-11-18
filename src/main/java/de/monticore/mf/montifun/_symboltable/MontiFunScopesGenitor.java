// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFConstantDeclaration;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun.types.check.FullSynthesizeFromMontiFun;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.AbstractSynthesize;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.stream.Collectors;

public class MontiFunScopesGenitor extends MontiFunScopesGenitorTOP {

  @Override
  public IMontiFunArtifactScope createFromAST(ASTMFCompilationUnit node) {
    Log.errorIfNull(node,
        "0xAF884 Error by creating of the MontiFunScopesGenitor symbol table: top ast node is null");
    IMontiFunArtifactScope artifactScope = MontiFunMill.artifactScope();
    if (node.isPresentMCPackageDeclaration()) {
      artifactScope.setPackageName(node.getMCPackageDeclaration().getMCQualifiedName().getQName());
    }
    List<ImportStatement> imports = node.getMCImportStatementList().stream()
        .map(mcImport -> new ImportStatement(mcImport.getQName(), mcImport.isStar()))
        .collect(Collectors.toList());
    artifactScope.setImportsList(imports);
    artifactScope.setName(node.getMFArtifact().getName());

    putOnStack(artifactScope);
    node.accept(getTraverser());
    return artifactScope;
  }

  @Override
  public void visit(final ASTMFCompilationUnit compilationUnit) {
    super.visit(compilationUnit);

    final String nameOfModel = compilationUnit.getMFArtifact().getName();
    Log.debug("Building Symboltable for MontiFun: " + nameOfModel,
        MontiFunScopesGenitor.class.getSimpleName());

    // imports
    final List<ImportStatement> imports = compilationUnit.streamMCImportStatements()
        .map(i -> new ImportStatement(i.getQName(), i.isStar())).collect(Collectors.toList());

    getCurrentScope().get().setAstNode(compilationUnit);

    final IMontiFunArtifactScope enclosingScope = (IMontiFunArtifactScope) compilationUnit.getEnclosingScope();
    enclosingScope.setImportsList(imports);
    if (compilationUnit.isPresentMCPackageDeclaration()) {
      enclosingScope.setPackageName(compilationUnit.getMCPackageDeclaration()
          .getMCQualifiedName().getQName());
    }
  }

  @Override
  public void endVisit(final ASTMFCompilationUnit compilationUnit) {
    removeCurrentScope();
    super.endVisit(compilationUnit);
  }

}
