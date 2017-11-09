package schemely.psi.impl.list;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.Tokens;
import schemely.psi.api.SchemeBraced;
import schemely.psi.impl.SchemePsiElementBase;
import schemely.psi.impl.symbols.SchemeIdentifier;
import schemely.psi.resolve.ResolveUtil;


public class SchemeList extends SchemeListBase implements SchemeBraced
{
  public static final String LAMBDA = "lambda";
  public static final String DEFINE = "define";
  public static final String DEFINE_SYNTAX = "define-syntax";
  public static final String DO = "do";
  public static final String LET = "let";
  public static final String LET_STAR = "let*";
  public static final String LETREC = "letrec";
  public static final String LET_SYNTAX = "let-syntax";
  public static final String LETREC_SYNTAX = "letrec-syntax";

  private static final String QUOTE = "quote";
  private static final String QUASIQUOTE = "quasiquote";
  private static final String UNQUOTE = "unquote";
  private static final String UNQUOTE_SPLICING = "unquote-splicing";

  public SchemeList(@NotNull ASTNode astNode)
  {
    super(astNode, "SchemeList");
  }

  @Override
  public String toString()
  {
    return getText();
  }

  @NotNull
  public PsiElement getFirstBrace()
  {
    PsiElement element = findChildByType(Tokens.LEFT_PAREN);
    assert element != null;
    return element;
  }

  public PsiElement getLastBrace()
  {
    return findChildByType(Tokens.RIGHT_PAREN);
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place)
  {
    if (isLambda())
    {
      return processLambdaDeclaration(processor, this, place);
    }
    else if (isDefinition())
    {
      return processDefineDeclaration(processor, this, place);
    }
    else if (isSyntaxDefinition())
    {
      return processDefineSyntaxDeclaration(processor, this, place);
    }
    else if (isLet())
    {
      return processLetDeclaration(processor, this, place);
    }
    else if (isDo())
    {
      return processDoDeclaration(processor, this, place);
    }
    return true;
  }

  @Override
  public int getQuotingLevel()
  {
    String headText = getHeadText();
    if (QUOTE.equals(headText) || QUASIQUOTE.equals(headText))
    {
      return 1;
    }
    else if (UNQUOTE.equals(headText) || UNQUOTE_SPLICING.equals(headText))
    {
      return -1;
    }
    return 0;
  }

