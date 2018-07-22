package schemely.structure;

import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;


public class SchemeStructureViewBuilderFactory implements PsiStructureViewFactory
{
  public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile)
  {
    return new TreeBasedStructureViewBuilder()
    {
      @NotNull
      public StructureViewModel createStructureViewModel(Editor editor)
      {
        return new SchemeStructureViewModel(psiFile);
      }

      public boolean isRootNodeShown()
      {
        return false;
      }
    };
  }
}