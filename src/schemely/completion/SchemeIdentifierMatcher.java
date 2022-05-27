package schemely.completion;

import com.intellij.codeInsight.completion.PrefixMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.util.text.Matcher;
import com.intellij.util.containers.hash.LinkedHashMap;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.RunAutomaton;
import net.jcip.annotations.GuardedBy;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author Colin Fleming
 */
public class SchemeIdentifierMatcher extends PrefixMatcher
{
  // All valid scheme subsequent chars
  private static final String SCHEME_SUBSEQUENT = "[-A-Za-z0-9!$%&*/:<=>?~_^@\\.+]";
  // All valid scheme subsequent chars except '-'
  private static final String SCHEME_SUBSEQUENT_INTER_DASH = "[A-Za-z0-9!$%&*/:<=>?~_^@\\.+]";

  private static final int MAX_LENGTH = 40;

  @GuardedBy("patternCache")
  private static final Map<String, Matcher> patternCache = new LinkedHashMap<String, Matcher>()
  {
    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Matcher> eldest)
    {
      return size() > 10;
    }
  };

  private final boolean caseSensitive;
  private Matcher matcher;

  public SchemeIdentifierMatcher(String prefix)
  {
    // TODO configure case sensitivity
    this(prefix, false);
  }

  public SchemeIdentifierMatcher(String prefix, boolean caseSensitive)
  {
    super(prefix);
    this.caseSensitive = caseSensitive;
  }

  @Override
  public boolean prefixMatches(@NotNull String name)
  {
    synchronized (patternCache)
    {
      if (matcher == null)
      {
        Matcher pattern = patternCache.get(myPrefix);
        if (pattern == null)
        {
          pattern = createMatcher();
          patternCache.put(myPrefix, pattern);
        }
        matcher = pattern;
      }
      return matcher.matches(name);
    }
  }

  private Matcher createMatcher()
  {
    String pattern = myPrefix;
    int eol = pattern.indexOf('\n');
    if (eol != -1)
    {
      pattern = pattern.substring(0, eol);
    }
    if (pattern.length() >= MAX_LENGTH)
    {
      pattern = pattern.substring(0, MAX_LENGTH);
    }

    boolean previousWasNormalChar = false;

    StringBuilder regexp = new StringBuilder();
    for (int i = 0; i < pattern.length(); i++)
    {
      char ch = pattern.charAt(i);
      if (Character.isLetter(ch))
      {
        if (caseSensitive)
        {
          regexp.append(ch);
        }
        else
        {
          regexp.append('[');
          regexp.append(Character.toLowerCase(ch));
          regexp.append(Character.toUpperCase(ch));
          regexp.append(']');
        }
        previousWasNormalChar = true;
      }
      else if (Character.isDigit(ch))
      {
        regexp.append(ch);
        previousWasNormalChar = true;
      }
      else if (ch == '*')
      {
        regexp.append(SCHEME_SUBSEQUENT + '*');
        previousWasNormalChar = false;
      }
      else if ((ch == '-') && previousWasNormalChar)
      {
        regexp.append(SCHEME_SUBSEQUENT_INTER_DASH + '*');
        regexp.append("\\-");
        previousWasNormalChar = false;
      }
      else
      {
        regexp.append("\\").append(ch);
        previousWasNormalChar = true;
      }
    }

    regexp.append(SCHEME_SUBSEQUENT + '*');

    final RunAutomaton automaton = new RunAutomaton(new RegExp(regexp.toString(), RegExp.NONE).toAutomaton());
    return new Matcher()
    {
      @Override
      public boolean matches(String name)
      {
        return automaton.run(name);
      }
    };
  }

  @NotNull
  @Override
  public PrefixMatcher cloneWithPrefix(@NotNull String prefix)
  {
    return new SchemeIdentifierMatcher(prefix);
  }
}
