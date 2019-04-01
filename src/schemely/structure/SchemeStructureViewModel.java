package schemely.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.TextEditorBasedStructureViewModel;
import com.intellij.ide.util.treeView.smartTree.Filter;
import com.intellij.ide.util.treeView.smartTree.Grouper;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemeSymbol;


public class SchemeStructureViewModel extends TextEditorBasedStructureViewModel
{
  private StructureViewTreeElement myRoot;
  private PsiFile myFile;
  private Editor myEditor;
  private DocumentListener myDocumentListener;
  private Disposable myEditorDocumentListenerDisposable;

  public SchemeStructureViewModel(Editor editor, PsiFile file)
  {
    super(editor, file);
    myFile = file;
    myEditor = editor;

    myRoot = new SchemeStructureViewElement(file);

    myDocumentListener = new DocumentListener()
    {
      @Override
      public void documentChanged(DocumentEvent event)
      {
        if (event.getDocument().equals(myEditor.getDocument())) {
          fireModelUpdate();
        }
      }
    };

    if (null != myEditor)
    {
      myEditorDocumentListenerDisposable = Disposer.newDisposable();
      EditorFactory.getInstance().getEventMulticaster().addDocumentListener(myDocumentListener, myEditorDocumentListenerDisposable);
    }
  }

  @Override
  public void dispose() {

    if (null != myEditorDocumentListenerDisposable)
    {
      Disposer.dispose(myEditorDocumentListenerDisposable);
      myEditorDocumentListenerDisposable = null;
    }

    super.dispose();
    if (myEditorDocumentListenerDisposable != null) {
      Disposer.dispose(myEditorDocumentListenerDisposable);
    }
  }

  @NotNull
  public StructureViewTreeElement getRoot()
  {
    return myRoot;
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
    return myFile;
  }

  @NotNull
  protected Class[] getSuitableClasses()
  {
    return new Class[] { SchemeSymbol.class };
  }
}
