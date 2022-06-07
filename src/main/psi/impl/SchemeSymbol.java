package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.SchemeIcons;
import main.parser.AST;
import main.psi.impl.symbols.CompleteSymbol;
import main.psi.util.SchemePsiElementFactory;
import main.psi.util.SchemePsiUtil;

import javax.swing.Icon;


public class SchemeSymbol extends SchemePsiElementBase  implements PsiReference, PsiNamedElement
{
  public SchemeSymbol(ASTNode node)
  {
    super(node, "SchemeSymbol");
  }


  @Override
  public PsiReference getReference()
  {
    return this;
  }

  @Override
  public String toString()
  {
    return "SchemeSymbol: " + getReferenceName();
  }

  @NotNull
  public PsiElement getElement()
  {
    return this;
  }

  @NotNull
  public TextRange getRangeInElement()
  {
    return new TextRange(0, getTextLength());
  }

  @Nullable
  public String getReferenceName()
  {
    return getText();
  }

  public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException
  {
    ASTNode newNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newName);
    ASTNode thisNode = getNode();
    ASTNode parentNode = thisNode.getTreeParent();
    if (parentNode != null)
    {
      parentNode.replaceChild(thisNode, newNode);
      return newNode.getPsi();
    } else {
      return this;
    }
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
      public String getLocationString()
      {
        String name = getContainingFile().getName();
        //todo show namespace
        return "(in " + name + ")";
      }

      @Nullable
      public Icon getIcon(boolean open)
      {
        return SchemeSymbol.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }
    };
  }

  @Override
  public String getName()
  {
    return getText();
  }

  @NotNull
  public Object[] getVariants()
  {
    return CompleteSymbol.getVariants(this);
  }

  public String getCanonicalText()
  {
    return getText();
  }

  public boolean isSoft()
  {
    return false;
  }

  public boolean isReferenceTo(PsiElement element)
  {
    if (element instanceof SchemeSymbol)
    {
      SchemeSymbol symbol = (SchemeSymbol) element;
      String referenceName = getReferenceName();
      if ((referenceName != null) && referenceName.equals(symbol.getReferenceName()))
      {
        return resolve() == symbol;
      }
    }
    return false;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
  {
    return setName(newElementName);
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
  {
    //todo implement me!
    return this;
  }

  public PsiElement resolve()
  {
    PsiElement bigBrother = this;
    PsiElement declaration;

    while (true)
    {
      bigBrother = SchemePsiUtil.getBigBrother(bigBrother);
      if (null == bigBrother)
      {
        return null;
      }
      if (isItDeclaration(bigBrother))
      {
        declaration = SchemePsiUtil.getNormalChildAt(bigBrother, 1);
        if (null != declaration)
        {
          if (declaration.textMatches(this))
          {
            return declaration;
          }
        }
      }
    }
  }

  private boolean isItDeclaration(PsiElement element)
  {
    if (null == element)
    {
      return false;
    }
    return AST.DEFINE_FORMS.contains(element.getNode().getElementType());
  }
}
