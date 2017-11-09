package schemely.parser;

import com.intellij.psi.tree.IElementType;
import schemely.file.SchemeFileType;

public class SchemeElementType extends IElementType
{
  private final String name;

  public SchemeElementType(String debugName)
  {
    super(debugName, SchemeFileType.SCHEME_LANGUAGE);
    name = debugName;
  }

  public String getName()
  {
    return name;
  }
}
