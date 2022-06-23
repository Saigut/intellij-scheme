package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.IncorrectOperationException;
import main.psi.util.SchemePsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.SchemeIcons;
import main.parser.AST;
import main.psi.impl.symbols.CompleteSymbol;
import main.psi.util.SchemePsiElementFactory;

import javax.swing.Icon;


public class SchemeSymbol extends SchemePsiElementBase  implements PsiReference
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
        return SchemeSymbol.this.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
      }
    };
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

  public boolean isReferenceTo(@NotNull PsiElement element)
  {
    if (element instanceof SchemeSymbolDefine)
    {
      SchemeSymbolDefine symbol = (SchemeSymbolDefine)element;
      String referenceName = getReferenceName();
      if ((referenceName != null) && referenceName.equals(symbol.getName()))
      {
        return resolve() == symbol;
      }
    }
    return false;
  }

  public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException
  {
    ASTNode thisNode = getNode();
    ASTNode newNode = SchemePsiElementFactory.getInstance(getProject()).createSymbolNodeFromText(newElementName);
    ASTNode oldNode = thisNode.getFirstChildNode();
    thisNode.replaceChild(oldNode, newNode);
    return this;
  }

  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException
  {
    //todo implement me!
    return this;
  }

  public PsiElement resolve()
  {
    PsiElement brother;
    brother = this.getPrevSibling();
    while (brother != null)
    {
      if (brother instanceof SchemeFormDefineBase) {
        PsiElement defPsi = ((SchemeFormDefineBase)brother).getDeclareName();
        if (defPsi != null && defPsi.textMatches(this)) {
          return defPsi;
        }
      }
      brother = brother.getPrevSibling();
    }

    PsiElement parent;
    PsiElement cur_ele = this;
    parent = this.getParent();
    while (parent != null) {
      if (parent instanceof PsiFile) {
        return null;
      }
      if (!(cur_ele instanceof SchemeInFormParamListLetInner)) {
        if (parent instanceof SchemeFormLetBase) {
          PsiElement find = findInLetForm((SchemeFormLetBase) parent, this);
          if (find != null) {
            return find;
          }
        } else if (parent instanceof SchemeFormDefine) {
          PsiElement find = findInDefineForm((SchemeFormDefine)parent, this);
          if (find != null) {
            return find;
          }
        } else if (parent instanceof SchemeFormDo) {
          PsiElement find = findInDoForm((SchemeFormDo)parent, this);
          if (find != null) {
            return find;
          }
        } else if (parent instanceof SchemeFormProcedure) {
          PsiElement find = findInProcedure((SchemeFormProcedure)parent, this);
          if (find != null) {
            return find;
          }
        } else {
          if (parent instanceof SchemeFormDefineBase) {
            PsiElement dec = ((SchemeFormDefineBase)parent).getDeclareName();
            if (dec != null && dec.textMatches(this)) {
              return dec;
            }
          }
          if (parent instanceof SchemeFormLocalBase) {
            PsiElement[] defs = ((SchemeFormLocalBase)parent).getLocalDefinitions();
//        System.out.println("my text: " + this.getText());
            for (PsiElement def : defs) {
//          System.out.println("localDefinition: " + def.getText());
              if (def.textMatches(this)) {
                return def;
              }
            }
          }
        }
      }

      brother = parent.getPrevSibling();
      while (brother != null)
      {
        if (brother instanceof SchemeFormDefineBase) {
          PsiElement defPsi = ((SchemeFormDefineBase)brother).getDeclareName();
          if (defPsi != null && defPsi.textMatches(this)) {
            return defPsi;
          }
        }
        brother = brother.getPrevSibling();
      }

      cur_ele = parent;
      if (cur_ele instanceof SchemeInFormParamListLetInner) {
        parent = cur_ele.getParent();
        if (parent == null) {
          return null;
        }
      }
      parent = parent.getParent();
    }
    return null;
  }

  private PsiElement findInDefineForm(SchemeFormDefine form, PsiElement toFind) {
    ASTNode node = form.getNode();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return null;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      if (toFind.textMatches(defNode.getPsi())) {
        return defNode.getPsi();
      }
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return null;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      if (toFind.textMatches(defNode.getPsi())) {
        return defNode.getPsi();
      }
      ASTNode localDefinition;
      localDefinition = defNode.getTreeNext();
      while (localDefinition != null) {
        if (localDefinition.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
          if (toFind.textMatches(localDefinition.getPsi())) {
            return localDefinition.getPsi();
          }
        }
        localDefinition = localDefinition.getTreeNext();
      }
    }
    return null;
  }

  private PsiElement findInDoForm(SchemeFormDo form, PsiElement toFind)
  {
    ASTNode node = form.getNode();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return null;
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType != AST.AST_BODY_IN_FORM_PARAM_LIST) {
      return null;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return null;
    }
    while (defNode != null) {
      ASTNode localDefinition = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
      if (localDefinition != null && localDefinition.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
        if (toFind.textMatches(localDefinition.getPsi())) {
          return localDefinition.getPsi();
        }
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
    }
    return null;
  }

  private PsiElement findInLetForm(SchemeFormLetBase form, PsiElement toFind)
  {
    ASTNode node = form.getNode();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return null;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      if (toFind.textMatches(defNode.getPsi())) {
        return defNode.getPsi();
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
      if (defNode == null) {
        return null;
      }
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType != AST.AST_BODY_IN_FORM_PARAM_LIST) {
      return null;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return null;
    }
    while (defNode != null) {
      ASTNode localDefinition = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
      if (localDefinition != null && localDefinition.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
        if (toFind.textMatches(localDefinition.getPsi())) {
          return localDefinition.getPsi();
        }
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
    }
    return null;
  }

  private PsiElement findInProcedure(SchemeFormProcedure form, PsiElement toFind) {
    ASTNode node = form.getNode();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return null;
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      if (toFind.textMatches(defNode.getPsi())) {
        return defNode.getPsi();
      }
    }
    if (defNodeType != AST.AST_BODY_IN_FORM_PARAM_LIST) {
      return null;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return null;
    }
    while (defNode != null) {
      if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
        if (toFind.textMatches(defNode.getPsi())) {
          return defNode.getPsi();
        }
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
    }
    return null;
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
