package main.psi.util;

import com.intellij.lang.ASTNode;
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

    while (true)
    {
      if (child.getFirstChild() != null) {
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

  public static PsiElement getPsiNextNonLeafSibling(PsiElement element)
  {
    PsiElement child = element.getNextSibling();
    while (child != null) {
      if (child.getFirstChild() != null) {
        return child;
      }
      child = child.getNextSibling();
    }
    return null;
  }

  public static PsiElement getPsiLastNonLeafChild(PsiElement element)
  {
    PsiElement child = element.getLastChild();
    while (child != null) {
      if (child.getFirstChild() != null) {
        return child;
      }
      child = child.getPrevSibling();
    }
    return null;
  }

  public static ASTNode getNodeNormalChildAt(ASTNode node, int i)
  {
    if (i < 0) {
      return null;
    }
    ASTNode child = node.getFirstChildNode();
    int idx = 0;
    while (child != null) {
      if (AST.AST_ELEMENTS.contains(child.getElementType())) {
        if (idx >= i) {
          return child;
        } else {
          idx++;
        }
      }
      child = child.getTreeNext();
    }
    return null;
  }

  public static ASTNode getNonLeafChildAt(ASTNode node, int i)
  {
    if (i < 0) {
      return null;
    }
    ASTNode child = node.getFirstChildNode();
    int idx = 0;
    while (child != null) {
      if (child.getFirstChildNode() != null
              && child.getElementType() != AST.AST_ELE_DATUM_COMMENT) {
        if (idx >= i) {
          return child;
        } else {
          idx++;
        }
      }
      child = child.getTreeNext();
    }
    return null;
  }

  public static ASTNode getNodeNextNonLeafSibling(ASTNode node)
  {
    ASTNode child = node.getTreeNext();
    while (child != null) {
      if (child.getFirstChildNode() != null
              && child.getElementType() != AST.AST_ELE_DATUM_COMMENT) {
        return child;
      }
      child = child.getTreeNext();
    }
    return null;
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
      return getBigBrother(parent);
    }
  }
}
