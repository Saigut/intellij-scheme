package schemely.scheme.impl;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.lexer.SchemeLexer;
import schemely.parser.SchemeParser;
import schemely.parser.SchemePsiCreator;
import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class DefaultScheme implements Scheme
{
  @Override
  public Lexer getLexer()
  {
    return new SchemeLexer();
  }

  @Override
  public PsiParser getParser()
  {
    return new SchemeParser();
  }

  @Override
  public SchemePsiCreator getPsiCreator()
  {
    return new SchemePsiCreator();
  }
}
