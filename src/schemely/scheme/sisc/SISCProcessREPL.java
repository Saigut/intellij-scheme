package schemely.scheme.sisc;

import com.intellij.execution.CantRunException;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionHelper;
import com.intellij.execution.KillableProcess;
import com.intellij.execution.configurations.JavaParameters;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.CommandLineArgumentsProvider;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessTerminatedListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import schemely.SchemeBundle;
import schemely.repl.REPLProviderBase;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.REPLException;
import schemely.scheme.common.REPLBase;
import schemely.scheme.common.REPLUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Colin Fleming
 */
public class SISCProcessREPL extends REPLBase
{
  public static final String REPL_TITLE;
  private static final String[] EMPTY_ENV = new String[0];
  private final Module module;
  private final String workingDir;
  private ProcessHandler processHandler;
  private SISCOutputProcessor outputProcessor;

  public SISCProcessREPL(Project project, Module module, SchemeConsoleView consoleView, String workingDir)
  {
    super(consoleView, project);
    this.workingDir = workingDir;
    this.module = module;
  }

  @Override
  public void start() throws REPLException
  {
    Process process;
    CommandLineArgumentsProvider provider;
    try
    {
      provider = getArgumentsProvider(module, workingDir);
      process = createProcess(provider);
    }
    catch (ExecutionException e)
    {
      throw new REPLException(e);
    }

    outputProcessor = new SISCOutputProcessor(consoleView.getConsole());
    processHandler = new SchemeProcessHandler(process, provider);
    ProcessTerminatedListener.attach(processHandler);
    processHandler.addProcessListener(new ProcessAdapter()
    {
      @Override
      public void processTerminated(ProcessEvent event)
      {
        consoleView.getConsole().getConsoleEditor().setRendererMode(true);
        hideEditor();
      }
    });

    getConsoleView().attachToProcess(processHandler);
    processHandler.startNotify();
  }

  @Override
  public void stop()
  {
    if (processHandler instanceof KillableProcess && processHandler.isProcessTerminating())
    {
      ((KillableProcess) processHandler).killProcess();
      outputProcessor.flush();
      return;
    }

    if (processHandler.detachIsDefault())
    {
      processHandler.detachProcess();
    }
    else
    {
      processHandler.destroyProcess();
    }
    outputProcessor.flush();
  }

  @Override
  public void execute(String command)
  {
    setEditorEnabled(false);

    outputProcessor.executing(new Runnable()
    {
      @Override
      public void run()
      {
        setEditorEnabled(true);
      }
    });

    OutputStream outputStream = processHandler.getProcessInput();
    try
    {
      byte[] bytes = (command + '\n').getBytes();
      outputStream.write(bytes);
      outputStream.flush();
    }
    catch (IOException ignore)
    {
    }
  }

  @Override
  public boolean isActive()
  {
    if (!processHandler.isStartNotified())
    {
      return false;
    }
    return !(processHandler.isProcessTerminated() || processHandler.isProcessTerminating());
  }

  @Override
  public boolean isExecuting()
  {
    return outputProcessor.ifExecuting(new Runnable()
    {
      @Override
      public void run()
      {
        // NOP
      }
    });
  }

  @Override
  public Collection<PsiNamedElement> getSymbolVariants(PsiManager manager, PsiElement symbol)
  {
    // TODO
    return Collections.emptyList();
  }

  CommandLineArgumentsProvider getArgumentsProvider(Module module, String workingDir) throws CantRunException
  {
    final List<String> args = createRuntimeArgs(module, workingDir);
    return new CommandLineArgumentsProvider()
    {
      @Override
      public String[] getArguments()
      {
        return args.toArray(new String[args.size()]);
      }

      @Override
      public boolean passParentEnvs()
      {
        return false;
      }

      @Override
      public Map<String, String> getAdditionalEnvs()
      {
        return new HashMap<String, String>();
      }
    };
  }

  protected Process createProcess(CommandLineArgumentsProvider provider) throws ExecutionException
  {
    Process process = null;
    try
    {
      process = Runtime.getRuntime().exec(provider.getArguments(), EMPTY_ENV, new File(workingDir));
    }
    catch (IOException e)
    {
      ExecutionHelper.showErrors(project, Arrays.<Exception>asList(e), REPL_TITLE, null);
    }

    return process;
  }

  public List<String> createRuntimeArgs(Module module, String workingDir) throws CantRunException
  {
    JavaParameters params = new JavaParameters();
    params.configureByModule(module, JavaParameters.JDK_AND_CLASSES);

    params.getClassPath().add(SISCConfigUtil.SISC_SDK);
    params.getClassPath().add(SISCConfigUtil.SISC_LIB);
    params.getClassPath().add(SISCConfigUtil.SISC_OPT);

    REPLUtil.addSourcesToClasspath(module, params);

    params.setMainClass(sisc.REPL.class.getName());
    params.setWorkingDirectory(new File(workingDir));

    return REPLUtil.getCommandLine(params);
  }

  static
  {
    REPL_TITLE = SchemeBundle.message("repl.toolWindowName");
  }

  private class SchemeProcessHandler extends ColoredProcessHandler
  {
    public SchemeProcessHandler(Process process, CommandLineArgumentsProvider provider)
    {
      super(process, provider.getCommandLineString(), CharsetToolkit.UTF8_CHARSET);
    }

    @Override
    protected void textAvailable(String text, Key attributes)
    {
      // TODO use attributes?
      outputProcessor.processOutput(text);
    }
  }

  static class Provider extends REPLProviderBase
  {
    @Override
    public boolean isSupported()
    {
      return true;
    }

    @Override
    public schemely.scheme.REPL newREPL(Project project,
                                        Module module,
                                        SchemeConsoleView consoleView,
                                        String workingDir)
    {
      return new SISCProcessREPL(project, module, consoleView, workingDir);
    }

    protected String getTabName()
    {
      return "External";
    }
  }
}
