package schemely.psi.stubs.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import schemely.parser.AST;
import schemely.psi.impl.SchemeFile;
import schemely.psi.stubs.api.SchemeFileStub;


public class SchemeFileStubImpl extends PsiFileStubImpl<SchemeFile> implements SchemeFileStub
{
  private final StringRef packageName;
  private final StringRef name;
  private final boolean isClassDefinition;

  public SchemeFileStubImpl(SchemeFile file)
  {
    super(file);
    packageName = StringRef.fromString(file.getPackageName());
    isClassDefinition = false;
    name = StringRef.fromString(null);
  }

  public SchemeFileStubImpl(StringRef packName, StringRef name, boolean isScript)
  {
    super(null);
    packageName = packName;
    this.name = name;
    this.isClassDefinition = isScript;
  }

  public IStubFileElementType getType()
  {
    return AST.FILE;
  }

  public StringRef getPackageName()
  {
    return packageName;
  }

  public StringRef getName()
  {
    return name;
  }

  public boolean isClassDefinition()
  {
    return isClassDefinition;
  }
}