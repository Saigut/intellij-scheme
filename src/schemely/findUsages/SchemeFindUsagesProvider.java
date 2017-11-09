package schemely.findUsages;

import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.lexer.SchemeLexer;
import schemely.lexer.Tokens;
import schemely.psi.impl.symbols.SchemeIdentifier;


public class SchemeFindUsagesProvider implements FindUsagesProvider
{
  @Nullable
  public WordsScanner getWordsScanner()
  {
    return new DefaultWordsScanner(new SchemeLexer(), Tokens.IDENTIFIERS, Tokens.COMMENTS, Tokens.STRINGS);
  }

  public boolean canFindUsagesFor(@NotNull PsiElement psiElement)
  {
    return psiElement instanceof SchemeIdentifier;
  }

  public String getHelpId(@NotNull PsiElement psiElement)
  {
    return null;
  }

  @NotNull
  public String getType(@NotNull PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      return "symbol";
    }
    //    if (element instanceof ClDef)
    //    {
    //      return "definition";
    //    }
    return "entity";
  }

  @NotNull
  public String getDescriptiveName(@NotNull PsiElement element)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier symbol = (SchemeIdentifier) element;
      String name = symbol.getText();
      return name == null ? symbol.getText() : name;
    }

    return element.getText();
  }

  @NotNull
  public String getNodeText(@NotNull PsiElement element, boolean useFullName)
  {
    if (element instanceof SchemeIdentifier)
    {
      SchemeIdentifier symbol = (SchemeIdentifier) element;
      String name = symbol.getReferenceName();
      return name == null ? symbol.getText() : name;
    }

    return element.getText();
  }
}
