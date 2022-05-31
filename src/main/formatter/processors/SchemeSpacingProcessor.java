package main.formatter.processors;

import com.intellij.formatting.Block;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import main.formatter.SchemeBlock;
import main.lexer.Tokens;
import main.parser.AST;


public class SchemeSpacingProcessor
{
  private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
  private static final Spacing NO_SPACING_WITH_NEWLINE = Spacing.createSpacing(0, 0, 0, true, 1);
  private static final Spacing COMMON_SPACING = Spacing.createSpacing(1, 1, 0, true, 100);

  public static Spacing getSpacing(Block child1, Block child2)
  {
    if (!(child1 instanceof SchemeBlock) || !(child2 instanceof SchemeBlock))
    {
      return null;
    }
    SchemeBlock block1 = (SchemeBlock) child1;
    SchemeBlock block2 = (SchemeBlock) child2;

    ASTNode node1 = block1.getNode();
    ASTNode node2 = block2.getNode();

    return getSpacingForAST(node1, node2);
  }

  public static Spacing getSpacingForAST(ASTNode node1, ASTNode node2)
  {
    IElementType type1 = node1.getElementType();
    IElementType type2 = node2.getElementType();

    if (Tokens.PREFIXES.contains(type1)
            || type1 == AST.AST_BAD_CHARACTER)
    {
      return NO_SPACING;
    }

    if (Tokens.BRACES.contains(type1) || Tokens.BRACES.contains(type2))
    {
      return NO_SPACING_WITH_NEWLINE;
    }

    return COMMON_SPACING;
  }
}
