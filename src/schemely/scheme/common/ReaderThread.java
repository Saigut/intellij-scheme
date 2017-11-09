package schemely.scheme.common;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
* @author Colin Fleming
*/
public abstract class ReaderThread implements Runnable
{
  private static final Logger log = Logger.getLogger(ReaderThread.class);

  private final Reader myReader;
  private final AtomicBoolean terminated;
  private boolean skipLF = false;

  private final char[] myBuffer = new char[8192];

  public ReaderThread(Reader reader, AtomicBoolean terminated)
  {
    myReader = reader;
    this.terminated = terminated;
  }

  public void run()
  {
    try
    {
      while (true)
      {
        int rc = readAvailable();
        if (rc == DONE)
        {
          break;
        }
        Thread.sleep(rc == READ_SOME ? 1L : 50L);
      }
    }
    catch (InterruptedException ignore)
    {
    }
    catch (IOException e)
    {
      log.error("Error reading", e);
    }
    catch (Exception e)
    {
      log.error("Error reading", e);
    }
  }

  private static final int DONE = 0;
  private static final int READ_SOME = 1;
  private static final int READ_NONE = 2;

  private synchronized int readAvailable() throws IOException
  {
    char[] buffer = myBuffer;
    StringBuilder token = new StringBuilder();
    int rc = READ_NONE;
    while (myReader.ready())
    {
      int n = myReader.read(buffer);
      if (n <= 0)
      {
        break;
      }
      rc = READ_SOME;

      for (int i = 0; i < n; i++)
      {
        char c = buffer[i];
        if (skipLF && c != '\n')
        {
          token.append('\r');
        }

        if (c == '\r')
        {
          skipLF = true;
        }
        else
        {
          skipLF = false;
          token.append(c);
        }

        if (c == '\n')
        {
          textAvailable(token.toString());
          token.setLength(0);
        }
      }
    }

    if (token.length() != 0)
    {
      textAvailable(token.toString());
      token.setLength(0);
    }

    if (terminated.get())
    {
      try
      {
        myReader.close();
      }
      catch (IOException ignored)
      {
      }

      return DONE;
    }

    return rc;
  }

  protected abstract void textAvailable(String s);
}
