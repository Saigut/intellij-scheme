package schemely.scheme.sisc;

import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import schemely.parser.DefaultPsiCreator;
import schemely.parser.SchemePsiCreator;
import schemely.repl.REPLProvider;
import schemely.scheme.Scheme;
import schemely.scheme.sisc.lexer.SISCLexer;
import schemely.scheme.sisc.parser.SISCParser;
import schemely.scheme.sisc.psi.SISCPsiCreator;

/**
 * @author Colin Fleming
 */
public class SISCScheme implements Scheme
{
  private static final SISCInProcessREPL.Provider IN_PROCESS_PROVIDER = new SISCInProcessREPL.Provider();
  private static final SISCProcessREPL.Provider EXTERNAL_PROVIDER = new SISCProcessREPL.Provider();

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
