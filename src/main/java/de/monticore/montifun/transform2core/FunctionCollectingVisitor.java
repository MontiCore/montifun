/* (c) https://github.com/MontiCore/monticore */
package de.monticore.montifun.transform2core;

import de.monticore.montifun._ast.*;
import de.monticore.montifun._symboltable.IMontiFunScope;
import de.monticore.montifun._symboltable.MFFunSymbol;
import de.monticore.montifun._symboltable.MFFunSymbolBuilder;
import de.monticore.montifun._visitor.MontiFunVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.montifun._ast.ASTMFPREDEFINEDCONSTRUCTOR.FUNCTION;

/**
 * Collects the parts of the functions in each module and creates MFFun objects
 * This is because the function parts may be rather spread out in the source code (unlike in e.g. JAVA)
 * Note: @MF_typeInfixFunc2PrefixVisitor has to run before this visitor
 */

public class FunctionCollectingVisitor implements MontiFunVisitor2 {

  protected class FunctionData {
    public Map<String, MFFunSymbol> functions = new HashMap<>();
    //we may not have found the type signature yet
    //we collect all associated structures and add them later
    public List<ASTMFFunDecl> functionDeclarations = new LinkedList<>();
    public List<ASTMFPatDecl> patternDeclarations = new LinkedList<>();
    public Map<String, ASTMFFixity> fixityDeclarations = new HashMap<>();
  }

  protected Map<IMontiFunScope, FunctionData> functionDatas = new HashMap<>();

  @Override
  public void visit(ASTMFTypeSignatureGenDecl typeSignature) {
    //are they functions (with parameters)?
    ASTMF_atype atype = typeSignature.getMF_type().getMF_atype(0);
    if (atype.isPresentMF_gtycon()
        && atype.getMF_gtycon().isPresentPredefinedConstructor()
        && atype.getMF_gtycon().getPredefinedConstructor() == FUNCTION) {
      //yes, add them
      typeSignature.streamMFSVars()
          .map(ASTMFSVar::getName)
          .forEach(name ->
              functionDatas
                  //ASTMFTypeSignatureGenDecl is one scope level below the others
                  .computeIfAbsent(typeSignature.getEnclosingScope().getEnclosingScope(), k -> new FunctionData())
                  .functions
                  .put(name,
                      new MFFunSymbolBuilder()
                          .setName(name)
                          .setTypeSignature(typeSignature)
                          .setAstNode(
                              new ASTMFFunBuilder()
                                  .setName(name)
                                  .build()
                          )
                          .build())
          );
    }
  }

  public void visit(ASTMFFunDecl funDecl) {
    functionDatas
        .computeIfAbsent(funDecl.getEnclosingScope(), k -> new FunctionData())
        .functionDeclarations
        .add(funDecl);
  }

  public void visit(ASTMFPatDecl patDecl) {
    functionDatas
        .computeIfAbsent(patDecl.getEnclosingScope(), k -> new FunctionData())
        .patternDeclarations
        .add(patDecl);
  }

  public void visit(ASTMFFixityDecl fixityDecl) {
    Map<String, ASTMFFixity> map = functionDatas
        .computeIfAbsent(fixityDecl.getEnclosingScope().getEnclosingScope(), k -> new FunctionData())
        .fixityDeclarations;

    fixityDecl.forEachMF_ops(
        op -> map.put(op.getName(), fixityDecl.getMFFixity())
    );
  }

  protected Map<IMontiFunScope, Collection<MFFunSymbol>> getFunctionSymbolsPerScope() {
    //add the associated declarations to the functions symbols
    for (FunctionData functionData : functionDatas.values()) {
      for (ASTMFFunDecl functionDeclaration : functionData.functionDeclarations) {
        String functionName = functionDeclaration.getMF_funlhs().getFunctionName();
        if (functionData.functions.containsKey(functionName)) {

          functionData.functions.get(functionName).getAstNode().addFunDecls(functionDeclaration);
        } else {
          Log.warn("0xFF004 could not find the function declaration for \""
              + functionName
              + "\""
          );
        }
      }
      //Todo patterns are not supported (yet?)
      functionData.fixityDeclarations.forEach(
          (functionName, fixity) -> {
            if(functionData.functions.containsKey(functionName)){
              functionData.functions.get(functionName).setFixity(fixity);
            } else {
              Log.warn("0xFF004 could not find the function declaration for \""
                  + functionName
                  + "\""
              );
            }
          }
      );

    }
    //fuse multiple declarations into one
    functionDatas.values().stream()
        .flatMap(functionData -> functionData.functions.values().stream())
        .forEach(FunctionDeclarationFuser::fuse);

    Map<IMontiFunScope, Collection<MFFunSymbol>> functionsPerScope =
        functionDatas.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().functions.values()
            ));
    return functionsPerScope;
  }

  public void addFunctionSymbolsToScopes() {
    getFunctionSymbolsPerScope().entrySet().stream()
        .forEach(
            entry -> entry.getValue().forEach(
                function -> entry.getKey().add(function)
            )
        );
  }

}