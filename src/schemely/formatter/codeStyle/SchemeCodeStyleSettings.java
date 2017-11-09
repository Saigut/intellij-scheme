package schemely.formatter.codeStyle;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;


public class SchemeCodeStyleSettings extends CustomCodeStyleSettings
{
  public boolean INDENT_LABEL_BLOCKS = true;

  protected SchemeCodeStyleSettings(CodeStyleSettings container)
  {
    super("SchemeCodeStyleSettings", container);
  }
}
