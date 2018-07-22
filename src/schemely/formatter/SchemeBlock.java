package schemely.formatter;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.formatter.processors.SchemeSpacingProcessor;
import schemely.parser.AST;
import schemely.psi.impl.SchemeFile;
import schemely.psi.impl.SchemeVector;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SchemeBlock implements Block, AST
{
  final protected ASTNode node;
  final protected Alignment alignment;
  final protected Indent indent;
  final protected Wrap wrap;
  final protected CodeStyleSettings settings;

  protected List<Block> subBlocks = null;

  protected SchemeBlock(@NotNull ASTNode node,
                        @Nullable Alignment alignment,
                        @NotNull Indent indent,
                        @Nullable Wrap wrap,
                        CodeStyleSettings settings)
  {
    this.node = node;
    this.alignment = alignment;
    this.indent = indent;
    this.wrap = wrap;
    this.settings = settings;
  }

  public static SchemeBlock create(ASTNode childNode,
                                   Alignment align,
                                   Indent indent,
                                   Wrap wrap,
                                   CodeStyleSettings settings)
  {
    PsiElement blockPsi = childNode.getPsi();
    if (blockPsi instanceof SchemeList)
    {
      SchemeList list = (SchemeList) blockPsi;
      PsiElement first = list.getFirstNonLeafElement();
      if (first instanceof SchemeIdentifier)
      {
        return new ApplicationBlock(childNode, align, indent, wrap, settings);
      }
      else
      {
        return new ListBlock(childNode, align, indent, wrap, settings);
      }
    }
    else if (blockPsi instanceof SchemeVector)
    {
      return new ListBlock(childNode, align, indent, wrap, settings);
    }
    else
    {
      return new SchemeBlock(childNode, align, indent, wrap, settings);
    }
  }

  @NotNull
  public ASTNode getNode()
  {
    return node;
  }

  @NotNull
  public TextRange getTextRange()
  {
    return node.getTextRange();
  }

  @NotNull
  public List<Block> getSubBlocks()
  {
    if (subBlocks == null)
    {
      subBlocks = generateSubBlocks(node, wrap, settings);
    }
    return subBlocks;
  }

  protected List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings)
  {
    List<Block> subBlocks = new ArrayList<Block>();
    for (ASTNode childNode : getChildren(node))
    {
      subBlocks.add(create(childNode, null, Indent.getNoneIndent(), wrap, settings));
    }
    return subBlocks;
  }

  protected static Collection<ASTNode> getChildren(ASTNode node)
  {
    Collection<ASTNode> ret = new ArrayList<ASTNode>();
    for (ASTNode astNode : node.getChildren(null))
    {
      if (nonEmptyBlock(astNode))
      {
        ret.add(astNode);
      }
    }
    return ret;
  }

  protected static boolean nonEmptyBlock(ASTNode node)
  {
    String nodeText = node.getText().trim();
    return (nodeText.length() > 0);
  }

  @Nullable
  public Wrap getWrap()
  {
    return wrap;
  }

  @Nullable
  public Indent getIndent()
  {
    return indent;
  }

  @Nullable
  public Alignment getAlignment()
  {
    return alignment;
  }

  public Spacing getSpacing(Block child1, Block child2)
  {
    return SchemeSpacingProcessor.getSpacing(child1, child2);
  }

  @NotNull
  public ChildAttributes getChildAttributes(int newChildIndex)
  {
    ASTNode astNode = getNode();
    PsiElement psiParent = astNode.getPsi();
    if (psiParent instanceof SchemeFile)
    {
      return new ChildAttributes(Indent.getNoneIndent(), null);
    }
    return createChildAttributes(newChildIndex);
  }

  protected ChildAttributes createChildAttributes(int newChildIndex)
  {
    return new ChildAttributes(Indent.getNoneIndent(), null);
  }

  public boolean isIncomplete()
  {
    return isIncomplete(node);
  }

  /**
   * @param node Tree node
   * @return true if node is incomplete
   */
  public boolean isIncomplete(@NotNull ASTNode node)
  {
    ASTNode lastChild = node.getLastChildNode();
    while (lastChild != null &&
           (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment))
    {
      lastChild = lastChild.getTreePrev();
    }
    return lastChild != null && (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
  }

  public boolean isLeaf()
  {
    return node.getFirstChildNode() == null;
  }
}
