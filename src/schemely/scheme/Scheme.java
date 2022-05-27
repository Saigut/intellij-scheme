package schemely.scheme;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.parser.SchemePsiCreator;

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
}
