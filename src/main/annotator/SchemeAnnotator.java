package main.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import main.highlighter.SchemeSyntaxHighlighter;
import main.psi.impl.SchemeEleDatumComment;


public class SchemeAnnotator implements Annotator
{
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
  {
    if (element instanceof SchemeEleDatumComment) {
      Annotation annotation = holder.createInfoAnnotation(element, null);
      annotation.setTextAttributes(SchemeSyntaxHighlighter.COMMENT);
    }
  }
}
