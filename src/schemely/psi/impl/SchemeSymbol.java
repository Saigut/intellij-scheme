package schemely.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeSymbol extends SchemePsiElementBase
{
  public SchemeSymbol(ASTNode node)
  {
    super(node, "SchemeSymbol");
  }
}
