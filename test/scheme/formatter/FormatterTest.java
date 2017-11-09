package schemely.formatter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.LightCodeInsightTestCase;

import java.io.File;
import java.io.IOException;

/**
 * @author Colin Fleming
 */
public class FormatterTest extends LightCodeInsightTestCase
{
  public void testIndentQuoted() throws IOException
  {
    doTest();
  }

  public void testInlineComment() throws IOException
  {
    doTest();
  }

  public void testInlineComment2() throws IOException
  {
    doTest();
  }

  public void testIssue18() throws IOException
  {
    doTest();
  }

  public void testListOfLists() throws IOException
  {
    doTest();
  }

  public void doTest() throws IOException
  {
    // TODO: Currently requires idea.home.path to be set to the project root
    String homePath = PathManager.getHomePath();
    String testDataPath = homePath + "/testData/formatter";

    String testName = getTestName();
    String source = doLoadFile(testDataPath, testName + ".scm");
    String result = doLoadFile(testDataPath, testName + ".result.scm");

    configureFromFileText("formatting.scm", source);

    CodeStyleManager manager = CodeStyleManager.getInstance(ourProject);
    manager.reformatText(myFile, 0, myFile.getTextLength());

    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      @Override
      public void run()
      {
        PsiDocumentManager.getInstance(getProject()).commitAllDocuments();
      }
    });

    String formatted = myFile.getText();
    assertEquals(result, formatted);
  }

  private String getTestName()
  {
    String name = getName();
    assertTrue("Test name should start with 'test': " + name, name.startsWith("test"));
    name = name.substring("test".length());
    StringBuilder builder = new StringBuilder();
    boolean lastLowerCase = false;
    int i = 0;
    while (i < name.length())
    {
      char ch = name.charAt(i);
      if (Character.isUpperCase(ch))
      {
        if (lastLowerCase)
        {
          builder.append("-");
        }
        builder.append(Character.toLowerCase(ch));
      }
      else
      {
        builder.append(ch);
        lastLowerCase = true;
      }
      i++;
    }
    return builder.toString();
  }

  private static String doLoadFile(String myFullDataPath, String name) throws IOException
  {
    String fullName = myFullDataPath + File.separatorChar + name;
    String text = new String(FileUtil.loadFileText(new File(fullName))).trim();
    text = StringUtil.convertLineSeparators(text);
    return text;
  }
}
