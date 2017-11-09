package schemely.utils;

import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

/**
 * @author Colin Fleming
 */
public class SchemeConfigUtil
{
  public static boolean checkLibrary(Library library, String jarNamePrefix, String necessaryClass)
  {
    boolean result = false;
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles)
    {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension()))
      {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists())
        {
          try
          {
            JarFile jarFile = new JarFile(realFile);
            try
            {
              if (name.startsWith(jarNamePrefix))
              {
                result = jarFile.getJarEntry(necessaryClass) != null;
              }
            }
            finally
            {
              jarFile.close();
            }
          }
          catch (IOException ignore)
          {
            result = false;
          }
        }
      }
    }
    return result;
  }

  public static String getSpecificJarForLibrary(Library library, String jarNamePrefix, String necessaryClass)
  {
    VirtualFile[] classFiles = library.getFiles(OrderRootType.CLASSES);
    for (VirtualFile file : classFiles)
    {
      String path = file.getPath();
      if (path != null && "jar".equals(file.getExtension()))
      {
        path = StringUtil.trimEnd(path, "!/");
        String name = file.getName();

        File realFile = new File(path);
        if (realFile.exists())
        {
          try
          {
            JarFile jarFile = new JarFile(realFile);
            try
            {
              if (name.startsWith(jarNamePrefix) && jarFile.getJarEntry(necessaryClass) != null)
              {
                return path;
              }
            }
            finally
            {
              jarFile.close();
            }
          }
          catch (IOException ignored)
          {
            //do nothing
          }
        }
      }
    }
    return "";
  }
}
