package schemely.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.psi.util.SchemePsiElementFactory;
import schemely.psi.util.SchemePsiUtil;

public final class RunTopSExprAction extends RunActionBase
{
  public RunTopSExprAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void actionPerformed(AnActionEvent event)
  {
    Editor editor = event.getData(DataKeys.EDITOR);
    if (editor == null)
    {
      return;
    }

    Project project = editor.getProject();
    if (project == null)
    {
      return;
    }

    PsiElement sexp = SchemePsiUtil.findTopSexpAroundCaret(editor);
    if (sexp == null)
    {
      return;
    }

    if (SchemePsiElementFactory.getInstance(project).hasSyntacticalErrors(sexp))
    {
      Messages.showErrorDialog(project,
                               SchemeBundle.message("evaluate.incorrect.sexp"),
                               SchemeBundle.message("evaluate.incorrect.cannot.evaluate"));

      return;
    }

    executeTextRange(editor, sexp.getTextRange());
  }
}
