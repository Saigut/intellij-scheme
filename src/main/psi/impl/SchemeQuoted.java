package main.psi.impl;

import com.intellij.lang.ASTNode;


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
