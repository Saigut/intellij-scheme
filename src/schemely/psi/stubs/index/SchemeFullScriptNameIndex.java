package schemely.psi.stubs.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemeFile;
import schemely.psi.impl.search.SchemeSourceFilterScope;

import java.util.Collection;


public class SchemeFullScriptNameIndex extends IntStubIndexExtension<SchemeFile>
{
  public static final StubIndexKey<Integer, SchemeFile> KEY = StubIndexKey.createIndexKey("scm.script.fqn");

  private static final SchemeFullScriptNameIndex ourInstance = new SchemeFullScriptNameIndex();

  public static SchemeFullScriptNameIndex getInstance()
  {
    return ourInstance;
  }

  @NotNull public StubIndexKey<Integer, SchemeFile> getKey()
  {
    return KEY;
  }

  public Collection<SchemeFile> get(@NotNull Integer integer, @NotNull Project project, @NotNull GlobalSearchScope scope)
  {
    return super.get(integer, project, new SchemeSourceFilterScope(scope, project));
  }
}