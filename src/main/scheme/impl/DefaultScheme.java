package main.scheme.impl;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import main.lexer.SchemeLexer;
import main.parser.SchemeParser;
import main.parser.SchemePsiCreator;
import main.scheme.Scheme;

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
