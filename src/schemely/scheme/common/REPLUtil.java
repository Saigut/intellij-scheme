package schemely.scheme.common;

import com.intellij.execution.CantRunException;
import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class REPLUtil
{
  public static void addSourcesToClasspath(Module module, JavaParameters params)
  {
    Collection<VirtualFile> virtualFiles = new HashSet<VirtualFile>();
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    for (OrderEntry orderEntry : entries)
    {
      if (orderEntry instanceof ModuleSourceOrderEntry)
      {
        virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
      }
    }

    for (VirtualFile file : virtualFiles)
    {
      params.getClassPath().add(file.getPath());
    }
  }

  public static List<String> getCommandLine(JavaParameters params) throws CantRunException
  {
    Project project = PlatformDataKeys.PROJECT.getData(DataManager.getInstance().getDataContext());
    GeneralCommandLine line = CommandLineBuilder.createFromJavaParameters(params, project, true);

    Sdk sdk = params.getJdk();
    assert sdk != null;
//    SdkType type = sdk.getSdkType();
//    String executablePath = ((JavaSdkType) type).getVMExecutablePath(sdk);

    String executablePath = sdk.getHomePath() + "/bin";

    List<String> cmd = new ArrayList<String>();
    cmd.add(executablePath);
    cmd.addAll(line.getParametersList().getList());
    return cmd;
  }
}
