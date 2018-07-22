package schemely.scheme.kawa;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications.Bus;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.util.PathUtil;
import com.intellij.util.containers.ContainerUtil;
import kawa.repl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeBundle;
import schemely.utils.LibrariesUtil;
import schemely.utils.SchemeConfigUtil;
import schemely.utils.SchemeUtils;

import java.util.List;

/**
 * @author Colin Fleming
 */
public class KawaConfigUtil
{
  @NonNls
  public static final String KAWA_JAR_NAME_PREFIX = "kawa";
  @NonNls
  public static final String KAWA_MAIN_CLASS_FILE = "kawa/repl.class";

  static final Condition<Library> KAWA_LIB_CONDITION = new Condition<Library>()
  {
    @Override
    public boolean value(Library library)
    {
      return (library != null) && SchemeConfigUtil.checkLibrary(library, KAWA_JAR_NAME_PREFIX, KAWA_MAIN_CLASS_FILE);
    }
  };

  public static final String KAWA_SDK = PathUtil.getJarPathForClass(repl.class);

  public static Library[] getProjectKawaLibraries(Project project)
  {
    if (project == null)
    {
      return new Library[0];
    }
    LibraryTable table = ProjectLibraryTable.getInstance(project);
    List<Library> all = ContainerUtil.findAll(table.getLibraries(), KAWA_LIB_CONDITION);
    return all.toArray(new Library[all.size()]);
  }

  public static Library[] getAllKawaLibraries(@Nullable Project project)
  {
//    return ArrayUtil.mergeArrays(getGlobalKawaLibraries(), getProjectKawaLibraries(project), Library.class);
    return null;
  }

  public static Library[] getGlobalKawaLibraries()
  {
    return LibrariesUtil.getGlobalLibraries(KAWA_LIB_CONDITION);
  }

  public static Library[] getKawaSdkLibrariesByModule(Module module)
  {
    return LibrariesUtil.getLibrariesByCondition(module, KAWA_LIB_CONDITION);
  }

  @NotNull
  public static String getKawaSdkJarPath(Module module)
  {
    if (module == null)
    {
      return "";
    }
    Library[] libraries = getKawaSdkLibrariesByModule(module);
    if (libraries.length == 0)
    {
      return "";
    }
    Library library = libraries[0];
    return getKawaJarPathForLibrary(library);
  }

  public static String getKawaJarPathForLibrary(Library library)
  {
    return SchemeConfigUtil.getSpecificJarForLibrary(library, KAWA_JAR_NAME_PREFIX, KAWA_MAIN_CLASS_FILE);
  }

  public static boolean isKawaConfigured(Module module)
  {
    ModuleRootManager manager = ModuleRootManager.getInstance(module);
    for (OrderEntry entry : manager.getOrderEntries())
    {
      if (entry instanceof LibraryOrderEntry)
      {
        Library library = ((LibraryOrderEntry) entry).getLibrary();
        if (library != null)
        {
          for (VirtualFile file : library.getFiles(OrderRootType.CLASSES))
          {
            @NonNls String path = file.getPath();
            if (path.endsWith(".jar!/"))
            {
              if (file.findFileByRelativePath(KAWA_MAIN_CLASS_FILE) != null)
              {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  public static void warningDefaultKawaJar(Module module)
  {
    Bus.notify(new Notification(SchemeUtils.SCHEME_NOTIFICATION_GROUP,
                                "",
                                SchemeBundle.message("kawa.jar.from.plugin.used"),
                                NotificationType.WARNING), module.getProject());
  }
}
