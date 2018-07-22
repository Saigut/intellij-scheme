package schemely.compiler;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SchemeCompilerConfigurable implements Configurable
{
  private JCheckBox modulesDefaultToStaticCheckBox;
  private JCheckBox warnOnUnknownMembersCheckBox;
  private JCheckBox warnInvokeUnknownMethodCheckBox;
  private JCheckBox warnOnUndefinedVariablesCheckBox;
  private JTextField classNamePrefixField;
  private JPanel panel;
  private JCheckBox treatWarningsAsErrorsCheckBox;

  private SchemeCompilerSettings settings;

  public SchemeCompilerConfigurable(SchemeCompilerSettings settings)
  {
    this.settings = settings;
  }

  @Nls
  public String getDisplayName()
  {
    return "Scheme Compiler";
  }

  public Icon getIcon()
  {
    return null;
  }

  public String getHelpTopic()
  {
    return null;
  }

  public JComponent createComponent()
  {
    return panel;
  }

  public boolean isModified()
  {
    return !settings.prefix.equals(classNamePrefixField.getText()) ||
           (settings.modulesDefaultToStatic != modulesDefaultToStaticCheckBox.isSelected()) ||
           (settings.warnUnknownMember != warnOnUnknownMembersCheckBox.isSelected()) ||
           (settings.warnInvokeUnknownMethod != warnInvokeUnknownMethodCheckBox.isSelected()) ||
           (settings.warnUndefinedVariable != warnOnUndefinedVariablesCheckBox.isSelected()) ||
           (settings.warningsAsErrors != treatWarningsAsErrorsCheckBox.isSelected());
  }

  public void apply() throws ConfigurationException
  {
    settings.prefix = classNamePrefixField.getText();
    settings.modulesDefaultToStatic = modulesDefaultToStaticCheckBox.isSelected();
    settings.warnUnknownMember = warnOnUnknownMembersCheckBox.isSelected();
    settings.warnInvokeUnknownMethod = warnInvokeUnknownMethodCheckBox.isSelected();
    settings.warnUndefinedVariable = warnOnUndefinedVariablesCheckBox.isSelected();
    settings.warningsAsErrors = treatWarningsAsErrorsCheckBox.isSelected();
  }

  public void reset()
  {
    classNamePrefixField.setText(settings.prefix);
    modulesDefaultToStaticCheckBox.setSelected(settings.modulesDefaultToStatic);
    warnOnUnknownMembersCheckBox.setSelected(settings.warnUnknownMember);
    warnInvokeUnknownMethodCheckBox.setSelected(settings.warnInvokeUnknownMethod);
    warnOnUndefinedVariablesCheckBox.setSelected(settings.warnUndefinedVariable);
    treatWarningsAsErrorsCheckBox.setSelected(settings.warningsAsErrors);
  }

  public void disposeUIResources()
  {
  }
}
