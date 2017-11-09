package schemely.compiler;

import com.intellij.compiler.CompilerConfigurationImpl;
import com.intellij.compiler.OutputParser;
//import com.intellij.compiler.impl.javaCompiler.ExternalCompiler;
//import com.intellij.compiler.impl.javaCompiler.ModuleChunk;
//import com.intellij.compiler.impl.javaCompiler.javac.JavacSettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdkType;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.impl.MockJdkWrapper;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ModuleSourceOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;
import schemely.psi.impl.SchemeFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Colin Fleming
 */
/*
public class SchemeBackendCompiler extends ExternalCompiler
{
  private static final Logger log = Logger.getLogger(SchemeBackendCompiler.class);
  private final Project project;

  public SchemeBackendCompiler(Project project)
  {
    this.project = project;
  }

  @NotNull
  @Override
  public String getId()
  {
    return "SchemeCompiler";
  }

  @Override
  public boolean checkCompiler(CompileScope compileScope)
  {
    VirtualFile[] files = compileScope.getFiles(SchemeFileType.SCHEME_FILE_TYPE, true);
    if (files.length == 0)
    {
      return true;
    }

    ProjectFileIndex index = ProjectRootManager.getInstance(project).getFileIndex();
    Set<Module> modules = new HashSet<Module>();
    for (VirtualFile file : files)
    {
      Module module = index.getModuleForFile(file);
      if (module != null)
      {
        modules.add(module);
      }
    }

    boolean hasJava = false;
    for (Module module : modules)
    {
      if (isSuitableModule(module))
      {
        hasJava = true;
      }
    }
    if (!hasJava)
    {
      return false;
    }

    for (Module module : modules)
    {
      if (!isSuitableModule(module))
      {
        Messages.showErrorDialog(project, "Cannot compile scheme files, module incorrect", "Cannot compile");
        return false;
      }
    }

    // TODO check Kawa is on the classpath

    Set<Module> nojdkModules = new HashSet<Module>();
    for (Module module : compileScope.getAffectedModules())
    {
      if (!isSuitableModule(module))
      {
        continue;
      }
      Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
      if (sdk == null || !(sdk.getSdkType() instanceof JavaSdkType))
      {
        nojdkModules.add(module);
      }
    }

    if (!nojdkModules.isEmpty())
    {
      Module[] noJdkArray = nojdkModules.toArray(new Module[nojdkModules.size()]);
      if (noJdkArray.length == 1)
      {
        Messages.showErrorDialog(project,
                                 "Cannot compile files, please configure JDK for " + noJdkArray[0].getName(),
                                 "Cannot compile");
      }
      else
      {
        StringBuffer modulesList = new StringBuffer();
        for (int i = 0; i < noJdkArray.length; i++)
        {
          if (i > 0)
          {
            modulesList.append(", ");
          }
          modulesList.append(noJdkArray[i].getName());
        }
        Messages.showErrorDialog(project,
                                 "Cannot compile files, please configure JDK for " + modulesList.toString(),
                                 "Cannot compile");
      }
      return false;
    }
    return true;
  }

  @NotNull
  public String[] createStartupCommand(final ModuleChunk chunk,
                                       final CompileContext context,
                                       final String outputPath) throws IOException, IllegalArgumentException
  {
    final ArrayList<String> commandLine = new ArrayList<String>();
    final Exception[] ex = new Exception[]{null};
    ApplicationManager.getApplication().runReadAction(new Runnable()
    {
      public void run()
      {
        try
        {
          createStartupCommandImpl(chunk, commandLine, outputPath, context.getCompileScope());
        }
        catch (IllegalArgumentException e)
        {
          ex[0] = e;
        }
        catch (IOException e)
        {
          ex[0] = e;
        }
      }
    });
    if (ex[0] != null)
    {
      if (ex[0] instanceof IOException)
      {
        throw (IOException) ex[0];
      }
      else if (ex[0] instanceof IllegalArgumentException)
      {
        throw (IllegalArgumentException) ex[0];
      }
      else
      {
        log.error(ex[0]);
      }
    }
    return commandLine.toArray(new String[commandLine.size()]);
  }

  private void createStartupCommandImpl(ModuleChunk chunk,
                                        Collection<String> commandLine,
                                        String outputPath,
                                        CompileScope scope) throws IOException
  {
    Sdk jdk = getJdkForStartupCommand(chunk);
    String versionString = jdk.getVersionString();
    if (versionString == null || "".equals(versionString))
    {
      throw new IllegalArgumentException("Cannot determine version for JDK " + jdk.getName());
    }
    JavaSdkType sdkType = (JavaSdkType) jdk.getSdkType();

    String javaExecutablePath = sdkType.getVMExecutablePath(jdk);
    commandLine.add(javaExecutablePath);

    // For debug
    //    commandLine.add("-Xdebug");
    //    commandLine.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=127.0.0.1:5448");


    StringBuilder classPathBuilder = new StringBuilder();
    classPathBuilder.append(sdkType.getToolsPath(jdk)).append(File.pathSeparator);

    // Add classpath and sources

    for (Module module : chunk.getModules())
    {
      if (isSuitableModule(module))
      {
        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        OrderEntry[] entries = moduleRootManager.getOrderEntries();
        Set<VirtualFile> virtualFiles = new HashSet<VirtualFile>();
        for (OrderEntry orderEntry : entries)
        {
          virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.COMPILATION_CLASSES)));

          // Add module sources
          if (orderEntry instanceof ModuleSourceOrderEntry)
          {
            virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
          }
        }

        for (VirtualFile file : virtualFiles)
        {
          String path = file.getPath();
          int jarSeparatorIndex = path.indexOf(JarFileSystem.JAR_SEPARATOR);
          if (jarSeparatorIndex > 0)
          {
            path = path.substring(0, jarSeparatorIndex);
          }
          classPathBuilder.append(path).append(File.pathSeparator);
        }
      }
    }
    classPathBuilder.append(outputPath).append(File.separator);

    commandLine.add("-cp");
    commandLine.add(classPathBuilder.toString());

    SchemeCompilerSettings settings = SchemeCompilerSettings.getInstance(project);

    //Add REPL class runner
    commandLine.add("kawa.repl");

    commandLine.add("-d");
    commandLine.add(outputPath);

    if (settings.prefix != null && settings.prefix.trim().length() > 0)
    {
      commandLine.add("-P");
      commandLine.add(settings.prefix.trim());
    }

    commandLine.add(settings.modulesDefaultToStatic ? "--module-static" : "--no-module-static");
    commandLine.add(settings.warnUnknownMember ? "--warn-unknown-member" : "--no-warn-unknown-member");
    commandLine.add(settings.warnInvokeUnknownMethod ? "--warn-invoke-unknown-method" : "--no-warn-invoke-unknown-method");
    commandLine.add(settings.warnUndefinedVariable ? "--warn-undefined-variable" : "--no-warn-undefined-variable");
    commandLine.add(settings.warningsAsErrors ? "--warn-as-error" : "--no-warn-as-error");

    commandLine.add("-C");

    VirtualFile[] files = scope.getFiles(SchemeFileType.SCHEME_FILE_TYPE, true);
    if (files.length == 0)
    {
      return;
    }

    Module[] modules = chunk.getModules();
    if (modules.length > 0)
    {
      Project project = modules[0].getProject();
      PsiManager manager = PsiManager.getInstance(project);
      for (VirtualFile file : files)
      {
        PsiFile psiFile = manager.findFile(file);
        if (psiFile != null && (psiFile instanceof SchemeFile))
        {
          log.info("Compiling " + file.getPath());
          commandLine.add(file.getPath());
        }
      }
    }
  }


  private Sdk getJdkForStartupCommand(ModuleChunk chunk)
  {
    Sdk jdk = chunk.getJdk();
    if (ApplicationManager.getApplication().isUnitTestMode() &&
        JavacSettings.getInstance(project).isTestsUseExternalCompiler())
    {
      String jdkHomePath = CompilerConfigurationImpl.getTestsExternalCompilerHome();
      if (jdkHomePath == null)
      {
        throw new IllegalArgumentException("[TEST-MODE] Cannot determine home directory for JDK to use javac from");
      }
      // when running under Mock JDK use VM executable from the JDK on which the tests run
      return new MockJdkWrapper(jdkHomePath, jdk);
    }
    return jdk;
  }

  @NotNull
  @Override
  public String getPresentableName()
  {
    return "Scheme Compiler";
  }

  @NotNull
  @Override
  public Configurable createConfigurable()
  {
    return null;
  }

  @Override
  public OutputParser createErrorParser(@NotNull String s, Process process)
  {
    return new SchemeOutputParser();
  }

  @Override
  public OutputParser createOutputParser(@NotNull String s)
  {
    return new OutputParser()
    {
      @Override
      public boolean processMessageLine(Callback callback)
      {
        return super.processMessageLine(callback) || callback.getCurrentLine() != null;
      }
    };
  }

  @Override
  public void compileFinished()
  {
  }

  public static boolean isSuitableModule(Module module)
  {
    if (module == null)
    {
      return false;
    }
    ModuleType type = module.getModuleType();
    return type instanceof JavaModuleType || "PLUGIN_MODULE".equals(type.getId());
  }
}
*/
