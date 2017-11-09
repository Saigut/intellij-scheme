package schemely.formatter.codeStyle;

import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import schemely.SchemeLanguage;


public class SchemeCodeStylePanel extends TabbedLanguageCodeStylePanel
{
  public SchemeCodeStylePanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
    super(SchemeLanguage.INSTANCE, currentSettings, settings);
  }

  @Override
  protected void initTabs(CodeStyleSettings settings) {
    super.initTabs(settings);
    for (CodeStyleSettingsProvider provider : Extensions.getExtensions(CodeStyleSettingsProvider.EXTENSION_POINT_NAME)) {
      if (provider.getLanguage() == SchemeLanguage.INSTANCE && !provider.hasSettingsPage()) {
        createTab(provider);
      }
    }
  }

  @Override
  protected void addSpacesTab(CodeStyleSettings settings) {
  }

  @Override
  protected void addBlankLinesTab(CodeStyleSettings settings) {
  }

  @Override
  protected void addWrappingAndBracesTab(CodeStyleSettings settings) {
  }
}
