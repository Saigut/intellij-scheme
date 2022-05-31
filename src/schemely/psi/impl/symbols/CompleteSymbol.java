package schemely.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import schemely.parser.AST;
import schemely.psi.util.SchemePsiUtil;

import java.util.ArrayList;


public class CompleteSymbol
{
  public static Object[] getVariants(PsiElement symbol)
  {
    PsiElement bigBrother = symbol;
    PsiElement declaration;

    ArrayList<Object> ary_list= new ArrayList<>();
    while (true)
    {
      bigBrother = SchemePsiUtil.getBigBrother(bigBrother);
      if (null == bigBrother)
      {
        break;
      }
      if (AST.DEFINE_FORMS.contains(bigBrother.getNode().getElementType()))
      {
        declaration = SchemePsiUtil.getNormalChildAt(bigBrother, 1);
        if (null != declaration)
        {
          ary_list.add(LookupElementBuilder.create(declaration.getText()));
        }
      }
    }
    return ary_list.toArray();
  }
}
