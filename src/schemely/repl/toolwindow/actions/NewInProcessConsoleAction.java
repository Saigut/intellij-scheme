package schemely.repl.toolwindow.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import schemely.SchemeIcons;
import schemely.repl.REPLProvider;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;
import schemely.utils.Actions;

public class NewInProcessConsoleAction extends AnAction implements DumbAware
{
  public NewInProcessConsoleAction()
  {
    getTemplatePresentation().setIcon(SchemeIcons.SCHEME_ICON);
  }

  public void update(AnActionEvent e)
  {
    Module m = Actions.getModule(e);
    Presentation presentation = e.getPresentation();
    if (m == null)
    {
      presentation.setEnabled(false);
      return;
    }

    Scheme scheme = SchemeImplementation.from(m.getProject());
    REPLProvider provider = scheme.getInProcessREPLProvider();
    presentation.setEnabled(provider.isSupported());
    super.update(e);
  }

  public void actionPerformed(AnActionEvent event)
  {
    Module module = Actions.getModule(event);
    assert (module != null) : "Module is null";

    // Find the tool window
    Project project = module.getProject();
    Scheme scheme = SchemeImplementation.from(project);
    REPLProvider provider = scheme.getInProcessREPLProvider();
    if (!provider.isSupported())
    {
      return;
    }

    provider.createREPL(project, module);
  }
}
