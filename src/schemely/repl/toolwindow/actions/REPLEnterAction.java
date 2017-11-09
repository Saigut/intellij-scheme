package schemely.repl.toolwindow.actions;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.DumbAware;
import schemely.scheme.REPL;

/**
 * @author Colin Fleming
 */
public class REPLEnterAction extends EditorWriteActionHandler implements DumbAware
{
  private final EditorActionHandler originalHandler;

  public REPLEnterAction(EditorActionHandler originalHandler)
  {
    this.originalHandler = originalHandler;
  }

  @Override
  public void executeWriteAction(Editor editor, DataContext dataContext)
  {
    REPL repl = editor.getUserData(REPL.REPL_KEY);
    if (repl == null)
    {
      originalHandler.execute(editor, dataContext);
    }
    else if (!repl.getConsoleView().getConsole().executeCurrent(false))
    {
      originalHandler.execute(editor, dataContext);
    }
  }

  @Override
  public boolean isEnabled(Editor editor, DataContext dataContext)
  {
    return originalHandler.isEnabled(editor, dataContext);
  }
}
