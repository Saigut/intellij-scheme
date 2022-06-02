package main.highlighter;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.lexer.SchemeTokens;

public class SchemeBraceMatcher implements PairedBraceMatcher
{
  private static final
  BracePair[]
    PAIRS =
    new BracePair[]{new BracePair(SchemeTokens.LEFT_PAREN, SchemeTokens.RIGHT_PAREN, true),
                    new BracePair(SchemeTokens.LEFT_SQUARE, SchemeTokens.RIGHT_SQUARE, true),
                    new BracePair(SchemeTokens.LEFT_CURLY, SchemeTokens.RIGHT_CURLY, true),
                    new BracePair(SchemeTokens.OPEN_VECTOR, SchemeTokens.RIGHT_PAREN, true)};

  public BracePair[] getPairs()
  {
    return PAIRS;
  }

  public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType lbraceType, @Nullable IElementType tokenType)
  {
    return tokenType == null ||
           SchemeTokens.WHITESPACE_SET.contains(tokenType) ||
           SchemeTokens.COMMENTS.contains(tokenType) ||
           tokenType == SchemeTokens.COMMA ||
           tokenType == SchemeTokens.RIGHT_SQUARE ||
           tokenType == SchemeTokens.RIGHT_PAREN ||
           tokenType == SchemeTokens.RIGHT_CURLY;
  }

  public int getCodeConstructStart(PsiFile file, int openingBraceOffset)
  {
    return openingBraceOffset;
  }
}
