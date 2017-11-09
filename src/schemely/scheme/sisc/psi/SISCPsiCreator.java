package schemely.scheme.sisc.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import schemely.parser.AST;
import schemely.parser.SchemePsiCreator;
import schemely.scheme.sisc.parser.SISCAST;

/**
 * @author Colin Fleming
 */
public class SISCPsiCreator implements SchemePsiCreator
{
  private final SchemePsiCreator original;

  public SISCPsiCreator(SchemePsiCreator original)
  {
    this.original = original;
  }

  @Override
  public PsiElement createElement(ASTNode node)
  {
    IElementType elementType = node.getElementType();

    if (elementType == AST.LIST)
    {
      return new SISCList(node);
    }
    else if (elementType == SISCAST.PTR_DEF)
    {
      return new SISCPointerDef(node);
    }
    else if (elementType == SISCAST.PTR_REF)
    {
      return new SISCPointerRef(node);
    }
    return original.createElement(node);
  }

  @Override
  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SISCFile(viewProvider);
  }
}
