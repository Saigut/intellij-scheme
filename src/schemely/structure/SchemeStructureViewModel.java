package schemely.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.symbols.SchemeIdentifier;


public class SchemeStructureViewModel extends TextEditorBasedStructureViewModel
{
  private PsiFile file;

  public SchemeStructureViewModel(PsiFile file)
  {
    super(file);
    this.file = file;
  }

  @NotNull
  public StructureViewTreeElement getRoot()
  {
    return new SchemeStructureViewElement(file);
  }

  @NotNull
  public Grouper[] getGroupers()
  {
    return Grouper.EMPTY_ARRAY;
  }

  @NotNull
  public Sorter[] getSorters()
  {
    return new Sorter[] { Sorter.ALPHA_SORTER };
  }

  @NotNull
  public Filter[] getFilters()
  {
    return Filter.EMPTY_ARRAY;
  }

  protected PsiFile getPsiFile()
  {
    return file;
  }

  @NotNull
  protected Class[] getSuitableClasses()
  {
    return new Class[] { SchemeIdentifier.class };
  }
}
