package schemely;

import com.intellij.lang.Language;

public class SchemeLanguage extends Language
{
  public static final SchemeLanguage INSTANCE = new SchemeLanguage();

  public SchemeLanguage()
  {
    super("Scheme");
  }
}
