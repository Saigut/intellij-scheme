package schemely.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeIcons;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class SchemeConfigurable extends AbstractProjectComponent implements Configurable
{
  protected static final String PROJECT_SETTINGS = "SchemeProjectSettings";
  private SchemeImplementation originalImplementation;

  private volatile Runnable reloadProjectRequest;

  private SchemeProjectSettings settings = null;

  private JPanel panel = null;
  private JComboBox schemeImplementationComboBox;
  private JCheckBox arrowKeysNavigateHistory;

  public SchemeConfigurable(Project project)
  {
    super(project);
    originalImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
  }

  @Override
  @Nls
  public String getDisplayName()
  {
    return "Scheme";
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  @Override
  public String getHelpTopic()
  {
    return null;
  }

  @Override
  public JComponent createComponent()
  {
    if (panel == null)
    {
      settings = SchemeProjectSettings.getInstance(myProject);

      panel = new JPanel();
      panel.setLayout(new MigLayout());

      panel.add(new JLabel("Scheme Implementation: "));
      schemeImplementationComboBox = new JComboBox(SchemeImplementation.values());
      panel.add(schemeImplementationComboBox, "align right, wrap");

      JPanel replPanel = new JPanel();
      replPanel.setLayout(new MigLayout());
      replPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "REPL"));

      arrowKeysNavigateHistory = new JCheckBox("Arrow keys navigate in history");
      replPanel.add(arrowKeysNavigateHistory, "wrap");

      panel.add(replPanel, "growx, span, wrap");

      reset();
    }
    return panel;
  }

  @Override
  public boolean isModified()
  {
    boolean equal = schemeImplementationComboBox.getSelectedItem().equals(settings.schemeImplementation);
    equal = equal && (arrowKeysNavigateHistory.isSelected() == settings.arrowKeysNavigateHistory);
    return !equal;
  }

  @Override
  public void apply() throws ConfigurationException
  {
    SchemeProjectSettings settings = SchemeProjectSettings.getInstance(myProject);
    SchemeImplementation implementation = (SchemeImplementation) schemeImplementationComboBox.getSelectedItem();
    settings.schemeImplementation = implementation;
    settings.arrowKeysNavigateHistory = arrowKeysNavigateHistory.isSelected();

    reloadProjectOnLanguageLevelChange(implementation, false);
  }

  @Override
  public void reset()
  {
    schemeImplementationComboBox.setSelectedItem(settings.schemeImplementation);
    arrowKeysNavigateHistory.setSelected(settings.arrowKeysNavigateHistory);
  }

  @Override
  public void disposeUIResources()
  {
    panel = null;
  }

  @Override
  @NotNull
  public String getComponentName()
  {
    return PROJECT_SETTINGS;
  }

  public void reloadProjectOnLanguageLevelChange(@NotNull SchemeImplementation implementation, final boolean forceReload)
  {
    if (willReload())
    {
      reloadProjectRequest = new Runnable()
      {
        @Override
        public void run()
        {
          if (myProject.isDisposed())
          {
            return;
          }
          if (reloadProjectRequest != this)
          {
            // obsolete, another request has already replaced this one
            return;
          }
          SchemeImplementation currentImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
          if (!forceReload && originalImplementation.equals(currentImplementation)) {
            // the question does not make sense now
            return;
          }
          String message =
            "Scheme implementation changes will take effect on project reload.\nWould you like to reload project \"" +
            myProject.getName() +
            "\" now?";
          if (Messages.showYesNoDialog(myProject,
                                       message,
                                       "Implementation changed",
                                       Messages.getQuestionIcon()) == 0)
          {
            ProjectManager.getInstance().reloadProject(myProject);
          }
          reloadProjectRequest = null;
        }
      };
      ApplicationManager.getApplication().invokeLater(reloadProjectRequest, ModalityState.NON_MODAL);
    }
    else
    {
      // if the project is not open, reset the original implementation to the same value as implementation
      originalImplementation = implementation;
    }
  }

  private boolean willReload()
  {
    return myProject.isOpen() && !ApplicationManager.getApplication().isUnitTestMode();
  }
}