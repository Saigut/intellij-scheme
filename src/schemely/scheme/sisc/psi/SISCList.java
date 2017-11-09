package schemely.scheme.sisc.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import schemely.psi.impl.list.SchemeList;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveUtil;

/**
 * @author Colin Fleming
 */
public class SISCList extends SchemeList
{
  public static final String MODULE = "module";

  public SISCList(@NotNull ASTNode astNode)
  {
    super(astNode);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place)
  {
    if (isModule())
    {
      return processModuleDeclaration(processor, this, place);
    }

    return super.processDeclarations(processor, state, lastParent, place);
  }

  private static boolean processModuleDeclaration(PsiScopeProcessor scopeProcessor, SchemeList module, PsiElement place)
  {
    PsiElement symbolsOrName = module.getSecondNonLeafElement();
    SchemeIdentifier name = null;
    SchemeList symbols = null;

    // (module name (symbols)) or (module (symbols))
    if (symbolsOrName instanceof SchemeIdentifier)
    {
      name = (SchemeIdentifier) symbolsOrName;
      symbolsOrName = ResolveUtil.getNextNonLeafElement(symbolsOrName);
    }
    if (symbolsOrName instanceof SchemeList)
    {
      symbols = (SchemeList) symbolsOrName;
    }

    // Module name & symbols resolve to the symbol itself
    if ((PsiTreeUtil.isAncestor(name, place, false) || PsiTreeUtil.isAncestor(symbols, place, false)))
    {
      if (!ResolveUtil.processElement(scopeProcessor, (PsiNamedElement) place))
      {
        return false;
      }
    }

    // Otherwise symbols are only visible outside the module
    if (!PsiTreeUtil.isAncestor(module, place, false))
    {
      if (name != null)
      {
        if (!ResolveUtil.processElement(scopeProcessor, name))
        {
          return false;
        }
      }
      if (symbols != null)
      {
        for (SchemeIdentifier identifier : symbols.getAllIdentifiers())
        {
          if (!ResolveUtil.processElement(scopeProcessor, identifier))
          {
            return false;
          }
        }
      }
    }

    // TODO internal defines
    
    return true;
  }

  public boolean isModule()
  {
    String headText = getHeadText();
    return MODULE.equals(headText);
  }

  @Override
  public boolean isTopLevelDefinition()
  {
    return isModule() || super.isTopLevelDefinition();
  }
}
