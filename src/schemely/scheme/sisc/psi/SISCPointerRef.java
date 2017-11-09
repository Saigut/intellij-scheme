package schemely.scheme.sisc.psi;

import com.intellij.lang.ASTNode;
import schemely.psi.impl.SchemePsiElementBase;


public class SISCPointerRef extends SchemePsiElementBase
{
  public SISCPointerRef(ASTNode node)
  {
    super(node, "SISCPointerRef");
  }
}
