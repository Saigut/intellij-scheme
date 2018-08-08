package schemely.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemePsiElementBase;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.util.SchemePsiUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;


public class SchemeStructureViewElement implements StructureViewTreeElement, ItemPresentation
{
  private PsiElement element;

  public SchemeStructureViewElement(PsiElement element)
  {
    this.element = element;
  }

  public PsiElement getValue()
  {
    return element;
  }

  public void navigate(boolean requestFocus)
  {
    ((NavigationItem) element).navigate(requestFocus);
  }

  public boolean canNavigate()
  {
    return ((NavigationItem) element).canNavigate();
  }

  public boolean canNavigateToSource()
  {
    return ((NavigationItem) element).canNavigateToSource();
  }

  @NotNull
  public StructureViewTreeElement[] getChildren()
  {
    final List<SchemePsiElementBase> childrenElements = new ArrayList<SchemePsiElementBase>();
    element.acceptChildren(new PsiElementVisitor()
    {
      public void visitElement(PsiElement element)
      {
        if (isBrowsableElement(element))
        {
          childrenElements.add((SchemePsiElementBase) element);
        }
        else
        {
          element.acceptChildren(this);
        }
      }
    });

    StructureViewTreeElement[] children = new StructureViewTreeElement[childrenElements.size()];
    for (int i = 0; i < children.length; i++)
    {
      children[i] = new SchemeStructureViewElement(childrenElements.get(i));
    }

    return children;
  }

  private boolean isBrowsableElement(PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier identifier = (SchemeIdentifier) element;

      PsiElement parentElement = identifier.getParent();
      if (!(parentElement instanceof SchemeList))
      {
        return false;
      }

      SchemeList parent = (SchemeList) parentElement;
      PsiElement tmpEle;
      tmpEle = SchemePsiUtil.getNormalChildAt(parent, 0);
      if (null == tmpEle)
      {
        return false;
      }
      if (!tmpEle.textMatches("define"))
      {
        return false;
      }

      tmpEle = SchemePsiUtil.getNormalChildAt(parent, 1);
      if (null == tmpEle)
      {
        return false;
      }

      return tmpEle.textMatches(element);
    }

    return false;
  }

  @Override
  public String getLocationString()
  {
    return null;
  }

  @Override
  public Icon getIcon(boolean open)
  {
    return element.getIcon(Iconable.ICON_FLAG_OPEN);
  }

  @Override
  public String getPresentableText()
  {
    return ((PsiNamedElement) element).getName();
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation()
  {
    return this;
  }
}