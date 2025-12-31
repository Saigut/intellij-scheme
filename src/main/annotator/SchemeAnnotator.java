package main.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import main.parser.AST;
import main.psi.impl.*;
import org.jetbrains.annotations.NotNull;
import main.highlighter.SchemeSyntaxHighlighter;

import static main.lexer.SchemeTokens.COMMENTS;


public class SchemeAnnotator implements Annotator
{
  private final SchemeSyntaxHighlighter syntaxHighlighter;

  SchemeAnnotator(SchemeSyntaxHighlighter syntaxHighlighter) {
    this.syntaxHighlighter = syntaxHighlighter;
  }

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
  {
    IElementType ele_type = element.getNode().getElementType();
    if (COMMENTS.contains(ele_type)) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(syntaxHighlighter.COMMENT);
    } else if (ele_type == AST.AST_BASIC_ELE_SYMBOL) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(syntaxHighlighter.LITERAL);
    } else if (element instanceof SchemeEleChar) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(syntaxHighlighter.CHAR);
    } else if (element instanceof SchemeBadCharacter/* || element instanceof SchemeBadElement*/) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(syntaxHighlighter.BAD_CHARACTER);
    } else if (element instanceof SchemeEleString) {
      holder.createInfoAnnotation(element, null)
              .setTextAttributes(syntaxHighlighter.STRING);

      String eStr = "abtnvfr\"\\\t\r\n";
      String str = element.getText();
      int strLen = str.length();
      int startOffset = element.getTextRange().getStartOffset();
      int idx = 0;
      int slashPos;
      char nextChar;

      slashPos = str.indexOf('\\', idx);
      while (slashPos >= 0 && idx + 1 < strLen) {
        if (slashPos + 1 >= strLen) {
          break;
        }
        nextChar = str.charAt(slashPos + 1);
        if (nextChar == 'x') {
          idx = slashPos + 2;
          nextChar = str.charAt(idx);
          while (((nextChar >= '0' && nextChar <= '9')
                  || (nextChar >= 'a' && nextChar <= 'f')
                  || (nextChar >= 'A' && nextChar <= 'F')) && idx + 2 < strLen) {
            idx++;
            nextChar = str.charAt(idx);
          }
          if (idx > slashPos + 2) {
            if (str.charAt(idx) == ';') {
              holder.createInfoAnnotation(new TextRange(
                              startOffset + slashPos,
                              startOffset + idx + 1), null)
                      .setTextAttributes(syntaxHighlighter.STRING_ESCAPE);
              idx = idx + 1;
            }
          }
        } else if (eStr.indexOf(nextChar) >= 0) {
          holder.createInfoAnnotation(new TextRange(
                          startOffset + slashPos,
                          startOffset + slashPos + 2), null)
                  .setTextAttributes(syntaxHighlighter.STRING_ESCAPE);
          idx = slashPos + 2;
        } else {
          idx = slashPos + 2;
        }
        if (idx + 1 >= strLen) {
          break;
        }
        slashPos = str.indexOf('\\', idx);
      }
    }
  }
}
