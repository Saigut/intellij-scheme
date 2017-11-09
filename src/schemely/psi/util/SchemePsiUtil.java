package schemely.psi.util;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;
import schemely.psi.api.SchemeBraced;
import schemely.psi.impl.SchemeFile;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.list.SchemeList;


public class SchemePsiUtil
{
  public static <T> T findNextSiblingByClass(PsiElement element, Class<T> aClass)
  {
    PsiElement next = element.getNextSibling();
    while (next != null && !aClass.isInstance(next))
    {
      next = next.getNextSibling();
    }
    return aClass.cast(next);
  }

  @Nullable
  public static SchemeList findSexpAtCaret(@NotNull Editor editor, boolean previous)
  {
    Project project = editor.getProject();
    if (project == null)
    {
      return null;
    }

    VirtualFile vfile = FileDocumentManager.getInstance().getFile(editor.getDocument());

    if (vfile == null)
    {
      return null;
    }

    PsiFile file = PsiManager.getInstance(project).findFile(vfile);
    if (file == null)
    {
      return null;
    }

    CharSequence chars = editor.getDocument().getCharsSequence();
    int offset = editor.getCaretModel().getOffset();
    if (previous)
    {
      while ((offset != 0) && (offset < chars.length()) && ("]})".indexOf(chars.charAt(offset)) < 0))
      {
        offset--;
      }
    }
    if (offset == 0)
    {
      return null;
    }

    PsiElement element = file.findElementAt(offset);
    while ((element != null) && (!(element instanceof SchemeList)))
    {
      element = element.getParent();
    }
    return (SchemeList) element;
  }

  @Nullable
  public static SchemeList findTopSexpAroundCaret(@NotNull Editor editor)
  {
    Project project = editor.getProject();
    if (project == null)
    {
      return null;
    }

    Document document = editor.getDocument();
    PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
    if (file == null)
    {
      return null;
    }

    PsiElement element = file.findElementAt(editor.getCaretModel().getOffset());
    SchemeList sexp = null;
    while (element != null)
    {
      if ((element instanceof SchemeList))
      {
        sexp = (SchemeList) element;
      }
      element = element.getParent();
    }
    return sexp;
  }

  public static boolean isValidSchemeExpression(String text, @NotNull Project project)
  {
    if (text == null)
    {
      return false;
    }
    text = text.trim();
    SchemePsiElementFactory factory = SchemePsiElementFactory.getInstance(project);
    SchemeFile file = factory.createSchemeFileFromText(text);
    PsiElement[] children = file.getChildren();

    if (children.length == 0)
    {
      return false;
    }
    for (PsiElement child : children)
    {
      if (containsSyntaxErrors(child))
      {
        return false;
      }
    }

    return true;
  }

  public static boolean containsSyntaxErrors(PsiElement elem)
  {
    if ((elem instanceof PsiErrorElement))
    {
      return true;
    }
    for (PsiElement child : elem.getChildren())
    {
      if (containsSyntaxErrors(child))
      {
        return true;
      }
    }
    return false;
  }

  public static boolean isStringLiteral(PsiElement element)
  {
    if (!(element instanceof SchemeLiteral))
    {
      return false;
    }
    ASTNode node = element.getNode();
    if (node == null)
    {
      return false;
    }
    ASTNode[] children = node.getChildren(null);
    return children.length == 1 && (children[0].getElementType() == Tokens.STRING_LITERAL);
  }

  public static boolean isNumberLiteral(PsiElement element)
  {
    if (!(element instanceof SchemeLiteral))
    {
      return false;
    }
    ASTNode node = element.getNode();
    if (node == null)
    {
      return false;
    }
    ASTNode[] children = node.getChildren(null);
    return children.length == 1 && (children[0].getElementType() == Tokens.NUMBER_LITERAL);
  }
}
