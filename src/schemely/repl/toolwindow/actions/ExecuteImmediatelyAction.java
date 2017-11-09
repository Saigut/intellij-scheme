package schemely.repl.toolwindow.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.IconLoader;
import schemely.repl.SchemeConsole;
import schemely.scheme.REPL;

import javax.swing.*;

/**
 * @author Colin Fleming
 */
public class ExecuteImmediatelyAction extends DumbAwareAction
{
  private static final Icon ACTIONS_EXECUTE_ICON = IconLoader.getIcon("/actions/execute.png");
  private static final String EXECUTE_IMMEDIATELY_ID = "Scheme.REPL.Execute.Immediately";

  private final SchemeConsole console;

  public ExecuteImmediatelyAction(SchemeConsole console)
  {
    super(null, null, ACTIONS_EXECUTE_ICON);
    this.console = console;
    EmptyAction.setupAction(this, EXECUTE_IMMEDIATELY_ID, null);
  }

  @Override
  public void update(AnActionEvent e)
  {
    REPL repl = console.getConsoleEditor().getUserData(REPL.REPL_KEY);
    if (repl != null)
    {
      e.getPresentation().setEnabled(repl.isActive());
    }
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    console.executeCurrent(true);
  }
}
