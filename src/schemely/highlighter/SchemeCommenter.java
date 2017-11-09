package schemely.highlighter;

import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;


public class SchemeCommenter implements CodeDocumentationAwareCommenter, Tokens
{
  public String getLineCommentPrefix()
  {
    return ";";
  }

  public String getBlockCommentPrefix()
  {
    return "#|";
  }

  public String getBlockCommentSuffix()
  {
    return "|#";
  }

  @Override
  public String getCommentedBlockCommentPrefix()
  {
    return "#|";
  }

  @Override
  public String getCommentedBlockCommentSuffix()
  {
    return "|#";
  }

  @Nullable
  public IElementType getLineCommentTokenType()
  {
    return COMMENT;
  }

  @Nullable
  public IElementType getBlockCommentTokenType()
  {
    return BLOCK_COMMENT;
  }

  @Nullable
  public IElementType getDocumentationCommentTokenType()
  {
    return null;
  }

  @Nullable
  public String getDocumentationCommentPrefix()
  {
    return null;
  }

  @Nullable
  public String getDocumentationCommentLinePrefix()
  {
    return null;
  }

  @Nullable
  public String getDocumentationCommentSuffix()
  {
    return null;
  }

  public boolean isDocumentationComment(PsiComment element)
  {
    return false;
  }


}
