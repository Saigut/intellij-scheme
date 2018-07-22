package schemely.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import schemely.highlighter.SchemeSyntaxHighlighter;
import schemely.lexer.Tokens;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveUtil;
import schemely.psi.util.SchemePsiUtil;

import java.util.Arrays;
import java.util.Set;


public class SchemeAnnotator implements Annotator
{
  public static final
  Set<String>
    IMPLICIT_NAMES =
    new HashSet<String>(Arrays.asList("define",
                                      "quote",
                                      "quasiquote",
                                      "unquote",
                                      "unquote-splicing",
                                      "lambda",
                                      "define",
                                      "define-syntax",
                                      "if",
                                      "else",
                                      "let",
                                      "let*",
                                      "letrec",
                                      "set!",
                                      "begin",
                                      "cond",
                                      "and",
                                      "or",
                                      "case",
                                      "do",
                                      "delay",
                                      "let-syntax",
                                      "letrec-syntax"));

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
  {
    if (ResolveUtil.getQuotingLevel(element) > 0)
    {
      if (element instanceof SchemeIdentifier)
      {
        Annotation annotation = holder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
      }
      else if (element instanceof SchemeLiteral)
      {
        Annotation annotation = holder.createInfoAnnotation(element, null);
        if (SchemePsiUtil.isStringLiteral(element))
        {
          annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_STRING);
        }
        else if (SchemePsiUtil.isNumberLiteral(element))
        {
          annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_NUMBER);
        }
        else
        {
          annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
        }
      }
      else if (element instanceof SchemeList || element instanceof SchemeVector)
      {
        ASTNode node = element.getNode();
        for (ASTNode child : node.getChildren(Tokens.BRACES))
        {
          Annotation annotation = holder.createInfoAnnotation(child, null);
          annotation.setTextAttributes(SchemeSyntaxHighlighter.QUOTED_TEXT);
        }
      }
    }
    else if (element instanceof SchemeList)
    {
      annotateList((SchemeList) element, holder);
    }
//    else if (element instanceof SchemeIdentifier)
//    {
//      SchemeIdentifier identifier = (SchemeIdentifier) element;
//      if (identifier.resolve() == null)
//      {
//        holder.createWarningAnnotation(identifier, "Can't resolve");
//      }
//    }
  }

  private void annotateList(SchemeList list, AnnotationHolder holder)
  {
    SchemeIdentifier first = list.getFirstIdentifier();

//    PsiElement second = list.getSecondNonLeafElement();
//    StringBuffer buffer = new StringBuffer();
//    buffer.append(first == null ? "null" : '"' + first.getReferenceName() + '"');
//    buffer.append(" ");
//    buffer.append(second == null ? "null" : '"' + second.getText() + '"');
//    System.out.println(buffer.toString());

    if ((first != null) && IMPLICIT_NAMES.contains(first.getReferenceName()))
    {
      Annotation annotation = holder.createInfoAnnotation(first, null);
      annotation.setTextAttributes(SchemeSyntaxHighlighter.KEYWORD);
    }
  }
}
