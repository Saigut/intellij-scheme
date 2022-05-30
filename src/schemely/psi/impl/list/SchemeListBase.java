package schemely.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
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
