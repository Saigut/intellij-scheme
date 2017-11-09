package schemely.psi.impl.search;

import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;


public class SchemeSourceFilterScope extends GlobalSearchScope
{
  private final GlobalSearchScope delegate;
  private final ProjectFileIndex index;

  public SchemeSourceFilterScope(GlobalSearchScope delegate, Project project)
  {
    this.delegate = delegate;
    index = ProjectRootManager.getInstance(project).getFileIndex();
  }

  public boolean contains(VirtualFile file)
  {
    if (delegate != null && !delegate.contains(file))
    {
      return false;
    }

    return index.isInSourceContent(file) || index.isInLibraryClasses(file);
  }

  public int compare(VirtualFile file1, VirtualFile file2)
  {
    return delegate != null ? delegate.compare(file1, file2) : 0;
  }

  public boolean isSearchInModuleContent(@NotNull Module aModule)
  {
    return delegate == null || delegate.isSearchInModuleContent(aModule);
  }

  public boolean isSearchInLibraries()
  {
    return delegate == null || delegate.isSearchInLibraries();
  }
}
