package main.psi.impl.list;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import main.psi.api.SchemeBraced;


public class SchemeList extends SchemeListBase implements SchemeBraced
{
  public SchemeList(@NotNull ASTNode astNode)
  {
    super(astNode, "SchemeList");
  }

  @Override
  public String toString()
  {
    return getText();
  }
}
