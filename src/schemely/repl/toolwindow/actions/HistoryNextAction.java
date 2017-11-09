package schemely.repl.toolwindow.actions;

import schemely.repl.ConsoleHistoryModel;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.EmptyAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.util.IconLoader;
import schemely.repl.SchemeConsole;
import schemely.scheme.REPL;

import javax.swing.*;

/**
 * @author Colin Fleming
 */
public class HistoryNextAction extends DumbAwareAction
{
  private static final Icon ACTIONS_NEXT_ICON = IconLoader.getIcon("/actions/nextOccurence.png");
  private static final String HISTORY_NEXT_ID = "Scheme.REPL.History.Next";

  private final SchemeConsole console;

  public HistoryNextAction(SchemeConsole console)
  {
    super(null, null, ACTIONS_NEXT_ICON);
    this.console = console;
    EmptyAction.setupAction(this, HISTORY_NEXT_ID, null);
  }

  @Override
  public void update(AnActionEvent e)
  {
    REPL repl = console.getConsoleEditor().getUserData(REPL.REPL_KEY);
    if (repl != null)
    {
      boolean active = repl.isActive();
      ConsoleHistoryModel model = console.getHistoryModel();
      active = active && model.hasNextHistory();
      e.getPresentation().setEnabled(active);
    }
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    ConsoleHistoryModel model = console.getHistoryModel();
    final String prev = model.getHistoryNext();
    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      @Override
      public void run()
      {
        if (prev == null)
        {
          console.restoreCurrentREPLItem();
        }
        else
        {
          console.getEditorDocument().setText(prev);
          console.getCurrentEditor().getCaretModel().moveToOffset(prev.length());
        }
      }
    });
  }
}
