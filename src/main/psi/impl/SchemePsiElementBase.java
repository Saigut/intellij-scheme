package main.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import main.psi.api.SchemePsiElement;


public abstract class SchemePsiElementBase extends ASTWrapperPsiElement implements SchemePsiElement
{
  final private String name;

  public SchemePsiElementBase(@NotNull ASTNode astNode, String name)
  {
    super(astNode);
    this.name = name;
  }

  public <T> T findFirstChildByClass(Class<T> aClass)
  {
    PsiElement element = getFirstChild();
    while (element != null && !aClass.isInstance(element))
    {
      element = element.getNextSibling();
    }
    return aClass.cast(element);
  }

  @Override
  public String toString()
  {
    return name == null ? super.toString() : name;
  }
}
