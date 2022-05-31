package main.scheme;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import main.parser.SchemePsiCreator;

/**
 * @author Colin Fleming
 */
public interface Scheme
{
  // Parsing customisations
  Lexer getLexer();

  PsiParser getParser();

  SchemePsiCreator getPsiCreator();
}
