package schemely.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;

public class SchemeBraceMatcher implements PairedBraceMatcher
{
  private static final
  BracePair[]
    PAIRS =
    new BracePair[]{new BracePair(Tokens.LEFT_PAREN, Tokens.RIGHT_PAREN, true),
                    new BracePair(Tokens.LEFT_SQUARE, Tokens.RIGHT_SQUARE, true),
                    new BracePair(Tokens.LEFT_CURLY, Tokens.RIGHT_CURLY, true),
                    new BracePair(Tokens.OPEN_VECTOR, Tokens.RIGHT_PAREN, true)};

  public BracePair[] getPairs()
  {
    return PAIRS;
  }

  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType tokenType)
  {
    return tokenType == null ||
           Tokens.WHITESPACE_SET.contains(tokenType) ||
           Tokens.COMMENTS.contains(tokenType) ||
           tokenType == Tokens.COMMA ||
           tokenType == Tokens.RIGHT_SQUARE ||
           tokenType == Tokens.RIGHT_PAREN ||
           tokenType == Tokens.RIGHT_CURLY;
  }

  public int getCodeConstructStart(PsiFile file, int openingBraceOffset)
  {
    return openingBraceOffset;
  }
}
