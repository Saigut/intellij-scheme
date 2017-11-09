package schemely.repl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;

/**
 * @author Colin Fleming
 */
public interface REPLProvider
{
  boolean isSupported();

  void createREPL(Project project, Module module);
}
