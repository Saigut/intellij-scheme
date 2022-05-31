package main.psi.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import main.parser.AST;


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

  public static PsiElement getNormalChildAt(PsiElement element, int index)
  {
    if (null == element)
    {
      return null;
    }
    if (index < 0)
    {
      return null;
    }

    PsiElement child;
    child = element.getFirstChild();
    if (null == child)
    {
      return null;
    }

    IElementType eleType;
    while (true)
    {
      eleType = child.getNode().getElementType();
      if (AST.AST_ELEMENTS.contains(eleType))
      {
        index--;
      }

      if (index < 0)
      {
        break;
      }

      child = child.getNextSibling();
      if (null == child)
      {
        break;
      }
    }

    return child;
  }

  public static PsiElement getBigBrother(PsiElement element)
  {
    if (null == element)
    {
      return null;
    }
    PsiElement bigBrother;
    bigBrother = element.getPrevSibling();
    if (null != bigBrother)
    {
      return bigBrother;
    }

    PsiElement parent;
    parent = element.getParent();
    if (null == parent)
    {
      return null;
    }
    else
    {
      if(parent instanceof PsiFile)
      {
        return null;
      }

      bigBrother = parent.getPrevSibling();
      if (null == bigBrother)
      {
        return null;
      }
      else
      {
        return bigBrother;
      }
    }
  }
}
