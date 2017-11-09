package schemely.compiler;

import com.intellij.compiler.OutputParser;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.log4j.Logger;

/**
 * @author Colin Fleming
 */
public class SchemeOutputParser extends OutputParser
{
  private static final Logger log = Logger.getLogger(SchemeOutputParser.class);
  private static final String COMPILING_PREFIX = "(compiling ";
  private static final String WARNING_SUBSTRING = ": warning";

  private String errorPrefix = null;
  private String fileURL = null;

  @Override
  public boolean processMessageLine(Callback callback)
  {
    if (super.processMessageLine(callback)) {
      return true;
    }
    String line = callback.getCurrentLine();
    if (line == null) {
      return false;
    }

    if (line.startsWith(COMPILING_PREFIX))
    {
      int index = line.indexOf(" to ");
      if (index == -1)
      {
        index = line.indexOf(')');
      }
      if (index > 0)
      {
        String filename = line.substring(COMPILING_PREFIX.length(), index);
        errorPrefix = filename + ":";
        fileURL = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, filename);
      }

      if (line.endsWith(")"))
      {
        callback.setProgressText(line.substring(1, line.length() -1));
      }
      else
      {
        callback.setProgressText(line.substring(1));
      }
    }
    else if ((errorPrefix != null) && line.startsWith(errorPrefix))
    {
      int offset = errorPrefix.length();
      if (Character.isDigit(line.charAt(offset)))
      {
        int lineNum = 0;
        while (Character.isDigit(line.charAt(offset)))
        {
          lineNum = lineNum * 10 + (line.charAt(offset) - '0');
          offset++;
        }
        if (line.charAt(offset) == ':')
        {
          offset++;
          if (Character.isDigit(line.charAt(offset)))
          {
            int character = 0;
            while (Character.isDigit(line.charAt(offset)))
            {
              character = character * 10 + (line.charAt(offset) - '0');
              offset++;
            }

            callback.message(CompilerMessageCategory.ERROR, line, fileURL, lineNum, character);
            return true;
          }
        }

        callback.message(CompilerMessageCategory.ERROR, line, fileURL, lineNum, -1);
        return true;
      }

      callback.message(CompilerMessageCategory.ERROR, line, fileURL, -1, -1);
    }
    else if (line.contains(WARNING_SUBSTRING))
    {
      callback.message(CompilerMessageCategory.WARNING, line, null, -1, -1);
    }
    else
    {
      callback.message(CompilerMessageCategory.WARNING, line, null, -1, -1);
    }

    return true;
  }
}
