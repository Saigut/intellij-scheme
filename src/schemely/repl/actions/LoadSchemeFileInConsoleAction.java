package schemely.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import schemely.SchemeIcons;
import schemely.psi.impl.SchemeFile;

public class LoadSchemeFileInConsoleAction extends RunActionBase
{
  public LoadSchemeFileInConsoleAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void actionPerformed(AnActionEvent e)
  {
    Editor editor = e.getData(DataKeys.EDITOR);

    if (editor == null)
    {
      return;
    }
    Project project = editor.getProject();
    if (project == null)
    {
      return;
    }

    Document document = editor.getDocument();
    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if ((psiFile == null) || (!(psiFile instanceof SchemeFile)))
    {
      return;
    }

    VirtualFile virtualFile = psiFile.getVirtualFile();
    if (virtualFile == null)
    {
      return;
    }
    String filePath = virtualFile.getPath();
    if (filePath == null)
    {
      return;
    }

    String command = "(load \"" + filePath + "\")";

    PsiDocumentManager.getInstance(project).commitAllDocuments();
    FileDocumentManager.getInstance().saveAllDocuments();

    executeCommand(project, command);
  }
}
