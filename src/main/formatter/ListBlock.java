package main.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import main.lexer.SchemeTokens;
import main.psi.impl.SchemeInFormBody;
import main.psi.impl.list.SchemeList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    List<Block> subBlocks = new ArrayList<>();
    PsiElement nodePsi = node.getPsi();
    if (node.getFirstChildNode() == null) {
      return subBlocks;
    } else {
      Alignment align;
      for (ASTNode childNode : getChildren(node))
      {
        Indent indent;
        if (nodePsi instanceof PsiFile) {
          align = alignment;
          indent = Indent.getNoneIndent();
        } else if (childNode.getFirstChildNode() == null
                && !SchemeTokens.CLOSE_BRACES.contains(childNode.getElementType())
                && !(childNode.getPsi() instanceof PsiComment)) {
          align = alignment;
          indent = Indent.getNoneIndent();
        } else if (nodePsi instanceof SchemeInFormBody) {
          align = alignment;
          indent = Indent.getNoneIndent();
        } else if (nodePsi instanceof SchemeList) {
          align = Alignment.createAlignment();
          indent = Indent.getSpaceIndent(1, true);
        } else {
          align = Alignment.createAlignment();
          indent = Indent.getNormalIndent(true);
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
