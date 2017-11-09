package schemely.editor;

import com.intellij.codeInsight.editorActions.QuoteHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.tree.IElementType;
import schemely.lexer.Tokens;


public class SchemeQuoteHandler implements QuoteHandler
{
  public boolean isClosingQuote(HighlighterIterator iterator, int offset)
  {
    IElementType tokenType = iterator.getTokenType();

    if (tokenType == Tokens.STRING_LITERAL)
    {
      int start = iterator.getStart();
      int end = iterator.getEnd();
      return end - start >= 1 && offset == end - 1;
    }
    return false;
  }

  public boolean isOpeningQuote(HighlighterIterator iterator, int offset)
  {
    IElementType tokenType = iterator.getTokenType();

    // TODO CMF
    if (/*tokenType == Tokens.WRONG_STRING_LITERAL || */tokenType == Tokens.STRING_LITERAL)
    {
      int start = iterator.getStart();
      return offset == start;
    }
    return false;
  }

  public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset)
  {
    return true;
  }

  public boolean isInsideLiteral(HighlighterIterator iterator)
  {
    IElementType tokenType = iterator.getTokenType();
    return tokenType == Tokens.STRING_LITERAL;
  }
}
