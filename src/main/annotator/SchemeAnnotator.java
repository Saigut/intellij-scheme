package main.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import main.lexer.SchemeTokens;
import main.parser.AST;
import main.psi.impl.SchemeBadCharacter;
import main.psi.impl.SchemeEleChar;
import org.jetbrains.annotations.NotNull;
import main.highlighter.SchemeSyntaxHighlighter;
import main.psi.impl.SchemeEleDatumComment;

import static main.lexer.SchemeTokens.COMMENTS;


public class SchemeAnnotator implements Annotator
{
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
  {
    if (COMMENTS.contains(element.getNode().getElementType())) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(SchemeSyntaxHighlighter.COMMENT);
    } else if (element instanceof SchemeEleChar) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(SchemeSyntaxHighlighter.CHAR);
    } else if (element instanceof SchemeBadCharacter) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(SchemeSyntaxHighlighter.BAD_CHARACTER);
    }
  }
}
