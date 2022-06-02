package main.psi.impl;

import com.intellij.lang.ASTNode;
import main.psi.api.SchemeBraced;


public class SchemeVector extends SchemePsiElementBase implements SchemeBraced
{
  public SchemeVector(ASTNode node)
  {
    super(node, "SchemeVector");
  }
}
