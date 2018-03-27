package schemely.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import icons.SchemeIcons;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import schemely.scheme.SchemeImplementation;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.util.Objects;

public class SchemeConfigurable extends AbstractProjectComponent implements Configurable {
	protected static final String PROJECT_SETTINGS = "SchemeProjectSettings";
	private SchemeImplementation originalImplementation;

	private volatile Runnable reloadProjectRequest;

	private SchemeProjectSettings settings = null;

	private JPanel panel = null;
	private ComboBox schemeImplementationComboBox;
	private JBCheckBox arrowKeysNavigateHistory;

	public SchemeConfigurable(Project project) {
		super(project);
		originalImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
	}

	@Override @Nls public String getDisplayName() {
		return "Scheme";
	}

	public Icon getIcon() {
		return SchemeIcons.SCHEME_ICON;
	}

	@Override public String getHelpTopic() {
		return null;
	}

	@Override public JComponent createComponent() {
		if (panel != null) return panel;
		settings = SchemeProjectSettings.getInstance(myProject);

		panel = new JPanel();
		panel.setLayout(new MigLayout());

		panel.add(new JLabel("Scheme Implementation: "));
		schemeImplementationComboBox = new ComboBox<>(SchemeImplementation.values());
		panel.add(schemeImplementationComboBox, "align right, wrap");

		JPanel replPanel = new JPanel();
		replPanel.setLayout(new MigLayout());
		replPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "REPL"));

		arrowKeysNavigateHistory = new JBCheckBox("Arrow keys navigate in history");
		replPanel.add(arrowKeysNavigateHistory, "wrap");

		panel.add(replPanel, "growx, span, wrap");

		reset();
		return panel;
	}

	@Override public boolean isModified() {
		return !(Objects.equals(schemeImplementationComboBox.getSelectedItem(), settings.schemeImplementation) &&
				(arrowKeysNavigateHistory.isSelected() == settings.arrowKeysNavigateHistory));
	}

	@Override public void apply() throws ConfigurationException {
		SchemeProjectSettings settings = SchemeProjectSettings.getInstance(myProject);
		SchemeImplementation implementation = (SchemeImplementation) schemeImplementationComboBox.getSelectedItem();
		if (implementation == null) throw new ConfigurationException("implementation is null!");
		settings.schemeImplementation = implementation;
		settings.arrowKeysNavigateHistory = arrowKeysNavigateHistory.isSelected();

		reloadProjectOnLanguageLevelChange(implementation, false);
	}

	@Override public void reset() {
		schemeImplementationComboBox.setSelectedItem(settings.schemeImplementation);
		arrowKeysNavigateHistory.setSelected(settings.arrowKeysNavigateHistory);
	}

	@Override public void disposeUIResources() {
		panel = null;
	}

	@Override @NotNull public String getComponentName() {
		return PROJECT_SETTINGS;
	}

	private void reloadProjectOnLanguageLevelChange(
			@NotNull SchemeImplementation implementation, final boolean forceReload) {
		if (willReload()) {
			//noinspection NonAtomicOperationOnVolatileField
			reloadProjectRequest = () -> {
				if (myProject.isDisposed() || reloadProjectRequest != this) return;
				SchemeImplementation currentImplementation = SchemeProjectSettings.getInstance(myProject).schemeImplementation;
				// the question does not make sense now
				if (forceReload || !originalImplementation.equals(currentImplementation)) {
					if (Messages.showYesNoDialog(myProject,
							"Scheme implementation changes will take effect on project reload.\nWould you like to reload project \"" +
									myProject.getName() +
									"\" now?", "Implementation Changed", Messages.getQuestionIcon()) ==
							Messages.YES) ProjectManager.getInstance().reloadProject(myProject);
					reloadProjectRequest = null;
				}
			};
			ApplicationManager.getApplication().invokeLater(reloadProjectRequest, ModalityState.NON_MODAL);
		} else {
			// if the project is not open, reset the original implementation to the same value as implementation
			originalImplementation = implementation;
		}
	}

	private boolean willReload() {
		return myProject.isOpen() && !ApplicationManager.getApplication().isUnitTestMode();
	}
}