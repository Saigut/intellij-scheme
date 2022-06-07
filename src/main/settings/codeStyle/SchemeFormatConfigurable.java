package main.settings.codeStyle;

import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.psi.codeStyle.CodeStyleSettings;


public class SchemeFormatConfigurable extends CodeStyleAbstractConfigurable
{
  public SchemeFormatConfigurable(CodeStyleSettings settings, CodeStyleSettings cloneSettings)
  {
    super(settings, cloneSettings, "Scheme");
  }

  protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings)
  {
    return new SchemeCodeStylePanel(getCurrentSettings(), settings);
  }

  public String getHelpTopic()
  {
    return "Scheme help topic. Nothing";
  }
}
