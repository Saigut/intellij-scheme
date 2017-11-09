package schemely.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import schemely.lexer.Tokens;


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

  @Override
  public int getQuotingLevel()
  {
    ASTNode child = getNode().findChildByType(Tokens.PREFIXES);
    if (child != null)
    {
      IElementType type = child.getElementType();
      if ((type == Tokens.QUOTE_MARK) || (type == Tokens.BACKQUOTE))
      {
        return 1;
      }
      else if (type == Tokens.COMMA || type == Tokens.COMMA_AT)
      {
        return -1;
      }
    }

    // Should never happen
    return 0;
  }
}
