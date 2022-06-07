package main.settings.codeStyle;

import com.intellij.lang.Language;
import com.intellij.openapi.options.Configurable;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import main.SchemeLanguage;


public class SchemeCodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
  @Override
  @NotNull
  public Configurable createSettingsPage(CodeStyleSettings settings, CodeStyleSettings originalSettings)
  {
    return new SchemeFormatConfigurable(settings, originalSettings);
  }

  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings)
  {
    return new SchemeCodeStyleSettings(settings);
  }

  @Override
  public String getConfigurableDisplayName() {
    return "Scheme";
  }

  @NotNull
  @Override
  public Language getLanguage() {
    return SchemeLanguage.INSTANCE;
  }
}
