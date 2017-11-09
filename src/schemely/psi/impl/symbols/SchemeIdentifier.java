package schemely.psi.impl.symbols;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeIcons;
import schemely.lexer.TokenSets;
import schemely.lexer.Tokens;
import schemely.psi.impl.SchemePsiElementBase;
import schemely.psi.resolve.ResolveUtil;
import schemely.psi.resolve.SchemeResolveResult;
import schemely.psi.resolve.processors.ResolveProcessor;
import schemely.psi.resolve.processors.SymbolResolveProcessor;
import schemely.psi.util.SchemePsiElementFactory;

import javax.swing.*;

public class SchemeIdentifier extends SchemePsiElementBase implements PsiReference, PsiNamedElement
{
  private static final IdentifierResolver RESOLVER = new IdentifierResolver();

  public SchemeIdentifier(ASTNode node)
  {
    super(node, "Identifier");
  }

  @Override
  public PsiReference getReference()
  {
    return this;
  }

  @Override
  public String toString()
  {
    return "SchemeIdentifier: " + getReferenceName();
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
      if ((node != null) && (node.getElementType() == Tokens.IDENTIFIER))
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
        return SchemeIdentifier.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
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

  public PsiElement resolve()
  {
//    return getManager().getResolveCache().resolveWithCaching(this, RESOLVER, false, false);
    return null;
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
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier identifier = (SchemeIdentifier) element;
      String referenceName = getReferenceName();
      if ((referenceName != null) && referenceName.equals(identifier.getReferenceName()))
      {
        return resolve() == identifier;
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

  public static class IdentifierResolver implements ResolveCache.Resolver
  {
    @Override
    public PsiElement resolve(PsiReference psiReference, boolean incompleteCode)
    {
      SchemeIdentifier schemeIdentifier = (SchemeIdentifier) psiReference;
      if (ResolveUtil.getQuotingLevel(schemeIdentifier) != 0)
      {
        return null;
      }

      String name = schemeIdentifier.getReferenceName();
      if (name == null)
      {
        return null;
      }

      ResolveProcessor processor = new SymbolResolveProcessor(name);

      ResolveUtil.resolve(schemeIdentifier, processor);

      SchemeResolveResult[] results = processor.getCandidates();
      return results.length == 1 ? results[0].getElement() : null;
    }
  }
}
