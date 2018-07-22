package schemely.formatter.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.SchemeLanguage;

import javax.swing.*;


public class SchemeFormatConfigurable extends CodeStyleAbstractConfigurable
{
  public SchemeFormatConfigurable(CodeStyleSettings settings, CodeStyleSettings cloneSettings)
  {
    super(settings, cloneSettings, SchemeBundle.message("title.scheme.code.style.settings"));
  }

  protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings)
  {
    return new SchemeCodeStylePanel(getCurrentSettings(), settings);
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  public String getHelpTopic()
  {
    return "reference.settingsdialog.IDE.globalcodestyle.spaces";
  }
}
