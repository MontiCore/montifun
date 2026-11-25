// (c) https://github.com/MontiCore/monticore
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MontiFunSymbolTableTest extends AbstractTest {

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldCreateSymTabForValidModels(String fileName) {
    ASTMFCompilationUnit ast = getTool().parse(readFile(fileName));
    getTool().createSymbolTable(ast);
    getTool().completeSymbolTable(ast);
  }
}
