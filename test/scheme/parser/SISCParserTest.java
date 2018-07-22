package schemely.parser;

import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
import schemely.file.SchemeFileType;
import schemely.psi.util.SchemePsiUtil;
import schemely.scheme.sisc.SISCConfigUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class SISCParserTest extends ParserTestBase
{
//  @Test(dataProvider = "siscFiles", groups = "Schemely")
  public void test(PsiFileWrapper wrapper)
  {
    assert !SchemePsiUtil.containsSyntaxErrors(wrapper.psiFile);
  }

//  @DataProvider(name = "siscFiles")
  private Object[][] getSISCFiles()
  {
    setUp();

    String url = VfsUtil.pathToUrl(SISCConfigUtil.SISC_SRC);
    VirtualFile sdkFile = VirtualFileManager.getInstance().findFileByUrl(url);
    VirtualFile jarFile = JarFileSystem.getInstance().getJarRootForLocalFile(sdkFile);

    List<PsiFile> files = new ArrayList<PsiFile>();
    parseAll(jarFile, files);

    Object[][] ret = new Object[files.size()][];
    for (int i = 0; i < files.size(); i++)
    {
      ret[i] = new Object[] { new PsiFileWrapper(files.get(i)) };
    }
    return ret;

  }

  private void parseAll(VirtualFile file, List<PsiFile> files)
  {
    if (file.isDirectory())
    {
      for (VirtualFile child : file.getChildren())
      {
        parseAll(child, files);
      }
    }
    else if (file.getFileType() == SchemeFileType.SCHEME_FILE_TYPE)
    {
      PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
      files.add(psiFile);
    }
  }

  private static class PsiFileWrapper
  {
    private final PsiFile psiFile;

    public PsiFileWrapper(PsiFile psiFile)
    {
      this.psiFile = psiFile;
    }

    @Override
    public String toString()
    {
      return psiFile.getName();
    }
  }
}
