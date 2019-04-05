package schemely.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeKeyword extends SchemePsiElementBase
{
  public SchemeKeyword(ASTNode node)
  {
    super(node, "SchemeKeyword");
  }
}
