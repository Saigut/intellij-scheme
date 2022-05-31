package main.settings.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;


public class SchemeCodeStyleSettings extends CustomCodeStyleSettings
{
  protected SchemeCodeStyleSettings(CodeStyleSettings container)
  {
    super("SchemeCodeStyleSettings", container);
  }
}
