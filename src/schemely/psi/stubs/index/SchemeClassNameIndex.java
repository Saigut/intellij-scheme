package schemely.psi.stubs.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemeFile;


public class SchemeClassNameIndex extends StringStubIndexExtension<SchemeFile>
{
  public static final StubIndexKey<String, SchemeFile> KEY = StubIndexKey.createIndexKey("scm.class");

  @NotNull public StubIndexKey<String, SchemeFile> getKey()
  {
    return KEY;
  }
}
