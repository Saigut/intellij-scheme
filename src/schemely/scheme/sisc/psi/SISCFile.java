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
import schemely.scheme.sisc.SISCConfigUtil;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public class SISCFile extends SchemeFile
{
  private static final Collection<String> bootFiles = Arrays.asList(// These are from the heap
                                                                    "sisc/boot/init.scm",
                                                                    "sisc/boot/compat.scm",
                                                                    "sisc/boot/analyzer.scm",
                                                                    "sisc/boot/eval.scm",
                                                                    "sisc/boot/init2.scm",
                                                                    "sisc/boot/repl.scm",
                                                                    "sisc/boot/psyntax.ss",
                                                                    "sisc/boot/psyntax.scm",
                                                                    // TODO maybe control if we load this
                                                                    "sisc/modules/std-modules.scm",
                                                                    // Natively supported SRFIs
                                                                    "sisc/libs/srfi/srfi-0.scm",
                                                                    "sisc/libs/srfi/srfi-7.scm",
                                                                    "sisc/libs/srfi/srfi-22.scm",
                                                                    "sisc/libs/srfi/srfi-28.scm",
                                                                    "sisc/libs/srfi/srfi-30.scm",
                                                                    "sisc/libs/srfi/srfi-39.scm",
                                                                    "sisc/libs/srfi/srfi-48.scm",
                                                                    "sisc/libs/srfi/srfi-55.scm",
                                                                    "sisc/libs/srfi/srfi-62.scm");

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

    String sourcePath = SISCConfigUtil.getJarPathForResource(sisc.REPL.class, "sisc/boot/repl.scm");
    String sourceURL = VfsUtil.pathToUrl(sourcePath);
    VirtualFile sourceFile = VirtualFileManager.getInstance().findFileByUrl(sourceURL);
    if (sourceFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sourceFile);
      if (jarFile != null)
      {
        for (String bootFile : bootFiles)
        {
          VirtualFile file = jarFile.findFileByRelativePath(bootFile);
          if (file != null)
          {
            PsiFile psiFile = PsiManager.getInstance(getProject()).findFile(file);
            if (psiFile instanceof SchemeFile)
            {
              SchemeFile schemeFile = (SchemeFile) psiFile;
              if (!schemeFile.processTopLevelDefinitions(processor, state, lastParent, place))
              {
                return false;
              }
            }
          }
        }
      }
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
