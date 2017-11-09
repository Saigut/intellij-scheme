package schemely.psi.impl;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
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
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;
import schemely.psi.api.SchemePsiElement;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveUtil;
import schemely.psi.util.SchemePsiUtil;
import schemely.psi.util.SchemeTextUtil;

import java.util.ArrayList;
import java.util.Collection;

public class SchemeFile extends PsiFileBase implements PsiFile, PsiFileWithStubSupport, SchemePsiElement
{
  private PsiElement context = null;

  @Override
  public String toString()
  {
    return "SchemeFile";
  }

  public SchemeFile(FileViewProvider viewProvider)
  {
    super(viewProvider, SchemeFileType.SCHEME_LANGUAGE);
  }

  @Override
  public PsiElement getContext()
  {
    if (context != null)
    {
      return context;
    }
    return super.getContext();
  }

  protected PsiFileImpl clone()
  {
    SchemeFile clone = (SchemeFile) super.clone();
    clone.context = context;
    return clone;
  }

  @NotNull
  public FileType getFileType()
  {
    return SchemeFileType.SCHEME_FILE_TYPE;
  }

  @NotNull
  public String getPackageName()
  {
    String ns = getNamespace();
    if (ns == null)
    {
      return "";
    }
    else
    {
      return SchemeTextUtil.getSymbolPrefix(ns);
    }
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

    String url = VfsUtil.pathToUrl(PathUtil.getJarPathForClass(SchemeFile.class));
    VirtualFile sdkFile = VirtualFileManager.getInstance().findFileByUrl(url);
    if (sdkFile != null)
    {
      VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sdkFile);
      if (jarFile != null)
      {
        return resolveFrom(processor, getR5RSFile(jarFile));
      }
      else if (sdkFile instanceof VirtualDirectoryImpl)
      {
        return resolveFrom(processor, getR5RSFile(sdkFile));
      }
    }
    return true;
  }

  private boolean resolveFrom(PsiScopeProcessor scopeProcessor, SchemeFile schemeFile)
  {
    for (SchemeList item : getSchemeCompleteItems(schemeFile))
    {
      SchemeIdentifier identifier = item.findFirstChildByClass(SchemeIdentifier.class);
      if (!ResolveUtil.processElement(scopeProcessor, identifier))
      {
        return false;
      }
    }
    return true;
  }

  public boolean processTopLevelDefinitions(@NotNull PsiScopeProcessor processor,
                                            @NotNull ResolveState state,
                                            PsiElement lastParent,
                                            @NotNull PsiElement place)
  {
    PsiElement next = getFirstChild();
    while (next != null)
    {
      if (next instanceof SchemeList)
      {
        SchemeList list = (SchemeList) next;

        if (list.isTopLevelDefinition())
        {
          if (!next.processDeclarations(processor, state, lastParent, place))
          {
            return false;
          }
        }

      }

      next = next.getNextSibling();
    }

    return true;
  }

  private Collection<PsiElement> getCompletionItems(SchemeFile schemeFile)
  {
    Collection<PsiElement> ret = new ArrayList<PsiElement>();
    for (SchemeList item : getSchemeCompleteItems(schemeFile))
    {
      SchemeIdentifier identifier = item.findFirstChildByClass(SchemeIdentifier.class);
      if (identifier != null)
      {
        ret.add(identifier);
      }
    }
    return ret;
  }

  private Collection<SchemeList> getSchemeCompleteItems(SchemeFile schemeFile)
  {
    Collection<SchemeList> ret = new ArrayList<SchemeList>();

    PsiElement child = schemeFile.getFirstChild();
    while ((child != null) && SchemePsiElementBase.isWrongElement(child))
    {
      child = child.getNextSibling();
    }
    if (child instanceof SchemeQuoted)
    {
      SchemeQuoted quoted = (SchemeQuoted) child;

      SchemeList items = quoted.findFirstChildByClass(SchemeList.class);
      SchemeList item = items.findFirstChildByClass(SchemeList.class);
      while (item != null)
      {
        ret.add(item);
        item = item.findFirstSiblingByClass(SchemeList.class);
      }
    }
    return ret;
  }

  private SchemeFile getR5RSFile(VirtualFile virtualFile)
  {
    VirtualFile r5rsFile = virtualFile.findFileByRelativePath("heap.scm");
    if (r5rsFile != null)
    {
      PsiFile file = PsiManager.getInstance(getProject()).findFile(r5rsFile);
      if (file instanceof SchemeFile)
      {
        return (SchemeFile) file;
      }
    }
    return null;
  }

  @Override
  public int getQuotingLevel()
  {
    return 0;
  }

  public String getNamespace()
  {
    SchemeList ns = getNamespaceElement();
    if (ns == null)
    {
      return null;
    }
    SchemeIdentifier first = ns.findFirstChildByClass(SchemeIdentifier.class);
    if (first == null)
    {
      return null;
    }
    SchemeIdentifier snd = SchemePsiUtil.findNextSiblingByClass(first, SchemeIdentifier.class);
    if (snd == null)
    {
      return null;
    }

    return snd.getNameString();
  }

  public SchemeList getNamespaceElement()
  {
    // TODO CMF
    return null; //SchemePsiUtil.findFormByNameSet(this, SchemeParser.NS_TOKENS);
  }
}
