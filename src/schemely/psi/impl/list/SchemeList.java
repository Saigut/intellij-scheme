package schemely.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.Tokens;
import schemely.psi.api.SchemeBraced;


public class SchemeList extends SchemeListBase implements SchemeBraced
{
  public SchemeList(@NotNull ASTNode astNode)
  {
    super(astNode, "SchemeList");
  }

  @Override
  public String toString()
  {
    return getText();
  }

  @NotNull
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_PAREN);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_PAREN);
  }
}
