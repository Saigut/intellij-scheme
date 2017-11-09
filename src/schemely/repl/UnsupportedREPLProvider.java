package schemely.repl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * @author Colin Fleming
 */
public class UnsupportedREPLProvider implements REPLProvider
{
  @Override
  public boolean isSupported()
  {
    return false;
  }

  @Override
  public void createREPL(Project project, Module module)
  {
    throw new UnsupportedOperationException();
  }
}
