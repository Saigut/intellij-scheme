package schemely.editor.selection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import schemely.psi.api.SchemeBraced;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;

import java.util.ArrayList;
import java.util.List;


public class SchemeListSelectioner implements ExtendWordSelectionHandler
{
  public boolean canSelect(PsiElement e)
  {
    return e instanceof SchemeList || e instanceof SchemeVector;
  }

  @Override
  public List<TextRange> select(PsiElement element, CharSequence editorText, int cursorOffset, Editor editor)
  {
    List<TextRange> result = new ArrayList<TextRange>();
    if (element instanceof SchemeBraced)
    {
      SchemeBraced list = (SchemeBraced) element;
      PsiElement left = list.getFirstBrace();
      PsiElement right = list.getLastBrace();
      if (right != null)
      {
        result.add(new TextRange(left.getTextRange().getStartOffset(), right.getTextRange().getEndOffset()));
      }
      else
      {
        result.add(new TextRange(left.getTextRange().getStartOffset(), element.getTextRange().getEndOffset()));
      }
    }

    return result;
  }
}
