package main.fold;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import main.lexer.SchemeTokens;
import main.psi.impl.SchemeBodyOfForm;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SchemeFoldingBuilder implements FoldingBuilder
{
  public String getPlaceholderText(ASTNode node)
  {
    if (node.getElementType() == SchemeTokens.LINE_COMMENT) {
      return ";...";
    } else if (node.getElementType() == SchemeTokens.BLOCK_COMMENT) {
      return "#|...|#";
    } else if (node.getElementType() == SchemeTokens.DATUM_COMMENT) {
      return "#;...";
    } else {
      return "...";
    }
  }

  @NotNull
  public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode rootNode, @NotNull Document document)
  {
    List<FoldingDescriptor> descriptors = new ArrayList<>();
    appendDescriptors(rootNode, descriptors);
    return descriptors.toArray(new FoldingDescriptor[0]);
  }

  public boolean isCollapsedByDefault(ASTNode node)
  {
      return false;
  }

  private void appendDescriptors(ASTNode node, List<FoldingDescriptor> descriptors)
  {
    if (isFoldableNode(node)) {
      descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
    }
    ASTNode child = node.getFirstChildNode();
    while (child != null) {
      if (child.getElementType() == SchemeTokens.LINE_COMMENT) {
        ASTNode firstNode = child;
        ASTNode lastNode = child;
        while (child != null) {
          if (child.getElementType() == SchemeTokens.LINE_COMMENT) {
            lastNode = child;
          } else if (child.getElementType() == SchemeTokens.WHITESPACE) {
          } else {
            break;
          }
          child = child.getTreeNext();
        }
        descriptors.add(new FoldingDescriptor(firstNode,
                new TextRange(firstNode.getStartOffset(),
                        lastNode.getTextRange().getEndOffset())));
      } else if (child.getElementType() == SchemeTokens.WHITESPACE) {
        child = child.getTreeNext();
      } else {
        appendDescriptors(child, descriptors);
        child = child.getTreeNext();
      }
    }
  }

  private boolean isFoldableNode(ASTNode node)
  {
    PsiElement element = node.getPsi();
    return (element instanceof SchemeBodyOfForm)
            || SchemeTokens.COMMENTS.contains(node.getElementType());
  }
}