package schemely.scheme.sisc.psi;

import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.SchemeFile;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public class SISCFile extends SchemeFile
{
  public SISCFile(FileViewProvider viewProvider)
  {
    super(viewProvider);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place)
  {
    if (!processTopLevelDefinitions(processor, state, lastParent, place))
    {
      return false;
    }

    String url = VfsUtil.pathToUrl(PathUtil.getJarPathForClass(SISCFile.class));
    VirtualFile sdkFile = VirtualFileManager.getInstance().findFileByUrl(url);
    if (sdkFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sdkFile);
      SchemeFile heapFile = null;
      if (jarFile != null)
      {
        heapFile = getHeapFile(jarFile);
      }
      else if (sdkFile instanceof VirtualDirectoryImpl)
      {
        heapFile = getHeapFile(sdkFile);
      }
      if (heapFile != null)
      {
        if (!heapFile.processTopLevelDefinitions(processor, state, lastParent, place))
        {
          return false;
        }
      }
    }

    return true;
  }

  private SchemeFile getHeapFile(VirtualFile virtualFile)
  {
    VirtualFile heapFile = virtualFile.findFileByRelativePath("heap.scm");
    if (heapFile != null)
    {
      PsiFile file = PsiManager.getInstance(getProject()).findFile(heapFile);
      if (file instanceof SchemeFile)
      {
        return (SchemeFile) file;
      }
    }
    return null;
  }
}
