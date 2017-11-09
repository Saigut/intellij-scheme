package schemely.repl.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;

import java.awt.*;

/**
 * @author Colin Fleming
 */
public class REPLToolWindowFactory implements ToolWindowFactory
{
  public static final String TOOL_WINDOW_ID = "Scheme.REPL";

  @Override
  public void createToolWindowContent(Project project, ToolWindow toolWindow)
  {
    // By default tool window has no content
    toolWindow.setToHideOnEmptyContent(true);
    toolWindow.getComponent().setForeground(Color.WHITE);
  }
}
