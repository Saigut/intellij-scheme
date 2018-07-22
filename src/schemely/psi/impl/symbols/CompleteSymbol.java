package schemely.psi.impl.symbols;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import schemely.psi.resolve.SchemeResolveResult;
import schemely.psi.resolve.completion.CompletionProcessor;
import schemely.scheme.REPL;

import java.util.HashMap;
import java.util.Map;


public class CompleteSymbol
{
  public static Object[] getVariants(PsiElement symbol)
  {
    PsiElement lastParent = null;
    PsiElement current = symbol;
    CompletionProcessor processor = new CompletionProcessor();
    while (current != null)
    {
      if (!current.processDeclarations(processor, ResolveState.initial(), lastParent, symbol))
      {
        break;
      }
      lastParent = current;
      current = current.getContext();
    }

    SchemeResolveResult[] candidates = processor.getCandidates();
    if (candidates.length == 0)
    {
      return PsiNamedElement.EMPTY_ARRAY;
    }

    Map<String, LookupElement> variants = new HashMap<String, LookupElement>();

    for (SchemeResolveResult candidate : candidates)
    {
      PsiNamedElement element = candidate.getElement();
      variants.put(element.getName(), mapToLookupElement(element));
    }

    PsiFile file = symbol.getContainingFile();
    REPL repl = file.getCopyableUserData(REPL.REPL_KEY);
    if (repl != null)
    {
      for (PsiNamedElement namedElement : repl.getSymbolVariants(symbol.getManager(), symbol))
      {
        variants.put(namedElement.getName(), mapToLookupElement(namedElement));
      }
    }

    return variants.values().toArray(new Object[variants.size()]);
  }

  private static LookupElement mapToLookupElement(PsiElement element)
  {
    if (element instanceof PsiNamedElement)
    {
      return LookupElementBuilder.create((PsiNamedElement) element);
    }
    return LookupElementBuilder.create(element, element.getText());
  }
}
