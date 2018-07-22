package schemely.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.Tokens;
import schemely.psi.impl.symbols.CompleteSymbol;
import schemely.psi.impl.symbols.SchemeIdentifier;

import static com.intellij.patterns.PlatformPatterns.or;
import static com.intellij.patterns.PlatformPatterns.psiElement;


public class SchemeCompletionContributor extends CompletionContributor
{
  private static final ElementPattern<PsiElement> IDENTIFIER =
    or(psiElement(SchemeIdentifier.class), psiElement(Tokens.IDENTIFIER));

  public SchemeCompletionContributor()
  {
    extend(CompletionType.BASIC, IDENTIFIER, new CompletionProvider<CompletionParameters>()
    {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters,
                                    ProcessingContext context,
                                    @NotNull CompletionResultSet result)
      {
        String prefix = parameters.getOriginalPosition().getText();
        result = result.withPrefixMatcher(new SchemeIdentifierMatcher(prefix));
        PsiElement position = parameters.getPosition();
        for (Object item : CompleteSymbol.getVariants(position))
        {
          if (item instanceof LookupElement)
          {
            result.addElement((LookupElement) item);
          }
        }
      }
    });
  }
}
