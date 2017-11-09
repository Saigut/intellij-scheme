package schemely.psi.resolve;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.NameHint;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import schemely.psi.api.SchemePsiElement;

import static schemely.psi.impl.SchemePsiElementBase.isWrongElement;


public abstract class ResolveUtil
{
  public static boolean resolve(PsiElement place, PsiScopeProcessor processor)
  {
    PsiElement lastParent = null;
    PsiElement current = place;
    while (current != null)
    {
      if (!current.processDeclarations(processor, ResolveState.initial(), lastParent, place))
      {
        return false;
      }
      lastParent = current;
      current = current.getContext();
    }

    return true;
  }

  public static boolean processElement(PsiScopeProcessor processor, PsiNamedElement namedElement)
  {
    if (namedElement == null)
    {
      return true;
    }
    NameHint nameHint = processor.getHint(NameHint.KEY);
    String name = nameHint == null ? null : nameHint.getName(ResolveState.initial());
    String elementName = namedElement.getName();
    if ((name == null || name.equals(elementName)) &&
        !elementName.endsWith(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED))
    {
      return processor.execute(namedElement, ResolveState.initial());
    }
    return true;
  }

  public static int getQuotingLevel(PsiElement place)
  {
    int ret = 0;
    PsiElement element = place;
    while (element != null)
    {
      if (element instanceof SchemePsiElement)
      {
        SchemePsiElement schemeElement = (SchemePsiElement) element;
        ret += schemeElement.getQuotingLevel();
      }
      element = element.getContext();
    }
    return ret;
  }

  public static PsiElement getNextNonLeafElement(@NotNull PsiElement element)
  {
    PsiElement next = element.getNextSibling();
    while ((next != null) && isWrongElement(next))
    {
      next = next.getNextSibling();
    }
    return next;
  }

  public static PsiElement getPrevNonLeafElement(@NotNull PsiElement element)
  {
    PsiElement next = element.getPrevSibling();
    while ((next != null) && isWrongElement(next))
    {
      next = next.getPrevSibling();
    }
    return next;
  }
}
