package main.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
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

  public SchemeAnnotator() {
    this.syntaxHighlighter = new SchemeSyntaxHighlighter();
  }

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder)
  {
    IElementType ele_type = element.getNode().getElementType();
    if (COMMENTS.contains(ele_type)) {
      holder.newAnnotation(HighlightSeverity.INFORMATION, "")
              .range(element)
              .textAttributes(syntaxHighlighter.COMMENT)
              .create();
    } else if (ele_type == AST.AST_BASIC_ELE_SYMBOL) {
      holder.newAnnotation(HighlightSeverity.INFORMATION, "")
              .range(element)
              .textAttributes(syntaxHighlighter.LITERAL)
              .create();
    } else if (element instanceof SchemeEleChar) {
      holder.newAnnotation(HighlightSeverity.INFORMATION, "")
              .range(element)
              .textAttributes(syntaxHighlighter.CHAR)
              .create();
    } else if (element instanceof SchemeBadCharacter/* || element instanceof SchemeBadElement*/) {
      holder.newAnnotation(HighlightSeverity.INFORMATION, "")
              .range(element)
              .textAttributes(syntaxHighlighter.BAD_CHARACTER)
              .create();
    } else if (element instanceof SchemeEleString) {
      holder.newAnnotation(HighlightSeverity.INFORMATION, "")
              .range(element)
              .textAttributes(syntaxHighlighter.STRING)
              .create();

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
              holder.newAnnotation(HighlightSeverity.INFORMATION, "")
                      .range(new TextRange(
                              startOffset + slashPos,
                              startOffset + idx + 1))
                      .textAttributes(syntaxHighlighter.STRING_ESCAPE)
                      .create();
              idx = idx + 1;
            }
          }
        } else if (eStr.indexOf(nextChar) >= 0) {
          holder.newAnnotation(HighlightSeverity.INFORMATION, "")
                  .range(new TextRange(
                          startOffset + slashPos,
                          startOffset + slashPos + 2))
                  .textAttributes(syntaxHighlighter.STRING_ESCAPE)
                  .create();
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
