package main.psi.stubs.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import main.psi.impl.SchemeFile;
import main.psi.impl.search.SchemeSourceFilterScope;

import java.util.Collection;


public class SchemeFullScriptNameIndex extends IntStubIndexExtension<SchemeFile>
{
  public static final StubIndexKey<Integer, SchemeFile> KEY = StubIndexKey.createIndexKey("scm.script.fqn");

  public StubIndexKey<Integer, SchemeFile> getKey()
  {
    return KEY;
  }

  public Collection<SchemeFile> get(Integer integer, Project project, GlobalSearchScope scope)
  {
    return super.get(integer, project, new SchemeSourceFilterScope(scope, project));
  }
}