package schemely.compiler;

import com.intellij.compiler.CompilerSettingsFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;

/**
 * @author Colin Fleming
 */
public class SchemeCompilerSettingsFactory implements CompilerSettingsFactory
{
  public Configurable create(Project project)
  {
    return new SchemeCompilerConfigurable(SchemeCompilerSettings.getInstance(project));
  }
}
