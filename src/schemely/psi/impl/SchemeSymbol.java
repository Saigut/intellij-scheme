package schemely.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeIcons;
import schemely.lexer.TokenSets;
import schemely.parser.AST;
import schemely.psi.impl.symbols.CompleteSymbol;
import schemely.psi.util.SchemePsiElementFactory;
import schemely.psi.util.SchemePsiUtil;

import javax.swing.*;


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

  public PsiElement getElement()
  {
    return this;
  }

  public TextRange getRangeInElement()
  {
    PsiElement refNameElement = getReferenceNameElement();
    if (refNameElement != null)
    {
      int offsetInParent = refNameElement.getStartOffsetInParent();
      return new TextRange(offsetInParent, offsetInParent + refNameElement.getTextLength());
    }
    return new TextRange(0, getTextLength());
  }

  @Nullable
  public PsiElement getReferenceNameElement()
  {
    ASTNode lastChild = getNode().getLastChildNode();
    if (lastChild == null)
    {
      return null;
    }
    for (IElementType elementType : TokenSets.REFERENCE_NAMES.getTypes())
    {
      if (lastChild.getElementType() == elementType)
      {
        return lastChild.getPsi();
      }
    }

    return null;
  }

  @Nullable
  public String getReferenceName()
  {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null)
    {
      ASTNode node = nameElement.getNode();
      if ((node != null) && (node.getElementType() == AST.AST_BASIC_ELE_SYMBOL))
      {
        return nameElement.getText();
      }
    }
    return null;
  }

  public PsiElement setName(@NotNull @NonNls String newName) throws IncorrectOperationException
  {
    ASTNode newNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newName);
    ASTNode parentNode = getParent().getNode();
    if (parentNode != null)
    {
      parentNode.replaceChild(getNode(), newNode);
    }
    return newNode.getPsi();
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

      @Nullable
      public TextAttributesKey getTextAttributesKey()
      {
        return null;
      }
    };
  }

  @Override
  public String getName()
  {
    return getNameString();
  }

  private boolean isItDeclaration(PsiElement element)
  {
    if (null == element)
    {
      return false;
    }
    return AST.DEFINE_FORMS.contains(element.getNode().getElementType());
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

  public static String id(Object object)
  {
    return object == null ? "null" : Integer.toString(System.identityHashCode(object));
  }

  public String getCanonicalText()
  {
    return null;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
  {
    PsiElement nameElement = getReferenceNameElement();
    if (nameElement != null)
    {
      ASTNode node = nameElement.getNode();
      ASTNode newNameNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newElementName);
      assert newNameNode != null && node != null;
      node.getTreeParent().replaceChild(node, newNameNode);
    }
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
  {
    //todo implement me!
    return this;
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

  @NotNull
  public Object[] getVariants()
  {
    return CompleteSymbol.getVariants(this);
  }

  public boolean isSoft()
  {
    return false;
  }

  @NotNull
  public String getNameString()
  {
    return getText();
  }
}
