package schemely.formatter;

import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.Tokens;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Colin Fleming
 */
public class ApplicationBlock extends ListBlock
{
  private static final Map<String, Integer> schemeFormIndent = new HashMap<String, Integer>();

  static
  {
    // From scheme.el && iuscheme.el
    schemeFormIndent.put("begin", 0);
    schemeFormIndent.put("define", 1);
    schemeFormIndent.put("define-syntax", 1);
    schemeFormIndent.put("define-class", 2);
    schemeFormIndent.put("define-simple-class", 2);
    schemeFormIndent.put("case", 1);
    schemeFormIndent.put("delay", 0);
    schemeFormIndent.put("do", 2);
    schemeFormIndent.put("lambda", 1);
    schemeFormIndent.put("let", 1);
    schemeFormIndent.put("let*", 1);
    schemeFormIndent.put("letrec", 1);
    schemeFormIndent.put("let-values", 1);
    schemeFormIndent.put("let*-values", 1);
    schemeFormIndent.put("sequence", 0);
    schemeFormIndent.put("let-syntax", 1);
    schemeFormIndent.put("letrec-syntax", 1);
    schemeFormIndent.put("syntax-rules", 1);
    schemeFormIndent.put("syntax-case", 2);
    schemeFormIndent.put("and", 0);
    schemeFormIndent.put("or", 0);
    schemeFormIndent.put("cond", 0);
    schemeFormIndent.put("set!", 1);
    schemeFormIndent.put("if", 3);
    schemeFormIndent.put("when", 1);
    schemeFormIndent.put("unless", 1);
    schemeFormIndent.put("parameterize", 1);
    schemeFormIndent.put("with-syntax", 1);

    schemeFormIndent.put("call-with-input-file", 1);
    schemeFormIndent.put("with-input-from-file", 1);
    schemeFormIndent.put("with-input-from-port", 1);
    schemeFormIndent.put("call-with-output-file", 1);
    schemeFormIndent.put("with-output-to-file", 1);
    schemeFormIndent.put("with-output-to-port", 1);
    schemeFormIndent.put("call-with-values", 1);
    schemeFormIndent.put("dynamic-wind", 3);
  }

  private final Alignment parameterAlignment = Alignment.createAlignment();
  private final Alignment parameterChildAlignment = Alignment.createChildAlignment(parameterAlignment);
  private final Alignment bodyAlignment = Alignment.createAlignment();
  private final Alignment bodyChildAlignment = Alignment.createChildAlignment(bodyAlignment);

  private final int parameters;

  public ApplicationBlock(@NotNull ASTNode node,
                          @Nullable Alignment alignment,
                          @NotNull Indent indent,
                          @Nullable Wrap wrap,
                          CodeStyleSettings settings)
  {
    super(node, alignment, indent, wrap, settings);

    int parameters = 0;

    SchemeList list = (SchemeList) node.getPsi();
    assert list != null;
    PsiElement first = list.getFirstNonLeafElement();
    String operator = first.getText();
    Integer integer = schemeFormIndent.get(operator);
    if (integer != null)
    {
      parameters = integer.intValue();
    }
    if (operator.equals("let"))
    {
      // Special case named let
      PsiElement element = list.getSecondNonLeafElement();
      if (element instanceof SchemeIdentifier)
      {
        parameters = 2;
      }
    }

    this.parameters = parameters;
  }

  @Override
  protected List<Block> generateSubBlocks(ASTNode node, Wrap wrap, CodeStyleSettings settings)
  {
    List<Block> subBlocks = new ArrayList<Block>();

    int childIndex = 0;
    for (ASTNode childNode : getChildren(node))
    {
      Alignment align = null;
      Indent indent;

      if (Tokens.OPEN_BRACES.contains(childNode.getElementType()))
      {
        indent = Indent.getNoneIndent();
      }
      else
      {
        boolean isComment = Tokens.COMMENTS.contains(childNode.getElementType());
        if (childIndex == 0)
        {
          indent = Indent.getNormalIndent(true);
        }
        else if ((childIndex - 1) < parameters)
        {
          align = isComment ? parameterChildAlignment : parameterAlignment;
          indent = Indent.getContinuationIndent(true);
        }
        else
        {
          align = isComment ? bodyChildAlignment : bodyAlignment;
          indent = Indent.getNormalIndent(true);
        }
        if (!isComment)
        {
          childIndex++;
        }
      }
      subBlocks.add(SchemeBlock.create(childNode, align, indent, wrap, settings));
    }
    return subBlocks;
  }

  @Override
  protected ChildAttributes createChildAttributes(int newChildIndex)
  {
    Alignment align = null;
    Indent indent;

    // Indexing starts at one here because of opening parenthesis
    if (newChildIndex == 1)
    {
      indent = Indent.getNormalIndent(true);
    }
    else if (newChildIndex < parameters)
    {
      align = parameterAlignment;
      indent = Indent.getContinuationIndent(true);
    }
    else
    {
      align = bodyAlignment;
      indent = Indent.getNormalIndent(true);
    }
    return new ChildAttributes(indent, align);
  }
}
