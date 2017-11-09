package schemely.scheme;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.parser.SchemePsiCreator;
import schemely.repl.REPLProvider;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  // Parsing customisations
  Lexer getLexer();

  PsiParser getParser();

  SchemePsiCreator getPsiCreator();

  boolean supportsSquareBracesForLists();

  REPLProvider getInProcessREPLProvider();

  REPLProvider getExternalREPLProvider();
}
