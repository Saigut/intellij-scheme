package schemely.psi.resolve.processors;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.NameHint;
import schemely.psi.resolve.SchemeResolveResult;

import java.util.HashSet;
import java.util.Set;


public class SymbolResolveProcessor extends ResolveProcessor
{
  private final Set<PsiElement> processedElements = new HashSet<PsiElement>();

  public SymbolResolveProcessor(String name)
  {
    super(name);
  }

  public boolean execute(PsiElement element, ResolveState resolveState)
  {
    if ((element instanceof PsiNamedElement) && !processedElements.contains(element))
    {
      PsiNamedElement namedElement = (PsiNamedElement) element;

      candidates.add(new SchemeResolveResult(namedElement));
      processedElements.add(namedElement);

      return false;
    }

    return true;
  }

  @Override
  public <T> T getHint(Key<T> hintKey)
  {
    if (hintKey.equals(NameHint.KEY))
    {
      return (T) this;
    }
    return null;
  }

  public String getName(ResolveState resolveState)
  {
    return name;
  }

  public boolean shouldProcess(DeclarationKind kind)
  {
    return true;
  }
}
