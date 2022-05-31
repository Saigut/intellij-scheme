package main.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.psi.impl.SchemePsiElementBase;
import main.psi.impl.symbols.SchemeIdentifier;


public abstract class SchemeListBase extends SchemePsiElementBase
{
  public SchemeListBase(@NotNull ASTNode astNode, String name)
  {
    super(astNode, name);
  }

  @Nullable
  public String getHeadText()
  {
    SchemeIdentifier first = findChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    return first.getText();
  }

  public PsiElement getSecondNonLeafElement()
  {
    PsiElement first = getFirstChild();
    while ((first != null) && isWrongElement(first))
    {
      first = first.getNextSibling();
    }
    if (first == null)
    {
      return null;
    }

    PsiElement second = first.getNextSibling();
    while (second != null && isWrongElement(second))
    {
      second = second.getNextSibling();
    }
    return second;
  }
}
