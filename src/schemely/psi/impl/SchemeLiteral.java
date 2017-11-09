package schemely.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeLiteral extends SchemePsiElementBase
{
  public SchemeLiteral(ASTNode node)
  {
    super(node, "SchemeLiteral");
  }
}
