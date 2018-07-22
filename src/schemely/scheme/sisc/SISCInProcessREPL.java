package schemely.scheme.sisc;

import com.intellij.codeInsight.completion.CompletionInitializationContext;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ExportableOrderEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.encoding.EncodingManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import schemely.repl.REPLProviderBase;
import schemely.repl.SchemeConsoleElement;
import schemely.repl.SchemeConsoleView;
import schemely.scheme.REPLException;
import schemely.scheme.common.REPLBase;
import sisc.REPL;
import sisc.data.Procedure;
import sisc.data.SchemeThread;
import sisc.data.SchemeVoid;
import sisc.data.Symbol;
import sisc.data.Value;
import sisc.env.DynamicEnvironment;
import sisc.env.MemorySymEnv;
import sisc.env.SymbolicEnvironment;
import sisc.interpreter.AppContext;
import sisc.interpreter.Context;
import sisc.interpreter.Interpreter;
import sisc.interpreter.SchemeCaller;
import sisc.interpreter.SchemeException;
import sisc.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Colin Fleming
 */
public class SISCInProcessREPL extends REPLBase
{
  private SISCOutputProcessor outputProcessor;
  private LocalREPLThread replThread = null;

  private enum State
  {
    INITIAL, RUNNING, STOPPED
  }

  private volatile State state = State.INITIAL;
  private final AtomicBoolean terminated = new AtomicBoolean(false);
  private final CountDownLatch replFinished = new CountDownLatch(1);
  private ToReplInputStream toREPL;
  private FromREPLOutputStream fromREPL;
  private AppContext appContext;

  ExecutorService executor = Executors.newCachedThreadPool();

  public SISCInProcessREPL(Project project, SchemeConsoleView consoleView)
  {
    super(consoleView, project);
  }

  @Override
  public void start() throws REPLException
  {
    verifyState(State.INITIAL);

    ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
    URLClassLoader classLoader = new URLClassLoader(getLibraryURLs(), AppContext.class.getClassLoader());

    try
    {
      Thread.currentThread().setContextClassLoader(classLoader);

      appContext = new AppContext();
      Context.setDefaultAppContext(appContext);
      URL heap = AppContext.findHeap(null);
      if (heap == null)
      {
        throw new REPLException("Heap is null");
      }
      try
      {
        if (!appContext.addHeap(AppContext.openHeap(heap)))
        {
          throw new REPLException("Error adding heap");
        }
      }
      catch (ClassNotFoundException e)
      {
        throw new REPLException("Error adding heap", e);
      }
      catch (IOException e)
      {
        throw new REPLException("Error opening heap", e);
      }

      outputProcessor = new SISCOutputProcessor(consoleView.getConsole());

      Charset charset = EncodingManager.getInstance().getDefaultCharset();
      toREPL = new ToReplInputStream(charset);
      fromREPL = new FromREPLOutputStream(charset)
      {
        @Override
        protected void textAvailable(String text)
        {
          outputProcessor.processOutput(text);
        }
      };

      DynamicEnvironment dynamicEnvironment = new DynamicEnvironment(appContext, toREPL, fromREPL);
      replThread = new LocalREPLThread(dynamicEnvironment, REPL.getCliProc(appContext));
      REPL repl = new REPL(replThread);
      repl.go();
    }
    finally
    {
      Thread.currentThread().setContextClassLoader(oldClassLoader);
    }

    state = State.RUNNING;
  }

  @Override
  public void stop()
  {
    verifyState(State.RUNNING);

    outputProcessor.ifExecuting(new Runnable()
    {
      @Override
      public void run()
      {
        replThread.threadContext.interrupt = true;
      }
    });

    toREPL.enqueue("(exit)\n");

    try
    {
      replFinished.await();
      outputProcessor.flush();
    }
    catch (InterruptedException ignored)
    {
      Thread.currentThread().interrupt();
    }
    state = State.STOPPED;
  }

  @Override
  public void execute(String command)
  {
    verifyState(State.RUNNING);

    setEditorEnabled(false);

    outputProcessor.executing(new Runnable()
    {
      @Override
      public void run()
      {
        setEditorEnabled(true);
      }
    });

    toREPL.enqueue(command + "\n");
  }

  @Override
  public boolean isActive()
  {
    return state == State.RUNNING;
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
    GetCompletions getCompletions = new GetCompletions(manager);
    try
    {
      Context.execute(appContext, getCompletions);
    }
    catch (SchemeException ignored)
    {
      // TODO
    }
    return getCompletions.getCompletions();
  }

  private void verifyState(State expected)
  {
    if (state != expected)
    {
      throw new IllegalStateException("Expected state " + expected + ", found " + state);
    }
  }

