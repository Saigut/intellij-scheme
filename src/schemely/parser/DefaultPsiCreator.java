package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import schemely.psi.impl.SchemeFile;
import schemely.psi.impl.SchemeLiteral;
import schemely.psi.impl.SchemeQuoted;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;


public class DefaultPsiCreator implements SchemePsiCreator
{
  public PsiElement createElement(ASTNode node)
  {
    IElementType elementType = node.getElementType();

    if (elementType == AST.AST_LIST)
    {
      return new SchemeList(node);
    }
    if (elementType == AST.AST_VECTOR)
    {
      return new SchemeVector(node);
    }
    if (elementType == AST.AST_QUOTED)
    {
      return new SchemeQuoted(node);
    }
    if (elementType == AST.AST_BACKQUOTED)
    {
      return new SchemeQuoted(node);
    }
    if (elementType == AST.AST_IDENTIFIER)
    {
      return new SchemeIdentifier(node);
    }
    if (elementType == AST.AST_KEYWORD)
    {
      /// fixme   SchemeIdentifier() has low efficiency
      return new SchemeLiteral(node);
    }
    if (elementType == AST.AST_SPECIAL)
    {
      return new SchemeIdentifier(node);
    }
    if (elementType == AST.AST_PLAIN_LITERAL)
    {
//      return new SchemeLiteral(node);
      return new SchemeIdentifier(node);
    }
    if (elementType == AST.AST_OTHER_LITERAL)
    {
      return new SchemeLiteral(node);
    }

    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SchemeFile(viewProvider);
  }
}