  private static boolean processLambdaDeclaration(PsiScopeProcessor scopeProcessor, SchemeList lambda, PsiElement place)
  {
    if (!PsiTreeUtil.isAncestor(lambda, place, false))
    {
      return true;
    }

    PsiElement formals = lambda.getSecondNonLeafElement();
    if (formals == null)
    {
      return true;
    }

    // Lambda formals do not resolve
    if (PsiTreeUtil.isAncestor(formals, place, false))
    {
      return false;
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(scopeProcessor, (SchemePsiElementBase) formals, place))
    {
      return false;
    }

    if (formals instanceof SchemeIdentifier)
    {
      // (lambda x (head x))
      if (place == formals)
      {
        if (!ResolveUtil.processElement(scopeProcessor, (PsiNamedElement) place))
        {
          return false;
        }
      }

      if (!ResolveUtil.processElement(scopeProcessor, (PsiNamedElement) formals))
      {
        return false;
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (lambda (x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      for (SchemeIdentifier identifier : args.getAllIdentifiers())
      {
        if (!ResolveUtil.processElement(scopeProcessor, identifier))
        {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processDefineDeclaration(PsiScopeProcessor scopeProcessor, SchemeList define, PsiElement place)
  {
    PsiElement formals = define.getSecondNonLeafElement();
    if (formals == null)
    {
      return true;
    }

    // Define variables do not resolve
    if ((PsiTreeUtil.isAncestor(formals, place, false)))
    {
      return false;
    }

    if (place.getContainingFile().equals(define.getContainingFile()) &&
        (PsiTreeUtil.findCommonParent(place, define) != define.getParent()) &&
        !PsiTreeUtil.isAncestor(define, place, false))
    {
      return true;
    }

    if ((place != formals) && (formals instanceof SchemeIdentifier))
    {
      // (define x 3)
      SchemeIdentifier identifier = (SchemeIdentifier) formals;
      if (!ResolveUtil.processElement(scopeProcessor, identifier))
      {
        return false;
      }
    }
    else if (formals instanceof SchemeList)
    {
      // (define (plus3 x) (+ x 3))
      SchemeList args = (SchemeList) formals;

      if (PsiTreeUtil.isAncestor(define, place, false))
      {
        // Arguments are only visible in the define body
        for (SchemeIdentifier identifier : args.getAllIdentifiers())
        {
          if (!ResolveUtil.processElement(scopeProcessor, identifier))
          {
            return false;
          }
        }
      }
      else
      {
        // Function name is visible everywhere
        if (!ResolveUtil.processElement(scopeProcessor, args.getFirstIdentifier()))
        {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processDefineSyntaxDeclaration(PsiScopeProcessor scopeProcessor,
                                                        SchemeList define,
                                                        PsiElement place)
  {
    PsiElement formals = define.getSecondNonLeafElement();
    if (formals == null)
    {
      return true;
    }

    // Define variables do not resolve
    if ((PsiTreeUtil.isAncestor(formals, place, false)))
    {
      return false;
    }

    if ((place != formals) && (formals instanceof SchemeIdentifier))
    {
      // (define-syntax x <whatever>)
      SchemeIdentifier identifier = (SchemeIdentifier) formals;
      if (!ResolveUtil.processElement(scopeProcessor, identifier))
      {
        return false;
      }
    }

    return true;
  }

  private static boolean processLetDeclaration(PsiScopeProcessor scopeProcessor,
                                               SchemeList declaration,
                                               PsiElement place)
  {
    if (!PsiTreeUtil.isAncestor(declaration, place, false))
    {
      return true;
    }

    PsiElement vars = declaration.getSecondNonLeafElement();
    if (vars == null)
    {
      return true;
    }

    String style = declaration.getHeadText();
    assert style != null;

    SchemeIdentifier namedLetName = null;
    if (vars instanceof SchemeIdentifier && style.equals(LET))
    {
      namedLetName = (SchemeIdentifier) vars;
      // Named let name does not resolve
      if (place == namedLetName)
      {
        return false;
      }

      vars = ResolveUtil.getNextNonLeafElement(vars);

      if (!ResolveUtil.processElement(scopeProcessor, namedLetName))
      {
        return false;
      }
    }

    if ((PsiTreeUtil.isAncestor(vars, place, false)))
    {
      // place is either a let-bound variable or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == vars))
      {
        // If place is the first identifier in a list which is a sub-list of the let vars, it's a let-bound
        // variable so it does not resolve
        SchemeList parentList = (SchemeList) placeParent;
        SchemeIdentifier firstIdentifier = parentList.getFirstIdentifier();
        if (place == firstIdentifier)
        {
          return false;
        }
      }

      if (style.equals(LET) || style.equals(LET_SYNTAX))
      {
        // It's part of a value for a let-bound variable, nothing in the let is in scope
        return true;
      }
      else if (style.equals(LET_STAR))
      {
        if (vars instanceof SchemeList)
        {
          // (let* ((x 3) (y 4)) (+ x y))
          SchemeList args = (SchemeList) vars;

          for (SchemeList binding : args.getSubLists())
          {
            SchemeIdentifier var = binding.getFirstIdentifier();
            if (!ResolveUtil.processElement(scopeProcessor, var))
            {
              return false;
            }

            // Variables are only visible in later bindings
            if (PsiTreeUtil.isAncestor(binding, place, false))
            {
              return true;
            }
          }
        }
        // Should never get this far
        return true;
      }
      else if (style.equals(LETREC) || style.equals(LETREC_SYNTAX))
      {
        if (vars instanceof SchemeList)
        {
          // (letrec ((x 3) (y 4)) (+ x y))
          SchemeList args = (SchemeList) vars;
          SchemeList[] bindings = args.getSubLists();
          for (SchemeList binding : bindings)
          {
            SchemeIdentifier var = binding.getFirstIdentifier();
            if (!ResolveUtil.processElement(scopeProcessor, var))
            {
              return false;
            }
          }
        }
        return true;
      }
    }

    // Process internal definitions first to get shadowing
    if (!processInternalDefinitions(scopeProcessor, (SchemePsiElementBase) vars, place))
    {
      return false;
    }

    if (vars instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) vars;
      for (SchemeList binding : args.getSubLists())
      {
        SchemeIdentifier var = binding.getFirstIdentifier();
        if (!ResolveUtil.processElement(scopeProcessor, var))
        {
          return false;
        }
      }

      if (!ResolveUtil.processElement(scopeProcessor, namedLetName))
      {
        return false;
      }
    }

    return true;
  }

  private static boolean processDoDeclaration(PsiScopeProcessor scopeProcessor,
                                              SchemeList declaration,
                                              PsiElement place)
  {
    if (!PsiTreeUtil.isAncestor(declaration, place, false))
    {
      return true;
    }

    PsiElement vars = declaration.getSecondNonLeafElement();
    if (vars == null)
    {
      return true;
    }

    // Are we somewhere within the variable declarations?
    if ((PsiTreeUtil.isAncestor(vars, place, false)))
    {
      // place is either a let-bound variable, part of an init, or its value.
      PsiElement placeParent = place.getParent();
      if ((placeParent instanceof SchemeList) && (placeParent.getParent() == vars))
      {
        // If place is the first identifier in a list which is a sub-list of the do vars,
        // it's a do-bound variable so it does not resolve
        SchemeList parentList = (SchemeList) placeParent;

        if (place == parentList.getFirstIdentifier())
        {
          return false;
        }

        // Init expressions are not in scope
        PsiElement init = parentList.getSecondNonLeafElement();
        if (init != null && PsiTreeUtil.isAncestor(init, place, false))
        {
          return true;
        }
      }
    }

    // All vars are in scope everywhere else in the do
    if (vars instanceof SchemeList)
    {
      // (let ((x 3) (y 4)) (+ x y))
      SchemeList args = (SchemeList) vars;
      for (SchemeList binding : args.getSubLists())
      {
        SchemeIdentifier var = binding.getFirstIdentifier();
        if (!ResolveUtil.processElement(scopeProcessor, var))
        {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean processInternalDefinitions(PsiScopeProcessor scopeProcessor,
                                                    SchemePsiElementBase after,
                                                    PsiElement place)
  {
    // Process later ones first to get shadowing
    PsiElement next = ResolveUtil.getNextNonLeafElement(after);
    while ((next != null) && isDefinition(next) && !PsiTreeUtil.isAncestor(next, place, false))
    {
      next = ResolveUtil.getNextNonLeafElement(next);
    }

    if (next != null)
    {
      next = ResolveUtil.getPrevNonLeafElement(next);
    }

    while ((next != null) && (next != after) && isDefinition(next))
    {
      if (!processDefineDeclaration(scopeProcessor, (SchemeList) next, place))
      {
        return false;
      }
      next = ResolveUtil.getPrevNonLeafElement(next);
    }
    return true;
  }

  public static boolean isDefinition(PsiElement element)
  {
    // Handles null
    if (!(element instanceof SchemeList))
    {
      return false;
    }

    SchemeList list = (SchemeList) element;
    return list.isDefinition();
  }

  public boolean isDefinition()
  {
    String headText = getHeadText();
    return DEFINE.equals(headText);
  }

  public boolean isSyntaxDefinition()
  {
    String headText = getHeadText();
    return DEFINE_SYNTAX.equals(headText);
  }

  public boolean isLambda()
  {
    String headText = getHeadText();
    return LAMBDA.equals(headText);
  }

  private boolean isLet()
  {
    String headText = getHeadText();
    return LET.equals(headText) ||
           LET_STAR.equals(headText) ||
           LETREC.equals(headText) ||
           LET_SYNTAX.equals(headText) ||
           LETREC_SYNTAX.equals(headText);
  }

  public boolean isDo()
  {
    String headText = getHeadText();
    return DO.equals(headText);
  }

  public boolean isTopLevelDefinition()
  {
    return isDefinition() || isSyntaxDefinition();
  }

  public PsiElement getFunctionNavigationItem()
  {
    if (!isDefinition())
    {
      return null;
    }

    PsiElement formals = getSecondNonLeafElement();
    if (formals == null)
    {
      return null;
    }

    if (formals instanceof SchemeList)
    {
      return ((SchemeList)formals).getFirstNonLeafElement();
    }
    else if (formals instanceof SchemeIdentifier)
    {
      PsiElement body = ResolveUtil.getNextNonLeafElement(formals);
      if (body instanceof SchemeList && ((SchemeList) body).isLambda())
      {
        return formals;
      }
    }

    return null;
  }
}
