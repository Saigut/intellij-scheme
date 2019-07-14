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

//  private void printAstTree(@NotNull ASTNode astNode)
//  {
//    ASTNode tmpNode;
//    tmpNode = astNode.getFirstChildNode();
//    if (null != tmpNode)
//    {
//      System.out.println("> Child tree");
//      System.out.println("ele type: " + tmpNode.getElementType().toString()
//              + ", ele text: " + tmpNode.getText());
//      printAstTree(tmpNode);
//    }
//
//    tmpNode = astNode.getTreeNext();
//    if (null != tmpNode)
//    {
//      System.out.println("Next tree");
//      System.out.println("ele type: " + tmpNode.getElementType().toString()
//              + ", ele text: " + tmpNode.getText());
//      printAstTree(tmpNode);
//    }
//  }

  @NotNull
  public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder)
  {
    ASTNode theAst;
    scheme = SchemeImplementation.from(builder.getProject());
    builder.setDebugMode(true);

    theAst = do_parse(builder);

//    printAstTree(theAst);

    return theAst;
  }

  ASTNode do_parse(PsiBuilder builder)
  {
    ASTNode theAst;

    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType())
    {
//      System.out.println("token type: " + token.toString() + ", token text: "
//              + builder.getTokenText() + ", index: " + builder.getCurrentOffset());
      parseTopSexp(token, builder);
    }
    marker.done(AST.AST_FILE);

    theAst = builder.getTreeBuilt();

    //    printAstTree(theAst);

    return theAst;

  }

  ASTNode parse_pass1(PsiBuilder builder)
  {
    ASTNode theAst;

    builder.setDebugMode(true);

    PsiBuilder.Marker marker = builder.mark();
    for (IElementType token = builder.getTokenType(); token != null; token = builder.getTokenType())
    {
      parseSexp(token, builder);
    }
    marker.done(AST.AST_FILE);

    theAst = builder.getTreeBuilt();

//    printAstTree(theAst);

    return theAst;
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
    if (BOOLEAN_LITERAL == type) {
      return AST.AST_BASIC_ELE_BOOL;
    }
    else if (NUMBER_LITERAL == type) {
      return AST.AST_BASIC_ELE_NUM;
    }
    else if (CHAR_LITERAL == type) {
      return AST.AST_BASIC_ELE_CHAR;
    }
    else if (STRING_LITERAL == type) {
      return AST.AST_BASIC_ELE_STR;
    }
    else if (KEYWORD == type) {
      return AST.AST_BASIC_ELE_KEYWORD;
    }
    else if (PLAIN_LITERAL == type
             || IDENTIFIER == type) {
      return AST.AST_BASIC_ELE_SYMBOL;
    }
    else
    {
      return AST.AST_BAD_CHARACTER;
    }
  }


  // New Main logic
  IElementType eatRemainList(PsiBuilder builder, IElementType close, IElementType success_type)
  {
    IElementType mark_type;
    IElementType token_type = builder.getTokenType();
    while (token_type != close && token_type != null)
    {
      parseSexp(token_type, builder);
      token_type = builder.getTokenType();
    }

    if (builder.getTokenType() != close)
    {
      builder.error(SchemeBundle.message("expected.token", close.toString()));
      mark_type = AST.AST_BAD_ELEMENT;
    }
    else
    {
      builder.advanceLexer();
      mark_type = success_type;
    }

    return mark_type;
  }

  IElementType parseFormLet(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_LET);
  }

  IElementType parseFormSet(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_SET);
  }

  IElementType parseFormLambda(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_PROCEDURE);
  }

  IElementType parseFormCons(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_CONS);
  }

  IElementType parseFormDefine(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_DEFINE);
  }

  IElementType parseFormDefineRecordType(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_DEFINE_RECORD_TYPE);
  }

  IElementType parseFormDefineSyntax(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_DEFINE_SYNTAX);
  }

  IElementType parseFormDo(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_DO);
  }

  IElementType parseFormList(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_LIST);
  }

  IElementType parseFormLibrary(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_LIBRARY);
  }

  IElementType parseFormCar(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_CAR);
  }

  IElementType parseFormCdr(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_CDR);
  }

  IElementType parseFormBegin(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_BEGIN);
  }

  IElementType parseFormIf(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_IF);
  }

  IElementType parseFormCond(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_COND);
  }

  IElementType parseFormWhen(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_WHEN);
  }

  IElementType parseFormUnless(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_UNLESS);
  }

  IElementType parseFormAnd(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_AND);
  }

  IElementType parseFormOr(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_OR);
  }

  IElementType parseFormNot(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_NOT);
  }

  IElementType parseTopOnlyForm(PsiBuilder builder, IElementType close, String text)
  {
    IElementType mark_type = null;
    if (text.equals("library"))
    {
      mark_type = parseFormLibrary(builder, close);
    }
    return mark_type;
  }

  IElementType parseTopAndLocalForm(PsiBuilder builder, IElementType close, String text)
  {
    IElementType mark_type = null;
    if (text.equals("and"))
    {
      mark_type = parseFormAnd(builder, close);
    }
    else if (text.equals("begin"))
    {
      mark_type = parseFormBegin(builder, close);
    }
    else if (text.equals("car"))
    {
      mark_type = parseFormCar(builder, close);
    }
    else if (text.equals("cdr"))
    {
      mark_type = parseFormCdr(builder, close);
    }
    else if (text.equals("cond"))
    {
      mark_type = parseFormCond(builder, close);
    }
    else if (text.equals("cons"))
    {
      mark_type = parseFormCons(builder, close);
    }
    else if (text.equals("define"))
    {
      mark_type = parseFormDefine(builder, close);
    }
    else if (text.equals("define-record-type"))
    {
      mark_type = parseFormDefineRecordType(builder, close);
    }
    else if (text.equals("define-syntax"))
    {
      mark_type = parseFormDefineSyntax(builder, close);
    }
    else if (text.equals("do"))
    {
      mark_type = parseFormDo(builder, close);
    }
    else if (text.equals("if"))
    {
      mark_type = parseFormIf(builder, close);
    }
    else if (text.equals("lambda"))
    {
      mark_type = parseFormLambda(builder, close);
    }
    else if (text.equals("let"))
    {
      mark_type = parseFormLet(builder, close);
    }
    else if (text.equals("list"))
    {
      mark_type = parseFormList(builder, close);
    }
    else if (text.equals("not"))
    {
      mark_type = parseFormNot(builder, close);
    }
    else if (text.equals("or"))
    {
      mark_type = parseFormOr(builder, close);
    }
    else if (text.equals("set!"))
    {
      mark_type = parseFormSet(builder, close);
    }
    else if (text.equals("unless"))
    {
      mark_type = parseFormUnless(builder, close);
    }
    else if (text.equals("when"))
    {
      mark_type = parseFormWhen(builder, close);
    }
    return mark_type;
  }

  private IElementType parseTopList(PsiBuilder builder, IElementType open, IElementType close)
  {
    IElementType mark_type = AST.AST_TEMP_LIST;
    PsiBuilder.Marker marker = markAndAdvance(builder);

    IElementType token_type = builder.getTokenType();
    String token_text = builder.getTokenText();
    if (token_text == null) {
//      internalError("token is null, something is wrong");
      builder.error("token is null, something is wrong");
      marker.drop();
      return null;
    }
    IElementType exp_type = null;
    if (token_type != close && token_type != null)
    {
      exp_type = parseSexp(token_type, builder);
    }

    if (exp_type == null)
    {
//      internalError("parse sexp failed");
      builder.error("parse sexp failed");
      marker.drop();
      return null;
    }
    else if (exp_type != AST.AST_BASIC_ELE_KEYWORD)
    {
      mark_type = eatRemainList(builder, close, AST.AST_UNRECOGNIZED_FORM);
    }
    else
    {
      mark_type = parseTopOnlyForm(builder, close, token_text);
      if (mark_type == null)
      {
        mark_type = parseTopAndLocalForm(builder, close, token_text);
        if (mark_type == null)
        {
          mark_type = eatRemainList(builder, close, AST.AST_FORM_CALL_PROCEDURE);
        }
      }
    }

    marker.done(mark_type);

    return mark_type;
  }

  void parseTopParen(IElementType type, PsiBuilder builder)
  {
    if (LEFT_PAREN == type)
    {
      parseTopList(builder, LEFT_PAREN, RIGHT_PAREN);
    }
    else if (LEFT_SQUARE == type)
    {
      parseTopList(builder, LEFT_SQUARE, RIGHT_SQUARE);
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

  void parseTopSexp(IElementType type, PsiBuilder builder)
  {
    if (!isParen(type)) {
      parseNonParen(type, builder);
    } else {
      parseTopParen(type, builder);
    }
  }

  // Main logic
  IElementType parseAtom(IElementType type, PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    IElementType mark_type = atomMarkType(type);
    marker.done(mark_type);
    return mark_type;
  }

  IElementType parseSinglePrefix(IElementType type, PsiBuilder builder)
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
      return null;
    }
    else
    {
      IElementType mark_type = null;
      if (DATUM_COMMENT_PRE == type) {
        PsiBuilder.Marker marker_datum = builder.mark();
        parseSexp(childType, builder);
        marker_datum.collapse(Tokens.COMMENTED_DATUM);

        mark_type = AST.AST_ELE_DATUM_COMMENT;
        marker.done(mark_type);
        return mark_type;
      }
      parseSexp(childType, builder);
      if (QUOTE_MARK == type)
      {
        mark_type = AST.AST_FORM_QUOTE;
      }
      else if (BACKQUOTE == type)
      {
        mark_type = AST.AST_FORM_BACKQUOTE;
      }
      else if (SHARP == type)
      {
        if (isParen(childType)) {
          mark_type = AST.AST_ELE_VECTOR;
        }
        else
        {
          mark_type = AST.AST_BAD_CHARACTER;
        }
      }
      else
      {
        syntaxError(builder, SchemeBundle.message("run.error.message.title"));
        marker.drop();
      }
      if (mark_type != null) {
        marker.done(mark_type);
      }
      return mark_type;
    }
  }

  IElementType parseNonParen(IElementType type, PsiBuilder builder)
  {
    // it is just leaf, mark it's type
    if (QUOTE_MARK == type
        || BACKQUOTE == type
        || SHARP == type
        || DATUM_COMMENT_PRE == type) {
      return parseSinglePrefix(type, builder);
    }
    else
    {
      return parseAtom(type, builder);
    }
  }

  IElementType parseParen(IElementType type, PsiBuilder builder)
  {
    if (LEFT_PAREN == type)
    {
      return parseList(builder, LEFT_PAREN, RIGHT_PAREN);
    }
    else if (LEFT_SQUARE == type)
    {
      return parseList(builder, LEFT_SQUARE, RIGHT_SQUARE);
    }
    else if (RIGHT_PAREN == type)
    {
      syntaxError(builder, SchemeBundle.message("expected.lparen"));
      return null;
    }
    else if (RIGHT_SQUARE == type)
    {
      syntaxError(builder, SchemeBundle.message("expected.lsquare"));
      return null;
    }
    else
    {
      syntaxError(builder, SchemeBundle.message("run.error.message.title"));
      return null;
    }
  }

  IElementType parseSexp(IElementType type, PsiBuilder builder)
  {
    if (!isParen(type)) {
      return parseNonParen(type, builder);
    } else {
      return parseParen(type, builder);
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
    marker.done(AST.AST_BASIC_ELE_SYMBOL);
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
    //    ParserUtils.getToken(builder, AST_IDENTIFIER, "Expected identifier");
    // Currently using this for keywords too
    // TODO fix this
    builder.advanceLexer();
    marker.done(AST.AST_BASIC_ELE_SYMBOL);
  }

  private void parseKeyword(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(AST.AST_BASIC_ELE_SYMBOL);
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
    marker.done(AST.AST_BASIC_ELE_SYMBOL);
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
    marker.done(AST.AST_FORM_QUOTE);
  }

  /**
   * Enter: Lexer is pointed at the opening left paren
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private IElementType parseList(PsiBuilder builder, IElementType open, IElementType close)
  {
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

    IElementType mark_type = AST.AST_TEMP_LIST;
    marker.done(mark_type);

    return mark_type;
  }

  /**
   * Enter: Lexer is pointed at the opening left square
   * Exit: Lexer is pointed immediately after the closing right paren, or at the end-of-file
   */
  private void parseVector(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = markAndAdvance(builder);
    parseExpressions(RIGHT_PAREN, builder);
    marker.done(AST.AST_ELE_VECTOR);
  }

  protected TokenSet getPrefixes()
  {
    return PREFIXES;
  }
}
