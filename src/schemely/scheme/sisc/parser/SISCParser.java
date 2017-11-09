package schemely.scheme.sisc.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.parser.SchemeParser;
import schemely.scheme.sisc.lexer.SISCTokens;

/**
 * @author Colin Fleming
 */
public class SISCParser extends SchemeParser
{
  private static final TokenSet SISC_PREFIXES = TokenSet.orSet(PREFIXES, TokenSet.create(SISCTokens.SYNTAX_QUOTE));

  @Override
  protected void parseDatum(PsiBuilder builder)
  {
    IElementType token = builder.getTokenType();
    if (SISCTokens.PTR_DEF == token)
    {
      parsePtrDef(builder);
    }
    else if (SISCTokens.PTR_REF == token)
    {
      parsePtrRef(builder);
    }
    else
    {
      super.parseDatum(builder);
    }
  }

  private void parsePtrDef(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    parseDatum(builder);
    marker.done(SISCAST.PTR_DEF);
  }

  private void parsePtrRef(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(SISCAST.PTR_REF);
  }

  @Override
  protected TokenSet getPrefixes()
  {
    return SISC_PREFIXES;
  }
}
