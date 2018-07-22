package schemely.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import com.intellij.testFramework.LightCodeInsightTestCase;
import org.intellij.lang.annotations.Language;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Colin Fleming
 */
public class ResolveTest extends LightCodeInsightTestCase
{
  public void testBasicDefine() throws IOException
  {
    parse("(define x 3) x");
    doesNotResolve(first(myFile, "x"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
  }

  public void testBasicLambda() throws IOException
  {
    parse("(lambda (x) (+ x 3)) x");
    doesNotResolve(first(myFile, "x"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
  }

  public void testLambda2Params() throws IOException
  {
    parse("(lambda (x y) (+ x y)) x y");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
    doesNotResolveTo(third(myFile, "y"), first(myFile, "y"));
  }

  public void testDottedLambdaParams() throws IOException
  {
    parse("(lambda (x . y) (+ x y)) x y");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
    doesNotResolveTo(third(myFile, "y"), first(myFile, "y"));
  }

  public void testListLambdaParams() throws IOException
  {
    parse("(lambda x (head x)) x");
    doesNotResolve(first(myFile, "x"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
  }

  public void testBasicFunctionDefine() throws IOException
  {
    parse("(define f (lambda (x) (+ x 2))) (f 5)");
    doesNotResolve(first(myFile, "f"));
    resolvesTo(second(myFile, "f"), first(myFile, "f"));
  }

  public void testBasicDefunStyle() throws IOException
  {
    parse("(define (f x) (+ x 2)) (f 5) x");
    doesNotResolve(first(myFile, "f"));
    resolvesTo(second(myFile, "f"), first(myFile, "f"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
  }

  public void testDefunReturnArgument() throws IOException
  {
    parse("(define (f x y) x y) (f 5) x");
    doesNotResolve(first(myFile, "f"));
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "f"), first(myFile, "f"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolveTo(third(myFile, "x"), first(myFile, "x"));
  }

  public void testDefunNoArgs() throws IOException
  {
    parse("(define (f) 5) (f)");
    doesNotResolve(first(myFile, "f"));
    resolvesTo(second(myFile, "f"), first(myFile, "f"));
  }

  public void testLambdaInternalDefine() throws IOException
  {
    parse("(lambda (x) (define y 4) (+ x y)) y");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolveTo(third(myFile, "y"), first(myFile, "y"));
  }

  public void testLambdaInternalDefineSeesParameters() throws IOException
  {
    parse("(lambda (x) (define y (+ 3 x)) (+ x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLambdaInternalDefineShadowsParameter() throws IOException
  {
    parse("(lambda (x) (define x 4) (+ x 3))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(second(myFile, "x"));
    resolvesTo(third(myFile, "x"), second(myFile, "x"));
  }

  public void testLambdaInternalDefineLetrecShadowing() throws IOException
  {
    parse("(lambda (x y) (define x 7) (define y x) (+ x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    doesNotResolve(second(myFile, "x"));
    doesNotResolve(second(myFile, "y"));
    resolvesTo(third(myFile, "x"), second(myFile, "x"));
    resolvesTo(fourth(myFile, "x"), second(myFile, "x"));
    resolvesTo(third(myFile, "y"), second(myFile, "y"));
  }

  public void testBasicLet() throws IOException
  {
    parse("(let ((x 2) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLetVarsDontSeeEachOther() throws IOException
  {
    parse("(lambda (x) (let ((x 7) (y (+ x 3))) (* y x)))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(second(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    resolvesTo(fourth(myFile, "x"), second(myFile, "x"));
  }

  public void testLetShadowing() throws IOException
  {
    parse("(let ((x 3)) (let ((x 5)) x))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(second(myFile, "x"));
    resolvesTo(third(myFile, "x"), second(myFile, "x"));
  }

  public void testLetInternalDefineShadowing() throws IOException
  {
    parse("(let ((x 3) (y 4)) (define x y) (define y x) x y)");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    doesNotResolve(second(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolve(third(myFile, "y"));
    resolvesTo(third(myFile, "x"), second(myFile, "x"));
    resolvesTo(fourth(myFile, "x"), second(myFile, "x"));
    resolvesTo(fourth(myFile, "y"), third(myFile, "y"));
  }

  public void testEmbeddedLet() throws IOException
  {
    parse("(let ((x (let ((y 5)) y)) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    doesNotResolve(third(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(fourth(myFile, "y"), third(myFile, "y"));
  }

  public void testBasicLetStar() throws IOException
  {
    parse("(let* ((x 2) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLetStarSeesPreviousBindings() throws IOException
  {
    parse("(let* ((x 2) (y x)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLetStarDoesntSeeLaterBindings() throws IOException
  {
    parse("(let* ((x y) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    doesNotResolve(second(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "y"), second(myFile, "y"));
  }


  public void testBasicLetRec() throws IOException
  {
    parse("(letrec ((x 2) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLetRecSeesPreviousBindings() throws IOException
  {
    parse("(letrec ((x 2) (y x)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testLetRecSeesLaterBindings() throws IOException
  {
    parse("(letrec ((x y) (y 3)) (* x y))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(second(myFile, "y"));
    resolvesTo(first(myFile, "y"), second(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "y"), second(myFile, "y"));
  }

  public void testBasicNamedLet() throws IOException
  {
    parse("(let loop ((x 2) (y 3)) (loop (+ x 1) (+ y 1)))");
    doesNotResolve(first(myFile, "loop"));
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "loop"), first(myFile, "loop"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
  }

  public void testDo() throws IOException
  {
    parse("(do ((x 2) (y 3 (+ y 1))) ((= x y) y) x y)");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(first(myFile, "y"));
    resolvesTo(second(myFile, "x"), first(myFile, "x"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(second(myFile, "y"), first(myFile, "y"));
    resolvesTo(third(myFile, "y"), first(myFile, "y"));
    resolvesTo(fourth(myFile, "y"), first(myFile, "y"));
    resolvesTo(fifth(myFile, "y"), first(myFile, "y"));
  }

  public void testDoInitDoesntResolve() throws IOException
  {
    parse("(let ((x 2)) (do ((x x)) ((= x 3) x) x))");
    doesNotResolve(first(myFile, "x"));
    doesNotResolve(second(myFile, "x"));
    resolvesTo(third(myFile, "x"), first(myFile, "x"));
    resolvesTo(fourth(myFile, "x"), second(myFile, "x"));
    resolvesTo(fifth(myFile, "x"), second(myFile, "x"));
  }

  public void testRecursiveFunction() throws IOException
  {
    parse("(define eternity (lambda () (eternity)))");
    doesNotResolve(first(myFile, "eternity"));
    resolvesTo(second(myFile, "eternity"), first(myFile, "eternity"));
  }

  public void testRecursiveDefun() throws IOException
  {
    parse("(define (eternity) (eternity))");
    doesNotResolve(first(myFile, "eternity"));
    resolvesTo(second(myFile, "eternity"), first(myFile, "eternity"));
  }

  private void parse(@Language("Scheme") String contents) throws IOException
  {
    configureFromFileText("resolve-test.scm", contents);
  }


  protected void doesNotResolve(PsiElement element)
  {
    PsiReference reference = element.getReference();
    assertTrue(element.getText() + " should not resolve", (reference == null) || (reference.resolve() == null));
  }

  protected void resolvesTo(PsiElement source, PsiElement target)
  {
    assertTrue(checkResolvesTo(source, target));
  }

  protected void doesNotResolveTo(PsiElement source, PsiElement target)
  {
    assertFalse(checkResolvesTo(source, target));
  }

  protected boolean checkResolvesTo(PsiElement source, PsiElement target)
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
