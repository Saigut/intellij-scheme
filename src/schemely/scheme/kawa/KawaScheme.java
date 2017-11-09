package schemely.scheme.kawa;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.lexer.SchemeLexer;
import schemely.parser.DefaultPsiCreator;
import schemely.parser.SchemeParser;
import schemely.parser.SchemePsiCreator;
import schemely.repl.REPLProvider;
import schemely.repl.UnsupportedREPLProvider;
import schemely.scheme.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaScheme implements Scheme
{
  private static final UnsupportedREPLProvider IN_PROCESS_PROVIDER = new UnsupportedREPLProvider();
  private static final UnsupportedREPLProvider EXTERNAL_PROVIDER = new UnsupportedREPLProvider();

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
    return new DefaultPsiCreator();
  }

  @Override
  public boolean supportsSquareBracesForLists()
  {
    return false;
  }

  @Override
  public REPLProvider getInProcessREPLProvider()
  {
    return IN_PROCESS_PROVIDER;
  }

  @Override
  public REPLProvider getExternalREPLProvider()
  {
    return EXTERNAL_PROVIDER;
  }
}
