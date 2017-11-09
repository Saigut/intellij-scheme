package schemely.scheme.common;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.content.Content;
import schemely.repl.SchemeConsole;
import schemely.repl.SchemeConsoleView;
import schemely.repl.toolwindow.actions.ExecuteImmediatelyAction;
import schemely.repl.toolwindow.actions.HistoryNextAction;
import schemely.repl.toolwindow.actions.HistoryPreviousAction;
import schemely.scheme.REPLException;
import schemely.utils.Editors;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public abstract class REPLBase implements schemely.scheme.REPL
{
  private static final Icon STOP_ICON = IconLoader.getIcon("/actions/suspend.png");
  private static final Icon CANCEL_ICON = IconLoader.getIcon("/actions/cancel.png");

  protected final Project project;
  protected final SchemeConsoleView consoleView;

  public REPLBase(SchemeConsoleView consoleView, Project project)
  {
    this.consoleView = consoleView;
    this.project = project;
  }

  @Override
  public abstract void start() throws REPLException;

  @Override
  public abstract void stop();

  @Override
  public abstract void execute(String command);

  @Override
  public abstract boolean isActive();

  @Override
  public SchemeConsoleView getConsoleView()
  {
    return consoleView;
  }

  @Override
  public AnAction[] getToolbarActions()
  {
    SchemeConsole console = consoleView.getConsole();
    return new AnAction[]{new ExecuteImmediatelyAction(console),
                          new StopAction(),
                          new CloseAction(),
                          new HistoryPreviousAction(console),
                          new HistoryNextAction(console)};
  }

  @Override
  public abstract Collection<PsiNamedElement> getSymbolVariants(PsiManager manager, PsiElement symbol);

  protected void hideEditor()
  {
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        SchemeConsole console = consoleView.getConsole();
        JComponent component = consoleView.getComponent();
        Container parent = component.getParent();
        if (parent instanceof JPanel)
        {
          EditorEx historyViewer = console.getHistoryViewer();
          parent.add(historyViewer.getComponent());
          parent.remove(component);
          Editors.scrollDown(historyViewer);
          ((JPanel) parent).updateUI();
        }
      }
    });
  }

  protected void setEditorEnabled(boolean enabled)
  {
    consoleView.getConsole().getConsoleEditor().setRendererMode(!enabled);
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      public void run()
      {
        consoleView.getConsole().getConsoleEditor().getComponent().updateUI();
      }
    });
  }

  private class StopAction extends DumbAwareAction
  {
    private StopAction()
    {
      copyShortcutFrom(ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM));
      Presentation templatePresentation = getTemplatePresentation();
      templatePresentation.setIcon(STOP_ICON);
      templatePresentation.setText("Stop REPL");
      templatePresentation.setDescription(null);
    }

    @Override
    public void update(AnActionEvent e)
    {
      e.getPresentation().setEnabled(isActive());
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
      if (isActive())
      {
        stop();
      }
    }
  }

  private class CloseAction extends DumbAwareAction
  {
    private CloseAction()
    {
      copyShortcutFrom(ActionManager.getInstance().getAction(IdeActions.ACTION_CLOSE));
      Presentation templatePresentation = getTemplatePresentation();
      templatePresentation.setIcon(CANCEL_ICON);
      templatePresentation.setText("Close REPL tab");
      templatePresentation.setDescription(null);
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
      if (isActive())
      {
        stop();
      }

      Content content = consoleView.getConsole().getConsoleEditor().getUserData(CONTENT_KEY);
      if (content != null)
      {
        content.getManager().removeContent(content, true);
      }
    }
  }
}
