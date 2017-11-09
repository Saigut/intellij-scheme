package schemely.compiler;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author Colin Fleming
 */
@State(
  name = "SchemeCompilerSettings",
  storages = {
    @Storage(id = "default", file = "$PROJECT_FILE$")
   ,@Storage(id = "dir", file = "$PROJECT_CONFIG_DIR$/scheme_compiler.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class SchemeCompilerSettings implements PersistentStateComponent<SchemeCompilerSettings>, ProjectComponent
{
  public String prefix = "";
  public boolean modulesDefaultToStatic = true;
  public boolean warnUnknownMember = true;
  public boolean warnInvokeUnknownMethod = true;
  public boolean warnUndefinedVariable = true;
  public boolean warningsAsErrors = false;

  @NotNull
  @Override
  public String getComponentName()
  {
    return "SchemeCompilerSettings";
  }

  @Override
  public void initComponent()
  {
  }

  @Override
  public void disposeComponent()
  {
  }

  @Override
  public SchemeCompilerSettings getState()
  {
    return this;
  }

  @Override
  public void loadState(SchemeCompilerSettings state)
  {
    XmlSerializerUtil.copyBean(state, this);
  }

  @Override
  public void projectOpened()
  {
  }

  @Override
  public void projectClosed()
  {
  }

  public static SchemeCompilerSettings getInstance(Project project) {
    return project.getComponent(SchemeCompilerSettings.class);
  }
}
