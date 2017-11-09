package schemely.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class ListBlock extends SchemeBlock
{
  private final Alignment childAlignment = Alignment.createAlignment();

  public ListBlock(@NotNull ASTNode node,
                   @Nullable Alignment alignment,
                   @NotNull Indent indent,
                   @Nullable Wrap wrap,
                   CodeStyleSettings settings)
  {
    super(node, alignment, indent, wrap, settings);
  }

  @Override
  protected List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings)
  {
    List<Block> subBlocks = new ArrayList<Block>();
    for (ASTNode childNode : getChildren(node))
    {
      Indent indent;
      Alignment align;
      if (Tokens.BRACES.contains(childNode.getElementType()))
      {
        indent = Indent.getNoneIndent();
        align = null;
      }
      else
      {
        indent = Indent.getNormalIndent(true);
        align = childAlignment;
      }
      subBlocks.add(SchemeBlock.create(childNode, align, indent, wrap, settings));
    }
    return subBlocks;
  }

  @Override
  protected ChildAttributes createChildAttributes(int newChildIndex)
  {
    return new ChildAttributes(Indent.getNormalIndent(true), childAlignment);
  }
}
