/* (c) https://github.com/MontiCore/monticore */
package de.monticore.mf.montifun._symboltable;

import de.monticore.mf.AbstractTest;
import de.monticore.mf.montifun.MontiFunMill;
import de.monticore.mf.montifun._ast.ASTMFCompilationUnit;
import de.monticore.mf.montifun._ast.ASTMFFunctionDeclaration;
import de.monticore.mf.montifun._parser.MontiFunParser;
import de.monticore.mf.montifun.util.MFSymbolTableUtil;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MontiFunScopesGenitorTest extends AbstractTest {

  protected final String DEBUG_LOG_NAME = "MontiFunScopesGenitorTest";

  protected static Stream<String> functionProvider() {
    return Stream.of(
        "boolean getTrue() = true;",
        "boolean not(boolean val) = !val;",
        "int plus(int a, int b) = a + b;"
    );
  }

  @Override
  @BeforeEach
  public void setup() {
    super.setup();
    MontiFunMill.globalScope().clear();
    MFSymbolTableUtil.prepareMill();
  }

  @ParameterizedTest
  @MethodSource("getParsableModels")
  public void shouldSetEnclosingScopeOfFunctionDeclaration(String filename) throws IOException {
    // Given
    ASTMFCompilationUnit ast = parse(filename);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();

    // When
    genitor.createFromAST(ast);

    // Then
    Assertions.assertAll(() -> {
      for (ASTMFFunctionDeclaration fun : ast.getMFArtifact().getMFFunctionDeclarationList()) {
        Assertions.assertNotNull(fun.getEnclosingScope(),
            String.format("%s: The ast of the function declaration is missing its enclosing scope.",
                fun.get_SourcePositionStart()));
      }
    });
  }

  @ParameterizedTest
  @MethodSource("functionProvider")
  @Order(0)
  public void handleFunctionDeclaration_shouldNotModifyScopeStack(String source) {
    // Given
    ASTMFFunctionDeclaration ast = parseFunctionDeclaration(source);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    IMontiFunArtifactScope artifactScope = MontiFunMill.artifactScope();
    genitor.scopeStack.addLast(artifactScope);

    // When
    ast.accept(genitor.traverser);

    // Then
    Assertions.assertEquals(artifactScope, genitor.scopeStack.getLast(),
        "The scope at the tail of the scope stack " +
            "should match the manually added scope but does not. " +
            "Thus, the genitor either removes too many scopes or " +
            "does not remove all added scopes.");
  }

  /**
   * Asserts that the enclosing scope of the ast of the function declaration is present
   * and matches the expected scope.
   *
   * @param source the String assumed to be a function declaration
   */
  @ParameterizedTest
  @MethodSource("functionProvider")
  @Order(1)
  public void handleFunctionDeclaration_shouldSetEnclosingScope(String source) {
    // Given
    ASTMFFunctionDeclaration ast = parseFunctionDeclaration(source);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    IMontiFunArtifactScope artifactScope = MontiFunMill.artifactScope();
    IMontiFunScope enclosingScope = MontiFunMill.scope();
    genitor.scopeStack.addLast(artifactScope);
    genitor.scopeStack.addLast(enclosingScope);

    // When
    ast.accept(genitor.traverser);

    // Then
    Assertions.assertAll(() -> {
      Assertions.assertNotNull(ast.getEnclosingScope(),
          "The ast of the function declaration is missing its enclosing scope.");
      Assertions.assertEquals(enclosingScope, ast.getEnclosingScope(),
          "The enclosing scope of the ast of the function declaration does not match the expected scope.");
    });
  }

  /**
   * Asserts that the spanned scope of the ast of the function declaration is present
   * and linked with the enclosing scope.
   *
   * @param source the String assumed to be a function declaration
   */
  @ParameterizedTest
  @MethodSource("functionProvider")
  @Order(2)
  public void handleFunctionDeclaration_shouldCreateSpannedScope(String source) {
    // Given
    ASTMFFunctionDeclaration ast = parseFunctionDeclaration(source);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    genitor.scopeStack.addLast(MontiFunMill.artifactScope());
    genitor.scopeStack.addLast(MontiFunMill.scope());

    // When
    ast.accept(genitor.traverser);

    // Then
    Assertions.assertAll(() -> {
      Assertions.assertNotNull(ast.getSpannedScope(),
          "The ast of the function declaration is missing its spanned scope.");
      assertTrue(ast.getSpannedScope().isPresentAstNode(),
          "The spanned scope is not linked with the ast of the function declaration.");
      Assertions.assertEquals(ast, ast.getSpannedScope().getAstNode(),
          "The ast of the spanned scope does not match the ast of the function declaration.");
      Assertions.assertNotNull(ast.getSpannedScope().getEnclosingScope(),
          "The enclosing scope of the spanned scope of the function declaration is missing.");
      Assertions.assertEquals(ast.getEnclosingScope(), ast.getSpannedScope().getEnclosingScope(),
          "The enclosing scope of the spanned scope of the function declaration does not match the expected scope.");
      assertTrue(ast.getEnclosingScope().getSubScopes().contains(ast.getSpannedScope()),
          "The enclosing scope of the function declaration does not contain the spanned scope as sub-scope.");
    });
  }

  /**
   * Asserts that the symbol of the function declaration is present and linked with
   * its ast, spanned scope, and enclosing scope.
   *
   * @param source the String assumed to be an function declaration with name
   */
  @ParameterizedTest
  @MethodSource("functionProvider")
  @Order(3)
  public void handleFunctionDeclaration_shouldCreateSymbol(String source) {
    // Given
    ASTMFFunctionDeclaration ast = parseFunctionDeclaration(source);
    MontiFunScopesGenitorDelegator genitor = MontiFunMill.scopesGenitorDelegator();
    genitor.scopeStack.addLast(MontiFunMill.artifactScope());
    IMontiFunScope enclosingScope = MontiFunMill.scope();
    genitor.scopeStack.addLast(enclosingScope);

    // When
    ast.accept(genitor.traverser);

    // Then
    Assertions.assertAll(() -> {
      assertTrue(ast.isPresentSymbol(),
          "The ast of the function declaration is not linked with its symbol.");
      assertTrue(ast.getSpannedScope().isPresentSpanningSymbol(),
          "The spanning symbol of the scope spanned by the function declaration is missing.");
      assertTrue(ast.getSpannedScope().getSpanningSymbol().isPresentAstNode(),
          "The symbol of the function declaration is not linked with its ast.");
      Assertions.assertNotNull(ast.getSpannedScope().getSpanningSymbol().getEnclosingScope(),
          "The symbol of the function declaration is missing its enclosing scope.");
      Assertions.assertEquals(ast.getSymbol(), ast.getSpannedScope().getSpanningSymbol(),
          "The symbol of the ast and the spanning symbol of the scope spanned by the function declaration do not match.");
      Assertions.assertEquals(ast.getSpannedScope(),
          ast.getSpannedScope().getSpanningSymbol().getSpannedScope(),
          "The scope spanned by the function declaration does not match the scope of its spanning symbol.");
      Assertions.assertEquals(ast, ast.getSpannedScope().getSpanningSymbol().getAstNode(),
          "The ast of the function declaration and the ast of its symbol do not match.");
      Assertions.assertEquals(ast.getEnclosingScope(),
          ast.getSpannedScope().getSpanningSymbol().getEnclosingScope(),
          "The enclosing scope of the symbol of the function declaration does not match the enclosing scope of its ast.");
      assertTrue(enclosingScope.getLocalFunctionSymbols().contains(ast.getSymbol()),
          "The enclosing scope of the function declaration does not contain the spanning symbol as local symbol.");
    });
  }

  /**
   * Parses the provided string as function declaration, catching exceptions to the
   * debug log and throwing runtime errors in case of parser errors.
   *
   * @param source the String to parse, assumed to be an function declaration,
   * @return the ast of the given String
   */
  protected ASTMFFunctionDeclaration parseFunctionDeclaration(String source) {
    MontiFunParser parser = MontiFunMill.parser();
    Optional<ASTMFFunctionDeclaration> ast;
    try {
      ast = parser.parse_StringMFFunctionDeclaration(source);
    }
    catch (IOException e) {
      Log.debug("An I/O Exception occurred parsing the invariant `%s`. ", DEBUG_LOG_NAME);
      Log.debug("Error thrown: " + e, DEBUG_LOG_NAME);
      throw new IllegalStateException(" An internal error occurred. See the debug log.");
    }
    assertFalse(parser.hasErrors(),
        String.format("There where errors parsing the function declaration `%s`. " +
            "The log lists the following findings: %s", source, Log.getFindings())
    );
    assertTrue(ast.isPresent(),
        String.format(
            "The parser did not return an abstract syntax tree for function declaration '%s'. " +
                "The log lists the following findings: %s", source, Log.getFindings())
    );
    return ast.get();
  }
}
