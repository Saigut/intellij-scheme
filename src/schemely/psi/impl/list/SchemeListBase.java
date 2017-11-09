package schemely.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;
import schemely.psi.impl.SchemePsiElementBase;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.util.SchemePsiUtil;


public abstract class SchemeListBase extends SchemePsiElementBase
{
  public SchemeListBase(@NotNull ASTNode astNode, String name)
  {
    super(astNode, name);
  }

  @Nullable
  public String getPresentableText()
  {
    SchemeIdentifier first = findChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    String text1 = getHeadText();
    PsiElement next = SchemePsiUtil.findNextSiblingByClass(first, SchemeIdentifier.class);
    if (next == null)
    {
      return text1;
    }
    else
    {
      return text1 + " " + next.getText();
    }
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

  public boolean isImproper()
  {
    PsiElement dot = findChildByType(Tokens.DOT);
    return dot != null;
  }

  @Nullable
  public SchemeIdentifier getFirstIdentifier()
  {
    PsiElement child = getFirstChild();
    while (child instanceof LeafPsiElement)
    {
      child = child.getNextSibling();
    }
    if (child instanceof SchemeIdentifier)
    {
      return (SchemeIdentifier) child;
    }
    return null;
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

  public SchemeIdentifier[] getAllIdentifiers()
  {
    return findChildrenByClass(SchemeIdentifier.class);
  }

  public SchemeList[] getSubLists()
  {
    return findChildrenByClass(SchemeList.class);
  }
}
