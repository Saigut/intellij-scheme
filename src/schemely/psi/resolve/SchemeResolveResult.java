package schemely.psi.resolve;

import com.intellij.psi.PsiNamedElement;


public class SchemeResolveResult implements com.intellij.psi.ResolveResult
{
  private final PsiNamedElement element;

  public SchemeResolveResult(PsiNamedElement element)
  {
    this.element = element;
  }

  public PsiNamedElement getElement()
  {
    return element;
  }

  public boolean isValidResult()
  {
    return true;
  }
}
