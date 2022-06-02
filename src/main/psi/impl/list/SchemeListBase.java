package main.psi.impl.list;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import main.psi.impl.SchemePsiElementBase;


public abstract class SchemeListBase extends SchemePsiElementBase
{
  public SchemeListBase(@NotNull ASTNode astNode, String name)
  {
    super(astNode, name);
  }
}
