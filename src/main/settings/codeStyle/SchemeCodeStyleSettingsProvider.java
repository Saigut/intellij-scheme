package main.settings.codeStyle;

import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.CodeStyleConfigurable;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import main.SchemeLanguage;


public class SchemeCodeStyleSettingsProvider extends CodeStyleSettingsProvider
{
  @Override
  @NotNull
  public CodeStyleConfigurable createConfigurable(@NotNull CodeStyleSettings settings, @NotNull CodeStyleSettings modelSettings)
  {
    return new SchemeFormatConfigurable(settings, modelSettings);
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
