package main.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import main.lexer.SchemeTokens;


public class SchemeParser implements PsiParser, SchemeTokens
{
  @NotNull
  public ASTNode parse(@NotNull IElementType root, @NotNull PsiBuilder builder)
  {
    ASTNode theAst;
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
    else if (PROCEDURE == type) {
      return AST.AST_BASIC_ELE_PROCEDURE;
    }
    else if (NAME_LITERAL == type
             || IDENTIFIER == type) {
      return AST.AST_BASIC_ELE_SYMBOL;
    }
    else
    {
      return AST.AST_BAD_CHARACTER;
    }
  }

  IElementType datumPrefixMarkType(IElementType type)
  {
    if (QUOTE == type)
    {
      return AST.AST_FORM_QUOTE;
    }
    else if (QUASIQUOTE == type)
    {
      return AST.AST_FORM_QUASIQUOTE;
    }
    else if (UNQUOTE == type)
    {
      return AST.AST_FORM_UNQUOTE;
    }
    else if (UNQUOTE_SPLICING == type)
    {
      return AST.AST_FORM_UNQUOTE_SPLICING;
    }
    else if (SYNTAX == type)
    {
      return AST.AST_FORM_SYNTAX;
    }
    else if (QUASISYNTAX == type)
    {
      return AST.AST_FORM_QUASISYNTAX;
    }
    else if (UNSYNTAX == type)
    {
      return AST.AST_FORM_UNSYNTAX;
    }
    else if (UNSYNTAX_SPLICING == type)
    {
      return AST.AST_FORM_UNSYNTAX_SPLICING;
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
      builder.error("Expected '" + close.toString() + "'");
      mark_type = AST.AST_BAD_ELEMENT;
    }
    else
    {
      builder.advanceLexer();
      mark_type = success_type;
    }

    return mark_type;
  }

  // ret: true, have body; false, have no body.
  boolean helperListAdvance(PsiBuilder builder, IElementType close, int i)
  {
    int j = 0;
    IElementType token_type = builder.getTokenType();
    while (token_type == SchemeTokens.DATUM_COMMENT_PRE) {
      parseSexp(token_type, builder);
      token_type = builder.getTokenType();
    }
    while (j < i && token_type != close && token_type != null)
    {
      parseSexp(token_type, builder);
      token_type = builder.getTokenType();
      if (token_type != SchemeTokens.DATUM_COMMENT_PRE) {
        j++;
      } else {
        parseSexp(token_type, builder);
      }
    }
    return (token_type != null) && (token_type != close);
  }

  void helperEatFormBody(PsiBuilder builder, IElementType close,
                         PsiBuilder.Marker bodyMarker)
  {
    IElementType token_type = builder.getTokenType();
    while (token_type != close && token_type != null)
    {
      parseSexp(token_type, builder);
      token_type = builder.getTokenType();
    }
    bodyMarker.done(AST.AST_BODY_IN_FORM_BODY);
  }

