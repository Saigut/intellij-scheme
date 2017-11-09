package schemely.editor.selection;

import com.intellij.codeInsight.editorActions.ExtendWordSelectionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import schemely.psi.impl.symbols.SchemeIdentifier;

import java.util.Collections;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class SchemeIdentifierSelectioner implements ExtendWordSelectionHandler
{
  @Override
  public boolean canSelect(PsiElement e)
  {
    PsiElement parent = e.getParent();
    return (e instanceof SchemeIdentifier) || (parent instanceof SchemeIdentifier);
  }

  @Override
  public List<TextRange> select(PsiElement e, CharSequence editorText, int cursorOffset, Editor editor)
  {
    return Collections.singletonList(e.getTextRange());
  }

  // Don't allow normal word selection for identifiers
  public static class BasicWordRestriction implements Condition<PsiElement>
  {
    @Override
    public boolean value(PsiElement element)
    {
      PsiElement parent = element.getParent();
      return !((element instanceof SchemeIdentifier) || (parent instanceof SchemeIdentifier));
    }
  }
}
