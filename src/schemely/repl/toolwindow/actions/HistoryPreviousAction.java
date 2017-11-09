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
public class HistoryPreviousAction extends DumbAwareAction
{
  private static final Icon ACTIONS_PREVIOUS_ICON = IconLoader.getIcon("/actions/previousOccurence.png");
  private static final String HISTORY_PREVIOUS_ID = "Scheme.REPL.History.Previous";

  private final SchemeConsole console;

  public HistoryPreviousAction(SchemeConsole console)
  {
    super(null, null, ACTIONS_PREVIOUS_ICON);
    this.console = console;
    EmptyAction.setupAction(this, HISTORY_PREVIOUS_ID, null);
  }

  @Override
  public void update(AnActionEvent e)
  {
    REPL repl = console.getConsoleEditor().getUserData(REPL.REPL_KEY);
    if (repl != null)
    {
      boolean active = repl.isActive();
      ConsoleHistoryModel model = console.getHistoryModel();
      active = active && model.hasPreviousHistory();
      e.getPresentation().setEnabled(active);
    }
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    ConsoleHistoryModel model = console.getHistoryModel();
    if (model.isEditingCurrentItem())
    {
      console.saveCurrentREPLItem();
    }

    final String next = model.getHistoryPrev();
    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      @Override
      public void run()
      {
        console.getEditorDocument().setText(next == null ? "" : next);
        console.getCurrentEditor().getCaretModel().moveToOffset(next == null ? 0 : next.length());
      }
    });
  }
}
