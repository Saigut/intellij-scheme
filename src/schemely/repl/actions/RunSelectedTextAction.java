package schemely.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.psi.util.SchemePsiElementFactory;

public class RunSelectedTextAction extends RunActionBase
{
  public RunSelectedTextAction()
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
    SelectionModel selectionModel = editor.getSelectionModel();
    String selectedText = selectionModel.getSelectedText();
    if ((selectedText == null) || (selectedText.trim().length() == 0))
    {
      return;
    }
    String text = selectedText.trim();
    Project project = editor.getProject();

    if (SchemePsiElementFactory.getInstance(project).hasSyntacticalErrors(text))
    {
      Messages.showErrorDialog(project,
                               SchemeBundle.message("evaluate.incorrect.form"),
                               SchemeBundle.message("evaluate.incorrect.cannot.evaluate", new Object[0]));

      return;
    }

    executeTextRange(editor, new TextRange(selectionModel.getSelectionStart(), selectionModel.getSelectionEnd()));
  }
}
