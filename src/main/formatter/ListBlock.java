package main.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import main.lexer.SchemeTokens;
import main.parser.AST;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class ListBlock extends SchemeBlock
{
  private final Alignment childAlignment = Alignment.createAlignment();
  private ASTNode parentNode;

  public ListBlock(@NotNull ASTNode node,
                   @Nullable Alignment alignment,
                   @NotNull Indent indent,
                   @Nullable Wrap wrap,
                   CodeStyleSettings settings,
                   @Nullable ASTNode parentNode)
  {
    super(node, alignment, indent, wrap, settings);
    this.parentNode = parentNode;
  }

  @Override
  protected List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings)
  {
    List<Block> subBlocks = new ArrayList<Block>();
    if (LEAF_ELEMENTS.contains(node.getElementType()) || SchemeTokens.BRACES.contains(node.getElementType())) {
      return subBlocks;
    } else {
//      boolean is_first_child = true;
      Alignment align;
      for (ASTNode childNode : getChildren(node))
      {
        Indent indent;
        if (SchemeTokens.OPEN_BRACES.contains(childNode.getElementType()))
        {
          indent = Indent.getNoneIndent();
          align = alignment;
        }
        else
        {
          if (parentNode != null && parentNode.getElementType() == AST.AST_ELE_VECTOR) {
            indent = Indent.getSpaceIndent(1, true);
          } else {
            indent = Indent.getNormalIndent(true);
          }
          align = Alignment.createAlignment();
//          if (node.getElementType() == AST.AST_TEMP_LIST) {
//            if (is_first_child) {
//              indent = Indent.getSpaceIndent(1, true);
//              align = Alignment.createAlignment();
//              is_first_child = false;
//            } else {
//              if (align == null) {
//                align = Alignment.createAlignment();
//              } else {
//                align = Alignment.createChildAlignment(align);
//              }
//            }
//          } else {
//            align = Alignment.createAlignment();
//          }
        }
        if (node.getElementType() == AST.AST_FILE) {
          indent = Indent.getNoneIndent();
        }
        subBlocks.add(SchemeBlock.create(childNode, align, indent, wrap, settings, node));
      }
    }
    return subBlocks;
  }

  @Override
  protected ChildAttributes createChildAttributes(int newChildIndex)
  {
    return new ChildAttributes(Indent.getNormalIndent(true), childAlignment);
  }
}
