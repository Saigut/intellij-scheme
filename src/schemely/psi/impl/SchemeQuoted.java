package schemely.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import schemely.lexer.Tokens;


public class SchemeQuoted extends SchemePsiElementBase
{
  public SchemeQuoted(ASTNode node)
  {
    super(node, "Quoted");
  }

  @Override
  public String toString()
  {
    return "SchemeQuoted";
  }
}
