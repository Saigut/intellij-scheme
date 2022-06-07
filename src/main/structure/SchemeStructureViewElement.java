package main.structure;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import main.parser.AST;
import org.jetbrains.annotations.NotNull;
import main.psi.util.SchemePsiUtil;

import java.util.ArrayList;
import java.util.List;


public class SchemeStructureViewElement implements StructureViewTreeElement, SortableTreeElement
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
    return element;
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

  @NotNull
  @Override
  public ItemPresentation getPresentation() {
    ItemPresentation presentation = nameChild.getPresentation();
    return presentation != null ? presentation : new PresentationData();
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

  private boolean isDeclarationFrom(PsiElement element) {
    return AST.DEFINE_FORMS.contains(element.getNode().getElementType());
  }

  private PsiElement getDeclareNameChild(PsiElement element) {
    return SchemePsiUtil.getNormalChildAt(element, 1);
  }
}