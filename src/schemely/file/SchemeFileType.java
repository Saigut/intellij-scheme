package schemely.file;

import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeIcons;
import schemely.SchemeLanguage;

import javax.swing.Icon;

public class SchemeFileType extends LanguageFileType
{
  public static final SchemeFileType SCHEME_FILE_TYPE = new SchemeFileType();
  public static final Language SCHEME_LANGUAGE = SCHEME_FILE_TYPE.getLanguage();
  @NonNls
  public static final String SCHEME_EXTENSIONS = "scm;ss";


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
    return "scm";
  }

  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  public boolean isJVMDebuggingSupported()
  {
    return true;
  }
}
