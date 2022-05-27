package schemely.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StorageScheme;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import schemely.scheme.SchemeImplementation;


@State(
  name = SchemeConfigurable.PROJECT_SETTINGS,
  storages = {@Storage(file = "$PROJECT_FILE$"), @Storage(file = "$PROJECT_CONFIG_DIR$/scheme_project.xml")})
public class SchemeProjectSettings implements PersistentStateComponent<SchemeProjectSettings>
{
  public SchemeImplementation schemeImplementation = SchemeImplementation.SISC_1_16_6;
  public boolean arrowKeysNavigateHistory = false;

  @Override
  public SchemeProjectSettings getState()
  {
    return this;
  }

  @Override
  public void loadState(SchemeProjectSettings state)
  {
    XmlSerializerUtil.copyBean(state, this);
  }

  public static SchemeProjectSettings getInstance(Project project)
  {
    return ServiceManager.getService(project, SchemeProjectSettings.class);
  }
}
