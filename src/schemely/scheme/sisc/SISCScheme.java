package schemely.scheme.sisc;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.parser.DefaultPsiCreator;
import schemely.parser.SchemePsiCreator;
import schemely.scheme.Scheme;
import schemely.scheme.sisc.lexer.SISCLexer;
import schemely.scheme.sisc.parser.SISCParser;
import schemely.scheme.sisc.psi.SISCPsiCreator;

/**
 * @author Colin Fleming
 */
public class SISCScheme implements Scheme
{
  @Override
  public Lexer getLexer()
  {
    return new SISCLexer();
  }

  @Override
  public PsiParser getParser()
  {
    return new SISCParser();
  }

  @Override
  public SchemePsiCreator getPsiCreator()
  {
    return new SISCPsiCreator(new DefaultPsiCreator());
  }

  @Override
  public boolean supportsSquareBracesForLists()
  {
    return true;
  }
}
