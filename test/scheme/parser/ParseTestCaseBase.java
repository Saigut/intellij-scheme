package schemely.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Language;
//import org.testng.annotations.Test;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;

/**
 * @author Colin Fleming
 */
public class ParseTestCaseBase extends ParserTestBase
{
//  @Test(dataProvider = "parseFiles", groups = "Schemely")
  public void parseTest(ParseTestCase testCase)
  {
    PsiFile psiFile = parse(testCase.contents);
    PsiElement[] children = psiFile.getChildren();
    assert children.length == 1 : "Expecting 1 child, found " + children.length;
    PsiElement child = children[0];
    assert testCase.theClass.isAssignableFrom(child.getClass()) :
      "Expected " + testCase.theClass.getName() + ", found " + child.getClass().getName();
    testCase.extraChecks(child);
  }

  protected ParseTestCaseBase.ParseTestCase list(String contents)
  {
    return element(contents, SchemeList.class);
  }


  protected ParseTestCaseBase.ParseTestCase improperList(String contents)
  {
    return new ParseTestCaseBase.ParseTestCase(contents, SchemeList.class)
    {
      @Override
      protected void extraChecks(PsiElement element)
      {
        assert ((SchemeList) element).isImproper() : "Expected improper list";
      }
    };
  }

  protected ParseTestCase literal(String contents)
  {
    return element(contents, SchemeLiteral.class);
  }

  protected ParseTestCase identifier(String contents)
  {
    return element(contents, SchemeIdentifier.class);
  }

  protected <T extends PsiElement> ParseTestCase element(String contents, Class<T> theClass)
  {
    return new ParseTestCase(contents, theClass);
  }

  public static class ParseTestCase
  {
    public final String contents;
    public final Class<? extends PsiElement> theClass;

    public ParseTestCase(String contents, Class<? extends PsiElement> theClass)
    {
      this.contents = contents;
      this.theClass = theClass;
    }

    protected void extraChecks(PsiElement element)
    {
    }

    @Override
    public String toString()
    {
      return contents;
    }
  }
}
