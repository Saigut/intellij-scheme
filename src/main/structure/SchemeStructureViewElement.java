package main.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import main.SchemeIcons;
import main.parser.AST;
import org.jetbrains.annotations.NotNull;
import main.psi.util.SchemePsiUtil;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;


public class SchemeStructureViewElement implements StructureViewTreeElement, ItemPresentation, SortableTreeElement
{
  private final NavigatablePsiElement element;
  private final NavigatablePsiElement nameChild;

  public SchemeStructureViewElement(NavigatablePsiElement element)
  {
    nameChild = (NavigatablePsiElement)getDeclareNameChild(element);
    this.element = element;
  }

  public PsiElement getValue()
  {
    return nameChild;
  }

  public void navigate(boolean requestFocus)
  {
    nameChild.navigate(requestFocus);
  }

  public boolean canNavigate()
  {
    return nameChild.canNavigate();
  }

  public boolean canNavigateToSource()
  {
    return nameChild.canNavigateToSource();
  }

  @NotNull
  @Override
  public String getAlphaSortKey() {
    return nameChild != null ? nameChild.getText() : "";
  }
  private boolean isDeclarationFrom(PsiElement element) {
    return AST.DEFINE_FORMS.contains(element.getNode().getElementType());
  }

  private PsiElement getDeclareNameChild(PsiElement element) {
    return SchemePsiUtil.getNormalChildAt(element, 1);
  }

  @NotNull
  public StructureViewTreeElement[] getChildren()
  {
    final List<SchemeStructureViewElement> childrenElements = new ArrayList<>();

    PsiElement child = element.getFirstChild();
    if (child == null) {
      return EMPTY_ARRAY;
    }
    if (isDeclarationFrom(child)) {
      PsiElement nameChild = getDeclareNameChild(child);
      if (nameChild != null) {
        childrenElements.add(new SchemeStructureViewElement((NavigatablePsiElement)child));
      }
    }

    while (true) {
      child = child.getNextSibling();
      if (child == null) {
        break;
      }
      if (isDeclarationFrom(child)) {
        PsiElement nameChild = getDeclareNameChild(child);
        if (nameChild != null) {
          childrenElements.add(new SchemeStructureViewElement((NavigatablePsiElement)child));
        }
      }
    }

    return childrenElements.toArray(new SchemeStructureViewElement[0]);
  }

  @Override
  public String getLocationString()
  {
    return null;
  }

  @Override
  public Icon getIcon(boolean open)
  {
    return nameChild == null ?
            SchemeIcons.SCHEME_ICON :
            nameChild.getIcon(Iconable.ICON_FLAG_VISIBILITY);
  }

  @Override
  public String getPresentableText()
  {
    return nameChild == null ? "" : nameChild.getText();
  }

  @Override
  @NotNull
  public ItemPresentation getPresentation()
  {
    return this;
  }
}