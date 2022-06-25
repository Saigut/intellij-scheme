package main.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import main.SchemeIcons;
import main.SchemeLanguage;

import javax.swing.Icon;

public class SchemeFileType extends LanguageFileType
{
  public static final SchemeFileType SCHEME_FILE_TYPE = new SchemeFileType();
  public static final Language SCHEME_LANGUAGE = SCHEME_FILE_TYPE.getLanguage();
  private static String[] extensions = {"ss", "sls", "scm"};

  public SchemeFileType()
  {
    super(SchemeLanguage.INSTANCE);
  }

  @NotNull
  public String getName()
  {
    return "Scheme";
  }

  @NotNull
  public String getDescription()
  {
    return "Scheme file";
  }

  @NotNull
  public String getDefaultExtension()
  {
    return "ss";
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  public static String[] getExtensions()
  {
    return extensions;
  }
}
