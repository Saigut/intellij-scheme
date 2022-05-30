package schemely.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.Tokens;
import schemely.psi.api.SchemeBraced;


public class SchemeVector extends SchemePsiElementBase implements SchemeBraced
{
  public SchemeVector(ASTNode node)
  {
    super(node, "SchemeVector");
  }

  @NotNull
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_SQUARE);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_SQUARE);
  }
}
