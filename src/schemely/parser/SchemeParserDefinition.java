package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.SchemeLexer;
import schemely.lexer.Tokens;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;


public class SchemeParserDefinition implements ParserDefinition
{
  private Scheme scheme = null;

  @NotNull
  public Lexer createLexer(Project project)
  {
    Scheme scheme = getScheme(project);
    return scheme.getLexer();
  }

  public PsiParser createParser(Project project)
  {
    Scheme scheme = getScheme(project);
    return scheme.getParser();
  }

  public IFileElementType getFileNodeType()
  {
    return AST.FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens()
  {
    return Tokens.WHITESPACE_SET;
  }

  @NotNull
  public TokenSet getCommentTokens()
  {
    return Tokens.COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements()
  {
    return Tokens.STRINGS;
  }

  @NotNull
  public PsiElement createElement(ASTNode node)
  {
    Scheme scheme = getScheme();
    SchemePsiCreator psiCreator = scheme.getPsiCreator();
    return psiCreator.createElement(node);
  }

  public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
  {
    if (Tokens.PREFIXES.contains(left.getElementType()))
    {
      return SpaceRequirements.MUST_NOT;
    }
    else if (left.getElementType() == Tokens.LEFT_PAREN ||
             right.getElementType() == Tokens.RIGHT_PAREN ||
             left.getElementType() == Tokens.RIGHT_PAREN ||
             right.getElementType() == Tokens.LEFT_PAREN

             ||
             left.getElementType() == Tokens.LEFT_CURLY ||
             right.getElementType() == Tokens.RIGHT_CURLY ||
             left.getElementType() == Tokens.RIGHT_CURLY ||
             right.getElementType() == Tokens.LEFT_CURLY

             ||
             left.getElementType() == Tokens.LEFT_SQUARE ||
             right.getElementType() == Tokens.RIGHT_SQUARE ||
             left.getElementType() == Tokens.RIGHT_SQUARE ||
             right.getElementType() == Tokens.LEFT_SQUARE)
    {
      return SpaceRequirements.MAY;
    }
    return SpaceRequirements.MUST;
  }

  public PsiFile createFile(FileViewProvider viewProvider)
  {
    Scheme scheme = getScheme(viewProvider.getManager().getProject());
    SchemePsiCreator psiCreator = scheme.getPsiCreator();
    return psiCreator.createFile(viewProvider);
  }

  private Scheme getScheme()
  {
    if (scheme == null)
    {
      throw new IllegalStateException("Scheme implementation not initialised");
    }
    return scheme;
  }

  private Scheme getScheme(Project project)
  {
    if (scheme == null)
    {
      initScheme(project);
    }
    return scheme;
  }

  private void initScheme(Project project)
  {
    scheme = SchemeImplementation.from(project);
  }
}
