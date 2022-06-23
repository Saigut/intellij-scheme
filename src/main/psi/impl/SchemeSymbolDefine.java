package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.IncorrectOperationException;
import main.SchemeIcons;
import main.psi.util.SchemePsiElementFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


public class SchemeSymbolDefine extends SchemePsiElementBase  implements PsiNamedElement
{
  public SchemeSymbolDefine(ASTNode node)
  {
    super(node, "SchemeSymbolDefine");
  }

  @Override
  public String toString()
  {
    return "SchemeSymbolDefine: " + getText();
  }

  @Override
  public Icon getIcon(int flags)
  {
    return SchemeIcons.SYMBOL;
  }

  @Override
  public ItemPresentation getPresentation()
  {
    return new ItemPresentation()
    {
      public String getPresentableText()
      {
        String name = getName();
        return name == null ? "<undefined>" : name;
      }

      @Nullable
      public Icon getIcon(boolean open)
      {
        return SchemeSymbolDefine.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }
    };
  }

  @Override
  public String getName()
  {
    return getText();
  }

  public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException
  {
    ASTNode thisNode = getNode();
    ASTNode newNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newName);
    ASTNode oldNode = thisNode.getFirstChildNode();
    thisNode.replaceChild(oldNode, newNode);
    return this;
  }
}
