package schemely.parser;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
//import org.testng.annotations.AfterTest;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public class ParserTestBase
{
  protected Project project;
  protected Module module;
  protected IdeaProjectTestFixture fixture;

  protected void setUp()
  {
    if (fixture == null)
    {
      System.out.println("Running setUp for " + this.getClass().getSimpleName());
      fixture = createFixture();

      try
      {
        fixture.setUp();
      }
      catch (Exception e)
      {
        throw new Error(e);
      }
      module = fixture.getModule();
      project = module.getProject();
    }
  }

  protected IdeaProjectTestFixture createFixture()
  {
    TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder =
      IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
    return fixtureBuilder.getFixture();
  }

//  @AfterTest
  protected void tearDown()
  {
    try
    {
      fixture.tearDown();
    }
    catch (Exception e)
    {
      throw new Error(e);
    }
  }

  private PsiFile createPseudoPhysicalFile(Project project, String fileName, String text) throws
                                                                                          IncorrectOperationException
  {
    FileType fileType = FileTypeManager.getInstance().getFileTypeByFileName(fileName);
    PsiFileFactory psiFileFactory = PsiFileFactory.getInstance(project);
    return psiFileFactory.createFileFromText(fileName, fileType, text);
  }

  public PsiFile parse(String contents)
  {
    assert project != null : "Project is null";
    PsiFile psiFile = createPseudoPhysicalFile(project, "test.scm", contents);
    System.out.println(contents);
    dump(psiFile);
    return psiFile;
  }

  protected void dump(PsiElement element)
  {
    System.out.println(DebugUtil.psiToString(element, false));
  }

  protected boolean doesNotResolve(PsiElement element)
  {
    PsiReference reference = element.getReference();
    return (reference == null) || (reference.resolve() == null);
  }

  protected boolean resolvesToSelf(PsiElement element)
  {
    PsiReference reference = element.getReference();
    return (reference != null) && (reference.resolve() == reference);
  }

  protected boolean resolvesTo(PsiElement source, PsiElement target)
  {
    PsiReference reference = source.getReference();
    if (reference == null)
    {
      return false;
    }

    PsiElement resolved = reference.resolve();
    if ((resolved != null) && resolved.equals(target))
    {
      return true;
    }

    if (reference instanceof PsiPolyVariantReference)
    {
      PsiPolyVariantReference polyVariantReference = (PsiPolyVariantReference) reference;
      ResolveResult[] results = polyVariantReference.multiResolve(false);
      for (ResolveResult result : results)
      {
        PsiElement element = result.getElement();
        if ((element != null) && element.equals(target))
        {
          return true;
        }
      }
    }
    return false;
  }

  protected PsiElement first(PsiElement root, String text)
  {
    return item(root, text, 1);
  }

  protected PsiElement second(PsiElement root, String text)
  {
    return item(root, text, 2);
  }

  protected PsiElement third(PsiElement root, String text)
  {
    return item(root, text, 3);
  }

  protected PsiElement fourth(PsiElement root, String text)
  {
    return item(root, text, 4);
  }

  protected PsiElement fifth(PsiElement root, String text)
  {
    return item(root, text, 5);
  }

  protected PsiElement sixth(PsiElement root, String text)
  {
    return item(root, text, 6);
  }

  protected PsiElement item(PsiElement root, String text, int index)
  {
    int count = 0;
    for (PsiElement element : depthFirst(root))
    {
      if (element.getText().equals(text))
      {
        count++;
        if (count == index)
        {
          return element;
        }
      }
    }

    throw new IllegalArgumentException("Expected " + index + " occurrences of " + text + ", found " + count);
  }

  protected Collection<PsiElement> depthFirst(PsiElement element)
  {
    Collection<PsiElement> ret = new ArrayList<PsiElement>();
    doDepthFirst(element, ret);
    return ret;
  }

  private void doDepthFirst(PsiElement element, Collection<PsiElement> ret)
  {
    ret.add(element);
    for (PsiElement child : element.getChildren())
    {
      doDepthFirst(child, ret);
    }
  }
}
