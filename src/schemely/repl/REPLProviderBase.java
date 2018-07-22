package schemely.repl;

import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.runners.AbstractConsoleRunnerWithHistory;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import schemely.repl.toolwindow.REPLToolWindowFactory;
import schemely.scheme.REPL;
import schemely.scheme.REPLException;
import schemely.settings.SchemeProjectSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Colin Fleming
 */
public abstract class REPLProviderBase implements REPLProvider
{
  public static final String CONSOLE_TITLE = "Console title";

  @Override
  public abstract boolean isSupported();

  public abstract REPL newREPL(Project project, Module module, SchemeConsoleView consoleView, String workingDir);

  protected abstract String getTabName();

  @Override
  public void createREPL(final Project project, Module module)
  {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(REPLToolWindowFactory.TOOL_WINDOW_ID);
    assert (toolWindow != null) : "ToolWindow is null";

    // Create the console
    ConsoleHistoryModel history = new ConsoleHistoryModel();
    SchemeConsoleView consoleView = new SchemeConsoleView(project, CONSOLE_TITLE, history);
    final SchemeConsole schemeConsole = consoleView.getConsole();

    // Create toolbar
    DefaultActionGroup toolbarActions = new DefaultActionGroup();
    ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("unknown", toolbarActions, false);

    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(Color.WHITE);
    panel.add(actionToolbar.getComponent(), "West");
    panel.add(consoleView.getComponent(), "Center");

    // TODO
    String workingDir = ModuleRootManager.getInstance(module).getContentRoots()[0].getPath();

    REPL repl = newREPL(project, module, consoleView, workingDir);

    AnAction[] actions;
    try
    {
      repl.start();
      actions = getToolbarActions(repl);
    }
    catch (REPLException e)
    {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), CONSOLE_TITLE, null);
      return;
    }

    toolbarActions.addAll(actions);

    registerActionShortcuts(actions, schemeConsole.getConsoleEditor().getComponent());
    registerActionShortcuts(actions, panel);
    panel.updateUI();

    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    final Content content = contentFactory.createContent(panel, getTabName(), false);
    final ContentManager contentManager = toolWindow.getContentManager();
    contentManager.addContent(content);
    content.putUserData(REPL.REPL_KEY, repl);
    schemeConsole.getConsoleEditor().putUserData(REPL.REPL_KEY, repl);
    schemeConsole.getConsoleEditor().putUserData(REPL.CONTENT_KEY, content);
    schemeConsole.getFile().putCopyableUserData(REPL.REPL_KEY, repl);

    if (toolWindow.isActive())
    {
      contentManager.addContent(content);
      contentManager.setSelectedContent(content);
    }
    else
    {
      toolWindow.activate(new Runnable()
      {
        @Override
        public void run()
        {
          contentManager.addContent(content);
          contentManager.setSelectedContent(content);
        }
      });
    }

    toolWindow.show(new Runnable()
    {
      @Override
      public void run()
      {
        IdeFocusManager focusManager = IdeFocusManager.getInstance(project);
        focusManager.requestFocus(schemeConsole.getCurrentEditor().getContentComponent(), true);
      }
    });
  }

  private AnAction[] getToolbarActions(REPL repl) throws REPLException
  {
    java.util.List<AnAction> actions = new ArrayList<AnAction>();
    actions.addAll(Arrays.asList(repl.getToolbarActions()));

    SchemeConsole console = repl.getConsoleView().getConsole();
    EditorEx consoleEditor = console.getConsoleEditor();

//    Computable<Boolean> upComputable = AbstractConsoleRunnerWithHistory.createCanMoveUpComputable(consoleEditor);
//    actions.add(createConsoleHistoryAction(upComputable, console, true, KeyEvent.VK_UP));
//    Computable<Boolean> downComputable = AbstractConsoleRunnerWithHistory.createCanMoveDownComputable(consoleEditor);
//    actions.add(createConsoleHistoryAction(downComputable, console, false, KeyEvent.VK_DOWN));

    return actions.toArray(new AnAction[actions.size()]);
  }

  public static void registerActionShortcuts(AnAction[] actions, JComponent component)
  {
    for (AnAction action : actions)
    {
      if (action.getShortcutSet() != null)
      {
        action.registerCustomShortcutSet(action.getShortcutSet(), component);
      }
    }
  }

  public static AnAction createConsoleHistoryAction(final Computable<Boolean> canMoveInEditor,
                                                    final SchemeConsole console,
                                                    final boolean previous,
                                                    int keyEvent)
  {
    AnAction action = new AnAction()
    {
      @Override
      public void actionPerformed(AnActionEvent e)
      {
        ConsoleHistoryModel historyModel = console.getHistoryModel();
        if (previous && historyModel.isEditingCurrentItem())
        {
          console.saveCurrentREPLItem();
        }

        final String text = previous ? historyModel.getHistoryPrev() : historyModel.getHistoryNext();
        new WriteCommandAction(console.getProject(), console.getFile())
        {
          @Override
          protected void run(Result result) throws Throwable
          {
            if (!previous && (text == null))
            {
              console.restoreCurrentREPLItem();
            }
            else
            {
              console.getEditorDocument().setText(text == null ? "" : text);
              console.getCurrentEditor().getCaretModel().moveToOffset(text == null ? 0 : text.length());
            }
          }
        }.execute();
      }

      @Override
      public void update(AnActionEvent e)
      {
        // Check if we have anything in history
        ConsoleHistoryModel historyModel = console.getHistoryModel();
        boolean enabled = previous ? historyModel.hasPreviousHistory() : historyModel.hasNextHistory();

        // Check if user has history navigation with arrow keys configured
        enabled = enabled && SchemeProjectSettings.getInstance(console.getProject()).arrowKeysNavigateHistory;

        if (!enabled)
        {
          e.getPresentation().setEnabled(false);
          return;
        }

        e.getPresentation().setEnabled(!canMoveInEditor.compute());
      }
    };
    action.registerCustomShortcutSet(keyEvent, 0, null);
    action.getTemplatePresentation().setVisible(false);
    return action;
  }
}
