package schemely.repl;

import net.jcip.annotations.GuardedBy;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;

/**
 * Copied from IDEA core and heavily modified.
 *
 * @author Colin Fleming
 * @author Gregory.Shrago
 */
public class ConsoleHistoryModel
{
  public static final int DEFAULT_MAX_SIZE = 100;

  @GuardedBy("history")
  private int historyCursor = -1;
  @GuardedBy("history")
  private int maxHistorySize = DEFAULT_MAX_SIZE;
  @GuardedBy("history")
  private final LinkedList<String> history = new LinkedList<String>();


  public boolean isEditingCurrentItem()
  {
    synchronized (history)
    {
      return historyCursor == -1;
    }
  }

  public void addToHistory(String statement)
  {
    synchronized (history)
    {
      historyCursor = -1;
      history.remove(statement);
      if (history.size() >= maxHistorySize)
      {
        history.removeLast();
      }
      history.addFirst(statement);
    }
  }

  @Nullable
  public String getHistoryPrev()
  {
    synchronized (history)
    {
      if (historyCursor < history.size() - 1)
      {
        return history.get(++historyCursor);
      }
      else
      {
        return null;
      }
    }
  }

  @Nullable
  public String getHistoryNext()
  {
    synchronized (history)
    {
      if (historyCursor > 0)
      {
        return history.get(--historyCursor);
      }
      else
      {
        if (historyCursor == 0)
        {
          historyCursor--;
        }
        return null;
      }
    }
  }

  public boolean hasPreviousHistory()
  {
    synchronized (history)
    {
      return historyCursor < history.size() - 1;
    }
  }

  public boolean hasNextHistory()
  {
    synchronized (history)
    {
      return historyCursor >= 0;
    }
  }
}
