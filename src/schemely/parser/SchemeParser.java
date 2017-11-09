package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeBundle;
import schemely.lexer.Tokens;
import schemely.scheme.Scheme;
import schemely.scheme.SchemeImplementation;


public class SchemeParser implements PsiParser, Tokens
{
  private Scheme scheme;

  @NotNull
  public ASTNode parse(IElementType root, PsiBuilder builder)
  {
    scheme = SchemeImplementation.from(builder.getProject());

    builder.setDebugMode(true);
    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType())
    {
      parseSexp(token, builder);
    }
    marker.done(AST.FILE);
    return builder.getTreeBuilt();
  }


  // Helpers
  boolean isParen(IElementType type) {
    if (LEFT_PAREN == type || RIGHT_PAREN == type ||
        LEFT_SQUARE == type || RIGHT_SQUARE == type)
    {
      return true;
    }
    else
    {
      return false;
    }
  }

  IElementType atomMarkType(IElementType type)
  {
    if (LITERALS.contains(type)) {
      return AST.LITERAL;
    }
    else if (IDENTIFIER == type)
    {
      return AST.IDENTIFIER;
    }
    else if (KEYWORD == type)
    {
      return AST.KEYWORD;
    }
    else {
      return AST.LITERAL;
    }
  }


  // Main logic
  void parseAtom(IElementType type, PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(atomMarkType(type));
  }

  void parseSinglePrefix(IElementType type, PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    IElementType childType = builder.getTokenType();
    if (childType == null ||
        WHITESPACE == childType ||
        COMMENTS.contains(childType))
    {
      syntaxErrorJustBad(builder, SchemeBundle.message("expected.element"));
      marker.drop();
    }
    else
    {
      parseSexp(childType, builder);
      if (QUOTE_MARK == type)
      {
        marker.done(AST.QUOTED);
      }
      else if (BACKQUOTE == type)
      {
        marker.done(AST.BACKQUOTED);
      }
      else if (SHARP == type)
      {
        if (isParen(childType)) {
          marker.done(AST.VECTOR);
        }
        else
        {
          marker.done(AST.SPECIAL);
        }
      }
      else
      {
        syntaxError(builder, SchemeBundle.message("run.error.message.title"));
        marker.drop();
      }
    }
  }

  void parseParen(IElementType type, PsiBuilder builder)
  {
    if (LEFT_PAREN == type)
    {
      parseList(builder, LEFT_PAREN, RIGHT_PAREN);
    }
    else if (LEFT_SQUARE == type)
    {
      parseList(builder, LEFT_SQUARE, RIGHT_SQUARE);
    }
    else if (RIGHT_PAREN == type)
    {
      syntaxError(builder, SchemeBundle.message("expected.lparen"));
    }
    else if (RIGHT_SQUARE == type)
    {
      syntaxError(builder, SchemeBundle.message("expected.lsquare"));
    }
    else
    {
      syntaxError(builder, SchemeBundle.message("run.error.message.title"));
    }
  }

  void parseNonParen(IElementType type, PsiBuilder builder)
  {
    // it is just leaf, mark it's type
    if (QUOTE_MARK == type) {
      parseSinglePrefix(type, builder);
    }
    else if (BACKQUOTE == type)
    {
      parseSinglePrefix(type, builder);
    }
    else if (SHARP == type)
    {
      parseSinglePrefix(type, builder);
    }
    else
    {
      parseAtom(type, builder);
    }
  }

  void parseSexp(IElementType type, PsiBuilder builder)
  {
    if (isParen(type)) {
      parseParen(type, builder);
    } else {
      parseNonParen(type, builder);
    }
  }



  protected void parseDatum(PsiBuilder builder)
  {
    IElementType token = builder.getTokenType();
    if (LEFT_PAREN == token)
    {
      parseList(builder, LEFT_PAREN, RIGHT_PAREN);
    }
    else if (LEFT_SQUARE == token && scheme.supportsSquareBracesForLists())
    {
      parseList(builder, LEFT_SQUARE, RIGHT_SQUARE);
    }
    else if (OPEN_VECTOR == token)
    {
      parseVector(builder);
    }
    else if (LITERALS.contains(token))
    {
      parseLiteral(builder);
    }
    else if (KEYWORD == token)
    {
      parseKeyword(builder);
    }
    else if (IDENTIFIER == token)
    {
      parseIdentifier(builder);
    }
    else if (SPECIAL == token)
    {
      parseSpecial(builder);
    }
    else if (getPrefixes().contains(token))
    {
      parseAbbreviation(builder);
    }
    else
    {
      syntaxError(builder, SchemeBundle.message("expected.left.paren.symbol.or.literal"));
    }
  }

  private void parseExpressions(IElementType endToken, PsiBuilder builder)
  {
    for (IElementType token = builder.getTokenType();
         token != endToken && token != null;
         token = builder.getTokenType())
    {
      parseDatum(builder);
    }
    if (builder.getTokenType() != endToken)
    {
      builder.error(SchemeBundle.message("expected.token", endToken.toString()));
    }
    else
    {
      builder.advanceLexer();
    }
  }

  private void syntaxError(PsiBuilder builder, String msg)
  {
    String e = msg + ": " + builder.getTokenText();
    builder.error(e);
    advanceLexerOrEOF(builder);
  }

  private void syntaxErrorJustBad(PsiBuilder builder, String msg)
  {
    String e = msg + ": " + builder.getTokenText();
    builder.error(e);
  }

  private void advanceLexerOrEOF(PsiBuilder builder)
  {
    if (builder.getTokenType() != null)
    {
      builder.advanceLexer();
    }
  }

  private PsiBuilder.Marker markAndAdvance(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    return marker;
  }

  private void internalError(String msg)
  {
    throw new Error(msg);
  }

  /**
   * Enter: Lexer is pointed at literal
   * Exit: Lexer is pointed immediately after literal
   *
   * @param builder
   */
  private void parseLiteral(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.LITERAL);
  }

  /**
   * Enter: Lexer is pointed at identifier
   * Exit: Lexer is pointed immediately after identifier
   *
   * @param builder
   */
  private void parseIdentifier(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    //    ParserUtils.getToken(builder, IDENTIFIER, "Expected identifier");
    // Currently using this for keywords too
    // TODO fix this
    builder.advanceLexer();
    marker.done(AST.IDENTIFIER);
  }

  private void parseKeyword(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.KEYWORD);
  }

  /**
   * Enter: Lexer is pointed at special
   * Exit: Lexer is pointed immediately after special symbol
   *
   * @param builder
   */
  private void parseSpecial(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.SPECIAL);
  }

  /**
   * Enter: Lexer is pointed at abbreviation mark
   * Exit: Lexer is pointed immediately after datum quoted by abbreviation mark
   */
  private void parseAbbreviation(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    parseDatum(builder);
    marker.done(AST.QUOTED);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseList(PsiBuilder builder, IElementType open, IElementType close)
  {
    if (builder.getTokenType() != open)
    {
      if (LEFT_PAREN == open) {
        internalError(SchemeBundle.message("expected.lparen"));
      }
      else if (LEFT_SQUARE == open)
      {
      internalError(SchemeBundle.message("expected.lsquare"));
      }
      else
      {
        internalError(SchemeBundle.message("run.error.message.title"));
      }
    }

    PsiBuilder.Marker marker = markAndAdvance(builder);

    IElementType token = builder.getTokenType();
    while (token != close && token != null)
    {
      parseSexp(token, builder);
      token = builder.getTokenType();
    }

    if (builder.getTokenType() != close)
    {
      builder.error(SchemeBundle.message("expected.token", close.toString()));
    }
    else
    {
      builder.advanceLexer();
    }
    marker.done(AST.LIST);
  }

  /**
   * Enter: Lexer is pointed at the opening left square
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseVector(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = markAndAdvance(builder);
    parseExpressions(RIGHT_PAREN, builder);
    marker.done(AST.VECTOR);
  }

  protected TokenSet getPrefixes()
  {
    return PREFIXES;
  }
}
