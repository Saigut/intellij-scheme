package main.fold;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import main.psi.impl.SchemeBodyOfForm;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SchemeFoldingBuilder implements FoldingBuilder
{
  public String getPlaceholderText(ASTNode node)
  {
    return "...";
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
    while (child != null)
    {
      appendDescriptors(child, descriptors);
      child = child.getTreeNext();
    }
  }

  private boolean isFoldableNode(ASTNode node)
  {
    PsiElement element = node.getPsi();
    return element instanceof SchemeBodyOfForm;
  }
}