package main.parser;

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
import main.lexer.SchemeTokens;
import main.scheme.impl.DefaultScheme;


public class SchemeParserDefinition implements ParserDefinition
{
  private DefaultScheme scheme = new DefaultScheme();

  @NotNull
  public Lexer createLexer(Project project)
  {
    return scheme.getLexer();
  }

  public PsiParser createParser(Project project)
  {
    return scheme.getParser();
  }

  public IFileElementType getFileNodeType()
  {
    return AST.AST_FILE;
  }

  @NotNull
  public TokenSet getWhitespaceTokens()
  {
    return SchemeTokens.WHITESPACE_SET;
  }

  @NotNull
  public TokenSet getCommentTokens()
  {
    return SchemeTokens.COMMENTS;
  }

  @NotNull
  public TokenSet getStringLiteralElements()
  {
    return SchemeTokens.STRINGS;
  }

  @NotNull
  public PsiElement createElement(ASTNode node)
  {
    SchemePsiCreator psiCreator = scheme.getPsiCreator();
    return psiCreator.createElement(node);
  }

  @NotNull
  public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right)
  {
    if (SchemeTokens.DATUM_PREFIXES.contains(left.getElementType()))
    {
      return SpaceRequirements.MUST_NOT;
    }
    else if (left.getElementType() == SchemeTokens.LEFT_PAREN ||
             right.getElementType() == SchemeTokens.RIGHT_PAREN ||
             left.getElementType() == SchemeTokens.RIGHT_PAREN ||
             right.getElementType() == SchemeTokens.LEFT_PAREN

             ||
             left.getElementType() == SchemeTokens.LEFT_CURLY ||
             right.getElementType() == SchemeTokens.RIGHT_CURLY ||
             left.getElementType() == SchemeTokens.RIGHT_CURLY ||
             right.getElementType() == SchemeTokens.LEFT_CURLY

             ||
             left.getElementType() == SchemeTokens.LEFT_SQUARE ||
             right.getElementType() == SchemeTokens.RIGHT_SQUARE ||
             left.getElementType() == SchemeTokens.RIGHT_SQUARE ||
             right.getElementType() == SchemeTokens.LEFT_SQUARE)
    {
      return SpaceRequirements.MAY;
    }
    return SpaceRequirements.MUST;
  }

  public PsiFile createFile(FileViewProvider viewProvider)
  {
    SchemePsiCreator psiCreator = scheme.getPsiCreator();
    return psiCreator.createFile(viewProvider);
  }
}
