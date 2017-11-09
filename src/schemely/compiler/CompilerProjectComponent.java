package schemely.compiler;

import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;

/**
 * @author Colin Fleming
 */
public class CompilerProjectComponent implements ProjectComponent
{
  private final Project project;

  public CompilerProjectComponent(Project project)
  {
    this.project = project;
  }

  @Override
  public void projectOpened()
  {
    CompilerManager compilerManager = CompilerManager.getInstance(project);
    compilerManager.addCompilableFileType(SchemeFileType.SCHEME_FILE_TYPE);

    for (SchemeCompiler compiler : CompilerManager.getInstance(project).getCompilers(SchemeCompiler.class))
    {
      CompilerManager.getInstance(project).removeCompiler(compiler);
    }
    CompilerManager.getInstance(project).addCompiler(new SchemeCompiler(project));
  }

  @Override
  public void projectClosed()
  {
  }

  @NotNull
  @Override
  public String getComponentName()
  {
    return "CompilerProjectComponent";
  }

  @Override
  public void initComponent()
  {
  }

  @Override
  public void disposeComponent()
  {
  }
}
