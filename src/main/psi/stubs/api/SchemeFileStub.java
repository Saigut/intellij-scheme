package main.psi.stubs.api;

import com.intellij.psi.stubs.PsiFileStub;
import com.intellij.util.io.StringRef;
import main.psi.impl.SchemeFile;


public interface SchemeFileStub extends PsiFileStub<SchemeFile>
{
  StringRef getPackageName();

  StringRef getName();

  boolean isClassDefinition();
}

