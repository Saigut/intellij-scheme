package schemely.repl.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.psi.util.SchemePsiElementFactory;
import schemely.psi.util.SchemePsiUtil;

public class RunLastSExprAction extends RunActionBase
{
  public RunLastSExprAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  @Override
  public void actionPerformed(AnActionEvent event)
  {
    Editor editor = event.getData(PlatformDataKeys.EDITOR);
    if (editor == null)
    {
      return;
    }

    Project project = editor.getProject();
    if (project == null)
    {
      return;
    }

    PsiElement sexp = SchemePsiUtil.findSexpAtCaret(editor, true);
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