  private URL[] getLibraryURLs() throws REPLException
  {
    List<URL> urls = new ArrayList<URL>();

    Module module = findSuitableModule();
    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
    OrderEntry[] entries = moduleRootManager.getOrderEntries();
    Set<VirtualFile> virtualFiles = new HashSet<VirtualFile>();
    for (OrderEntry orderEntry : entries)
    {
      if (orderEntry instanceof ExportableOrderEntry)
      {
        virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.CLASSES)));
        //
        //      // Add module sources
        //      if (orderEntry instanceof ModuleSourceOrderEntry)
        //      {
        //        virtualFiles.addAll(Arrays.asList(orderEntry.getFiles(OrderRootType.SOURCES)));
        //      }
      }
    }

    try
    {
      for (VirtualFile file : virtualFiles)
      {
        String path = file.getPath();
        int jarSeparatorIndex = path.indexOf(JarFileSystem.JAR_SEPARATOR);
        if (jarSeparatorIndex > 0)
        {
          path = path.substring(0, jarSeparatorIndex);
          urls.add(new URL("file://" + path));
        }
      }
    }
    catch (MalformedURLException e)
    {
      throw new REPLException("Bad library URL: " + e.getMessage());
    }

    return urls.toArray(new URL[urls.size()]);
  }

  private Module findSuitableModule()
  {
    Module[] modules = ModuleManager.getInstance(project).getModules();
    int i = 0;
    while (i < modules.length && !isSuitableModule(modules[i]))
    {
      i++;
    }
    return modules[i];
  }

  private boolean isSuitableModule(Module module)
  {
//    ModuleType type = module.getModuleType();
//    return ModuleTypeManager.getInstance().isClasspathProvider(type) &&
//           ((type instanceof JavaModuleType) || "PLUGIN_MODULE".equals(type.getId()));
    return true;
  }

  public class LocalREPLThread extends SchemeThread
  {
    public LocalREPLThread(DynamicEnvironment environment, Procedure thunk)
    {
      super(environment, thunk);
    }

    public void run()
    {
      super.run();
      terminated.set(true);

      try
      {
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        executor.shutdownNow();
      }
      catch (InterruptedException ignored)
      {
      }

      try
      {
        toREPL.close();
        fromREPL.close();
      }
      catch (IOException ignored)
      {
      }

      hideEditor();

      SISCInProcessREPL.this.state = State.STOPPED;

      replFinished.countDown();
    }

  }

  private static class GetCompletions implements SchemeCaller
  {
    private final Collection<PsiNamedElement> completions = new ArrayList<PsiNamedElement>();
    private final PsiManager psiManager;

    public GetCompletions(PsiManager psiManager)
    {
      this.psiManager = psiManager;
    }

    @Override
    public Object execute(Interpreter r) throws SchemeException
    {
      SymbolicEnvironment symbolicEnvironment = r.getContextEnv(Util.TOPLEVEL);

      SymbolicEnvironment syntaxEnvironment = symbolicEnvironment.getSidecarEnvironment(Util.EXPSC);
      addCompletions(syntaxEnvironment);

      SymbolicEnvironment topEnvironment = symbolicEnvironment.getSidecarEnvironment(Util.EXPTOP);
      addCompletions(topEnvironment);

      addCompletions(symbolicEnvironment);

      return new SchemeVoid();
    }

    private void addCompletions(SymbolicEnvironment symbolicEnvironment)
    {
      if (symbolicEnvironment instanceof MemorySymEnv)
      {
        MemorySymEnv symEnv = (MemorySymEnv) symbolicEnvironment;
        Map symbolMap = symEnv.symbolMap;
        for (Object object : symbolMap.keySet())
        {
          if (object instanceof Symbol)
          {
            String name = object.toString();
            if (!hasCompletion(name) && !name.endsWith(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED))
            {
              completions.add(new SchemeConsoleElement(psiManager, name));
            }
          }
        }
      }
    }

    private boolean hasCompletion(String name)
    {
      for (PsiElement completion : completions)
      {
        if (((SchemeConsoleElement) completion).getName().equals(name))
        {
          return true;
        }
      }
      return false;
    }

    private void dump(SymbolicEnvironment environment)
    {
      Set<SymbolicEnvironment> seen = Collections.newSetFromMap(new IdentityHashMap<SymbolicEnvironment, Boolean>());
      dump(environment, seen, "");
    }

    private void dump(SymbolicEnvironment environment, Set<SymbolicEnvironment> seen, String indent)
    {
      if (environment == null)
      {
        return;
      }

      if (seen.contains(environment))
      {
        System.out.println(indent + environment.getName() + ": already seen");
        return;
      }

      seen.add(environment);
      if (environment.getName() != null)
      {
        System.out.println(indent + environment.getName());
      }

      if (environment instanceof MemorySymEnv)
      {
        MemorySymEnv symEnv = (MemorySymEnv) environment;
        Map<Symbol, Integer> symbolMap = symEnv.symbolMap;
        for (Symbol key : sortSymbols(symbolMap.keySet()))
        {
          Value value = symEnv.env[symbolMap.get(key)];
          System.out.println(indent + key + ": " + value.getClass().getSimpleName());
        }

        if (environment.getParent() != null)
        {
          System.out.println(indent + "Parent:");
          dump(environment.getParent(), seen, indent + "  ");
        }

        Map<Symbol, SymbolicEnvironment> sidecarMap = symEnv.sidecars;
        if (!sidecarMap.isEmpty())
        {
          String nextIndent = indent + "  ";
          System.out.println(nextIndent + "Sidecars:");
          for (Symbol key : sortSymbols(sidecarMap.keySet()))
          {
            System.out.println(nextIndent + key);
            dump(sidecarMap.get(key), seen, nextIndent + "  ");
          }
        }
      }
      else
      {
        System.out.println(indent + environment.getName() + ": is a " + environment.getClass().getSimpleName());
      }
    }

    public Collection<Symbol> sortSymbols(Collection<Symbol> unsorted)
    {
      List<Symbol> symbols = new ArrayList<Symbol>(unsorted);
      Collections.sort(symbols, new Comparator<Symbol>()
      {
        @Override
        public int compare(Symbol o1, Symbol o2)
        {
          return o1.toString().compareTo(o2.toString());
        }
      });
      return symbols;
    }

    public Collection<PsiNamedElement> getCompletions()
    {
      return completions;
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
      return new SISCInProcessREPL(project, consoleView);
    }

    @Override
    protected String getTabName()
    {
      return "Local";
    }
  }

  static class ToReplInputStream extends InputStream
  {
    private final BlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();
    private final Charset charset;
    private int cursor = 0;
    private byte[] current = null;

    public ToReplInputStream(Charset charset)
    {
      this.charset = charset;
    }

    public void enqueue(String data)
    {
      try
      {
        queue.put(data.getBytes(charset));
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
      }
    }

    @Override
    public synchronized int read() throws IOException
    {
      ensureCurrent();

      int ret = current[cursor];
      incrementBy(1);

      return ret;
    }

    @Override
    public int read(byte[] b) throws IOException
    {
      return read(b, 0, b.length);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException
    {
      ensureCurrent();

      int length = Math.min(current.length - cursor, len);
      System.arraycopy(current, cursor, b, off, length);
      incrementBy(length);

      return length;
    }

    private void incrementBy(int count)
    {
      cursor += count;
      if (cursor >= current.length)
      {
        current = null;
      }
    }

    private void ensureCurrent() throws IOException
    {
      if (current == null)
      {
        try
        {
          current = queue.take();
          cursor = 0;
        }
        catch (InterruptedException e)
        {
          Thread.currentThread().interrupt();
          throw new IOException(e);
        }
      }
    }
  }

  static abstract class FromREPLOutputStream extends OutputStream
  {
    private final CharsetDecoder decoder;
    private final ByteBuffer input = ByteBuffer.allocate(8192);
    private final CharBuffer output = CharBuffer.allocate(8192);
    private final StringBuilder buffer = new StringBuilder();
    private boolean skipLF = false;

    public FromREPLOutputStream(Charset charset)
    {
      this.decoder = charset.newDecoder();
    }

    @Override
    public synchronized void write(int b) throws IOException
    {
      input.put((byte) b);
      decodeAndProcess();
    }

    @Override
    public void write(byte[] b) throws IOException
    {
      write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException
    {
      int cursor = off;
      while (cursor < (off + len))
      {
        int toCopy = Math.min(input.remaining(), len - (cursor - off));
        if (toCopy == 0)
        {
          throw new IOException("Can't write - buffer overflow");
        }
        input.put(b, cursor, toCopy);
        decodeAndProcess();
        cursor += toCopy;
      }
    }

    @Override
    public synchronized void flush() throws IOException
    {
      decodeAndProcess();
      if (buffer.length() > 0)
      {
        textAvailable(buffer.toString());
        buffer.setLength(0);
      }
    }

    private void decodeAndProcess() throws CharacterCodingException
    {
      input.flip();
      CoderResult result = decoder.decode(input, output, true);
      if (result.isError())
      {
        result.throwException();
      }
      output.flip();
      while (output.hasRemaining())
      {
        char ch = output.get();
        if (skipLF && ch != '\n')
        {
          buffer.append('\r');
        }
        if (ch == '\r')
        {
          skipLF = true;
        }
        else
        {
          skipLF = false;
          buffer.append(ch);
        }
        if (ch == '\n')
        {
          textAvailable(buffer.toString());
          buffer.setLength(0);
        }
      }
      input.compact();
      output.compact();
    }

    protected abstract void textAvailable(String text);
  }
}
