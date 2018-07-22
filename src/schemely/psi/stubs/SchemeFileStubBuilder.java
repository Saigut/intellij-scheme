package schemely.psi.stubs;

import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.DefaultStubBuilder;
import com.intellij.psi.stubs.StubElement;
import schemely.psi.impl.SchemeFile;
import schemely.psi.stubs.impl.SchemeFileStubImpl;


public class SchemeFileStubBuilder extends DefaultStubBuilder
{
  protected StubElement createStubForFile(PsiFile file)
  {
    if (file instanceof SchemeFile && false)
    {
      return new SchemeFileStubImpl((SchemeFile) file);
    }

    return super.createStubForFile(file);
  }
}