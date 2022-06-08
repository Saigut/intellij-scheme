package main.psi.impl;

import com.intellij.lang.ASTNode;


public class SchemeEleChar extends SchemePsiElementBase
{
  public SchemeEleChar(ASTNode node)
  {
    super(node, "SchemeSpecialLiteral");
  }
}
