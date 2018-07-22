package schemely.editor.selection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import schemely.psi.util.SchemePsiUtil;

import java.util.ArrayList;
import java.util.List;


public class SchemeLiteralSelectioner implements ExtendWordSelectionHandler
{
  public boolean canSelect(PsiElement e)
  {
    PsiElement parent = e.getParent();
    return SchemePsiUtil.isStringLiteral(e) || SchemePsiUtil.isStringLiteral(parent);
  }

  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor)
  {
    List<TextRange> result = new ArrayList<TextRange>();

    TextRange range = e.getTextRange();
    if (range.getLength() <= 2)
    {
      result.add(range);
    }
    else
    {
      result.add(new TextRange(range.getStartOffset() + 1, range.getEndOffset() - 1));
    }
    return result;
  }
}