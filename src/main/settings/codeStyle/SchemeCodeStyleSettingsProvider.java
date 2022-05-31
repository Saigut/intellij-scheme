package main.settings.codeStyle;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import main.SchemeLanguage;


public class SchemeCodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
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
