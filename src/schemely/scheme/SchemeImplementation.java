package schemely.scheme;

import com.intellij.openapi.project.Project;
import schemely.scheme.sisc.SISCScheme;
import schemely.settings.SchemeProjectSettings;

/**
 * @author Colin Fleming
 */
public enum SchemeImplementation
{
  SISC_1_16_6("Bundled SISC 1.16.6", new SISCScheme());

  private final String description;
  private final Scheme scheme;

  SchemeImplementation(String description, Scheme scheme)
  {
    this.description = description;
    this.scheme = scheme;
  }

  @Override
  public String toString()
  {
    return description;
  }

  public static Scheme from(Project project)
  {
    SchemeProjectSettings settings = SchemeProjectSettings.getInstance(project);
    return settings.schemeImplementation.scheme;
  }
}