  IElementType parseFormLetForms(PsiBuilder builder, IElementType close, IElementType let_type)
  {
    if (builder.getTokenType() == NAME_LITERAL) {
      markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
    }
    helperListAdvance(builder, close, 0);
    IElementType token_type = builder.getTokenType();
    if (OPEN_SEXP_BRACES.contains(token_type)) {
      PsiBuilder.Marker listMarker = builder.mark();
      IElementType list_close = getCloseParen(token_type);
      IElementType sub_list_token_type;
      builder.advanceLexer();
      helperListAdvance(builder, list_close, 0);
      sub_list_token_type = builder.getTokenType();
      while (sub_list_token_type != null && sub_list_token_type != list_close) {
        if (OPEN_SEXP_BRACES.contains(sub_list_token_type)) {
          PsiBuilder.Marker subListMarker = builder.mark();
          IElementType sub_list_close = getCloseParen(sub_list_token_type);
          builder.advanceLexer();
          helperListAdvance(builder, sub_list_close, 0);
          IElementType name_token_type = builder.getTokenType();
          if (name_token_type == NAME_LITERAL) {
            markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
            if (let_type == AST.AST_FORM_LET) {
              subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_BODY_IN_FORM_PARAM_LIST_LET_INNER));
            } else {
              subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_TEMP_LIST));
            }
          } else if (name_token_type == sub_list_close) {
            subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_BAD_ELEMENT));
          } else {
            markASexp(builder, AST.AST_BAD_ELEMENT);
            subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_BAD_ELEMENT));
          }
        } else {
          if (sub_list_token_type == NAME_LITERAL) {
            markAToken(builder, AST.AST_BAD_ELEMENT);
          } else {
            markASexp(builder, AST.AST_BAD_ELEMENT);
          }
        }
        helperListAdvance(builder, list_close, 0);
        sub_list_token_type = builder.getTokenType();
      }
      if (sub_list_token_type != list_close) {
        listMarker.done(eatRemainList(builder, list_close, AST.AST_BAD_ELEMENT));
      } else {
        listMarker.done(eatRemainList(builder, list_close, AST.AST_BODY_IN_FORM_PARAM_LIST));
      }
    } else {
      markAToken(builder, AST.AST_BAD_ELEMENT);
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, let_type);
  }

  IElementType parseFormSet(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_SET);
  }

  IElementType parseFormLambda(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_PROCEDURE;
    IElementType token_type = builder.getTokenType();
    if (OPEN_SEXP_BRACES.contains(token_type)) {
      PsiBuilder.Marker listMarker = builder.mark();
      IElementType next_token_type;
      IElementType list_close = getCloseParen(token_type);
      builder.advanceLexer();
      helperListAdvance(builder, list_close, 0);
      next_token_type = builder.getTokenType();
      while (next_token_type != null && next_token_type != list_close) {
        if (next_token_type == NAME_LITERAL) {
          markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
        } else {
          markASexp(builder, AST.AST_BAD_ELEMENT);
        }
        helperListAdvance(builder, list_close, 0);
        next_token_type = builder.getTokenType();
      }
      listMarker.done(eatRemainList(builder, list_close, AST.AST_BODY_IN_FORM_PARAM_LIST));
    } else {
      markASexp(builder, AST.AST_BAD_ELEMENT);
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormCons(PsiBuilder builder, IElementType close)
  {
    return eatRemainList(builder, close, AST.AST_FORM_CONS);
  }

  private IElementType getCloseParen(IElementType open) {
    if (LEFT_PAREN == open)
    {
      return  RIGHT_PAREN;
    }
    else
    {
      return RIGHT_SQUARE;
    }
  }

  IElementType parseFormDefine(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_DEFINE;
    IElementType token_type = builder.getTokenType();
    if (token_type == NAME_LITERAL) {
      markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
    } else if (OPEN_SEXP_BRACES.contains(token_type)) {
      PsiBuilder.Marker listMarker = builder.mark();
      IElementType list_mark_type;
      IElementType list_close = getCloseParen(token_type);
      builder.advanceLexer();
      helperListAdvance(builder, list_close, 0);
      token_type = builder.getTokenType();
      while (token_type != null && token_type != list_close) {
        if (token_type == NAME_LITERAL) {
          markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
        } else {
          markASexp(builder, AST.AST_BAD_ELEMENT);
        }
        helperListAdvance(builder, list_close, 0);
        token_type = builder.getTokenType();
      }
      listMarker.done(eatRemainList(builder, list_close, AST.AST_BODY_IN_FORM_PARAM_LIST));
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormDefineRecordType(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_DEFINE_RECORD_TYPE;
    IElementType token_type = builder.getTokenType();
    if (token_type == NAME_LITERAL) {
      markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
    } else {
      markASexp(builder, AST.AST_BAD_ELEMENT);
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormDefineSyntax(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_DEFINE_SYNTAX;
    IElementType token_type = builder.getTokenType();
    if (token_type == NAME_LITERAL) {
      markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
    } else {
      markASexp(builder, AST.AST_BAD_ELEMENT);
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormDo(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_DO;
    helperListAdvance(builder, close, 0);
    IElementType token_type = builder.getTokenType();
    if (OPEN_SEXP_BRACES.contains(token_type)) {
      PsiBuilder.Marker listMarker = builder.mark();
      IElementType list_close = getCloseParen(token_type);
      IElementType sub_list_token_type;
      builder.advanceLexer();
      helperListAdvance(builder, list_close, 0);
      sub_list_token_type = builder.getTokenType();
      while (sub_list_token_type != null && sub_list_token_type != list_close) {
        if (OPEN_SEXP_BRACES.contains(sub_list_token_type)) {
          PsiBuilder.Marker subListMarker = builder.mark();
          IElementType sub_list_close = getCloseParen(sub_list_token_type);
          builder.advanceLexer();
          helperListAdvance(builder, sub_list_close, 0);
          IElementType name_token_type = builder.getTokenType();
          if (name_token_type == NAME_LITERAL) {
            markAToken(builder, AST.AST_BASIC_ELE_SYMBOL_DEFINE);
            subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_TEMP_LIST));
          } else if (name_token_type == sub_list_close) {
            subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_BAD_ELEMENT));
          } else {
            markASexp(builder, AST.AST_BAD_ELEMENT);
            subListMarker.done(eatRemainList(builder, sub_list_close, AST.AST_BAD_ELEMENT));
          }
        } else {
          PsiBuilder.Marker marker = builder.mark();
          parseSexp(builder.getTokenType(), builder);
          marker.collapse(AST.AST_BAD_ELEMENT);
        }
        helperListAdvance(builder, list_close, 0);
        sub_list_token_type = builder.getTokenType();
      }
      if (sub_list_token_type != list_close) {
        listMarker.done(eatRemainList(builder, list_close, AST.AST_BAD_ELEMENT));
      } else {
        listMarker.done(eatRemainList(builder, list_close, AST.AST_BODY_IN_FORM_PARAM_LIST));
      }
    }
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormList(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_LIST;
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormLibrary(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_LIBRARY;
    if (helperListAdvance(builder, close, 3)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
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
    IElementType form_mark_type = AST.AST_FORM_BEGIN;
    if (helperListAdvance(builder, close, 0)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
  }

  IElementType parseFormIf(PsiBuilder builder, IElementType close)
  {
    IElementType form_mark_type = AST.AST_FORM_IF;
    if (helperListAdvance(builder, close, 1)) {
      PsiBuilder.Marker bodyMarker = builder.mark();
      helperEatFormBody(builder, close, bodyMarker);
    }
    return eatRemainList(builder, close, form_mark_type);
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
      mark_type = parseFormLetForms(builder, close, AST.AST_FORM_LET);
    }
    else if (text.equals("letrec"))
    {
      mark_type = parseFormLetForms(builder, close, AST.AST_FORM_LETREC);
    }
    else if (text.equals("let*"))
    {
      mark_type = parseFormLetForms(builder, close, AST.AST_FORM_LET_A);
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

    helperListAdvance(builder, close, 0);

    IElementType token_type = builder.getTokenType();
    if (token_type == null) {
      builder.error("parse sexp failed");
      marker.drop();
      return null;
    }

    String token_text = builder.getTokenText();
    if (token_text == null) {
      builder.error("token is null, something is wrong");
      marker.drop();
      return null;
    }

    if (token_type == close) {
      builder.advanceLexer();
      marker.done(mark_type);
      return mark_type;
    }

    IElementType exp_type;
    exp_type = parseSexp(token_type, builder);
    helperListAdvance(builder, close, 0);
    if (exp_type == null)
    {
      builder.error("parse sexp failed");
      marker.drop();
      return null;
    }
    else if ((exp_type == AST.AST_BASIC_ELE_KEYWORD)
            || (exp_type == AST.AST_BASIC_ELE_PROCEDURE)
            || (exp_type == AST.AST_FORM_PROCEDURE))
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
    else
    {
      mark_type = eatRemainList(builder, close, AST.AST_UNRECOGNIZED_FORM);
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
      syntaxError(builder, "'(' expected");
    }
    else if (RIGHT_SQUARE == type)
    {
      syntaxError(builder, "Expected [");
    }
    else
    {
      syntaxError(builder, "Run Error");
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
  void markAToken(PsiBuilder builder, IElementType mark_type)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    marker.done(mark_type);
  }

  void markASexp(PsiBuilder builder, IElementType mark_type)
  {
    PsiBuilder.Marker marker = builder.mark();
    parseSexp(builder.getTokenType(), builder);
    marker.done(mark_type);
  }

  IElementType parseAtom(IElementType type, PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    IElementType mark_type = atomMarkType(type);
    marker.done(mark_type);
    return mark_type;
  }

  IElementType parsePrefix(IElementType type, PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    IElementType childType = builder.getTokenType();
    IElementType mark_type = null;

    if (childType == null ||
        WHITESPACE == childType ||
        COMMENTS.contains(childType))
    {
      syntaxErrorJustBad(builder, "Expected element");
      marker.drop();
      return null;
    }

    if (DATUM_COMMENT_PRE == type) {
      parseSexp(childType, builder);
      mark_type = SchemeTokens.DATUM_COMMENT;
      marker.collapse(SchemeTokens.DATUM_COMMENT);
      return mark_type;
    }

    if (DATUM_PREFIXES.contains(type)) {
      parseSexp(childType, builder);
      mark_type = datumPrefixMarkType(type);
      marker.done(mark_type);
      return mark_type;
    }

    syntaxError(builder, "Run Error");
    marker.drop();
    return null;
  }

  IElementType parseVector(PsiBuilder builder)
  {
    PsiBuilder.Marker marker = builder.mark();
    builder.advanceLexer();
    IElementType mark_type  = eatRemainList(builder, RIGHT_PAREN, AST.AST_ELE_VECTOR);
    marker.done(mark_type);
    return mark_type;
  }

  IElementType parseNonParen(IElementType type, PsiBuilder builder)
  {
    if (DATUM_PREFIXES.contains(type)
        || DATUM_COMMENT_PRE == type) {
      return parsePrefix(type, builder);
    }
    else if (OPEN_VECTOR == type)
    {
      return parseVector(builder);
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
      syntaxError(builder, "'(' expected");
      return null;
    }
    else if (RIGHT_SQUARE == type)
    {
      syntaxError(builder, "Expected [");
      return null;
    }
    else
    {
      syntaxError(builder, "Run Error");
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

  private IElementType parseList(PsiBuilder builder, IElementType open, IElementType close)
  {
    IElementType mark_type = AST.AST_TEMP_LIST;
    PsiBuilder.Marker marker = markAndAdvance(builder);

    helperListAdvance(builder, close, 0);

    IElementType token_type = builder.getTokenType();
    if (token_type == null) {
      builder.error("parse sexp failed");
      marker.drop();
      return null;
    }

    String token_text = builder.getTokenText();
    if (token_text == null) {
      builder.error("token is null, something is wrong");
      marker.drop();
      return null;
    }

    if (token_type == close) {
      builder.advanceLexer();
      marker.done(mark_type);
      return mark_type;
    }

    IElementType exp_type;
    exp_type = parseSexp(token_type, builder);
    if (exp_type == null)
    {
      builder.error("parse sexp failed");
      marker.drop();
      return null;
    }
    else if ((exp_type == AST.AST_BASIC_ELE_KEYWORD)
            || (exp_type == AST.AST_BASIC_ELE_PROCEDURE)
            || (exp_type == AST.AST_FORM_PROCEDURE))
    {
      mark_type = parseTopAndLocalForm(builder, close, token_text);
      if (mark_type == null)
      {
        mark_type = eatRemainList(builder, close, AST.AST_FORM_CALL_PROCEDURE);
      }
    }
    else
    {
      mark_type = eatRemainList(builder, close, AST.AST_UNRECOGNIZED_FORM);
    }

    marker.done(mark_type);

    return mark_type;
  }
}
