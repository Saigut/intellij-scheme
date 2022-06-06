package main.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Token;
import org.jparsec.Tokens;
import org.jparsec.Terminals;
import org.jparsec.pattern.CharPredicates;
import org.jparsec.pattern.Pattern;
import org.jparsec.pattern.Patterns;

import java.util.List;

import static java.util.Arrays.asList;


/**
 * @author Colin Fleming
 */
public class SchemeLexer extends LexerBase
{
  private CharSequence buffer;
  private CharSequence myBuffer;
  private int start;
  private int end;
  private int token_index;
  private int token_length;
  private Tokens.Fragment token_frag;
  protected int cursor;
  private int bufferEnd;
  protected IElementType type;



  enum Tag {
    TAG_LINE_COMMENT,
    TAG_BLOCK_COMMENT,
    TAG_DATUM_COMMENT_PREFIX,
    TAG_WHITE_SPACE,
    TAG_OP_SINGLE_CHAR,
    TAG_OP_SHARP_MARK,
    TAG_OP_ABBREVIATIONS,
    TAG_NUMBER,
    TAG_BOOLEAN,
    TAG_QUOTE_STRING,
    TAG_SHARP_CHAR,
    TAG_IDENTIFIER,
    TAG_BAD_CHARACTER,

    TAG_KEYWORD,
    TAG_PROCEDURE
  }

  /**
   * Helpers
   */
  private static Pattern notChar2(final char c1, final char c2) {
    return new Pattern() {
      @Override public int match(CharSequence src, int begin, int end) {
        if (begin == end - 1) return 1;
        if (begin >= end) return MISMATCH;
        if (src.charAt(begin) == c1 && src.charAt(begin + 1) == c2) return Pattern.MISMATCH;
        return 1;
      }
    };
  }

  /**
   * Tokens
   */
  // Blanks and Comments
  Pattern PT_DATUM_COMMENT_PREFIX = Patterns.string("#;");
  Parser<?> SCA_DATUM_COMMENT_PREFIX = PT_DATUM_COMMENT_PREFIX.toScanner("#;");
  Parser<?> s_datum_comment_prefix = SCA_DATUM_COMMENT_PREFIX.source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_DATUM_COMMENT_PREFIX)));

  Parser<?> SCA_LINE_COMMENT = Patterns.lineComment(";").toScanner(";");
  Parser<?> s_line_comment = SCA_LINE_COMMENT.source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_LINE_COMMENT)));
  Parser<?> SCA_BLOCK_COMMENT_CONTENT =
          notChar2('|', '#').many().toScanner("commented block");
  Parser<?> s_block_comment = Parsers.sequence(Scanners.string("#|"),
                  SCA_BLOCK_COMMENT_CONTENT, Scanners.string("|#")).source()
          .map((a) -> (Tokens.fragment("#||#", Tag.TAG_BLOCK_COMMENT)));
  Parser<?> PAR_COMMENT = Parsers.or(s_datum_comment_prefix, s_line_comment, s_block_comment);

  Parser<?> s_whitespace = Scanners.WHITESPACES
          .map((a) -> (Tokens.fragment("WHITE_SPACE", Tag.TAG_WHITE_SPACE)));

  // Operators
  Pattern PT_OP_SINGLE_CHAR = Patterns.among("()[]'`,");
  Parser<?> s_op_single_char = PT_OP_SINGLE_CHAR.toScanner("operator").source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_OP_SINGLE_CHAR)));
  Parser<?> s_op_sharp_mark = Patterns.string("#").toScanner("#")
          .source().map((a) -> (Tokens.fragment(a, Tag.TAG_OP_SHARP_MARK)));
  List<String> STRS_ABBREVIATION = asList(",@", "#'", "#`", "#,", "#,@");
  Terminals TERM_ABBREVIATIONS = Terminals
          .operators(STRS_ABBREVIATION);
  Parser<?> s_op_abbreviations = TERM_ABBREVIATIONS.tokenizer().source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_OP_ABBREVIATIONS)));
  Parser<?> PAR_OPERATORS = Parsers.or(s_op_abbreviations, s_op_sharp_mark, s_op_single_char);

  // Numbers
  Pattern PT_RIGHT_INTEGER = Patterns.sequence(Patterns.among("+-").optional(), Patterns.INTEGER);
  Parser<String> PAR_RIGHT_INTEGER = PT_RIGHT_INTEGER.toScanner("integer").source();
  Parser<?> s_numbers = Parsers.or(
          PAR_RIGHT_INTEGER,
          Scanners.DECIMAL,
          Scanners.DEC_INTEGER, Scanners.OCT_INTEGER,
          Scanners.HEX_INTEGER,
          Scanners.SCIENTIFIC_NOTATION)
          .map((a) -> (Tokens.fragment(a, Tag.TAG_NUMBER)));

  // Identifier
  Pattern PT_IDENTIFIER_CHAR_VALID = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  Patterns.isChar(CharPredicates.IS_DIGIT),
                  Patterns.among("!$%&*+-./:<=>?@^_~"));
  Pattern PT_IDENTIFIER = PT_IDENTIFIER_CHAR_VALID.many1();
  Parser<String> PAR_IDENTIFIER = PT_IDENTIFIER.toScanner("identifier").source();
  Parser<?> s_identifier = PAR_IDENTIFIER
          .map((a) -> (Tokens.fragment(a, Tag.TAG_IDENTIFIER)));

  // Characters
  List<String> STRS_SPECIAL_CHAR_NAME = asList("nul", "alarm",
          "backspace", "tab", "linefeed",
          "newline", "vtab", "page", "return", "esc",
          "space", "delete", "vtab", "λ");
  Terminals TERM_SPECIAL_CHAR_NAMES = Terminals
          .operators(STRS_SPECIAL_CHAR_NAME);
  Pattern PT_SINGLE_CHAR = Patterns.ANY_CHAR.next(PT_IDENTIFIER.not());
  Pattern PT_HEX = Patterns.sequence(Patterns.among("xX"), Patterns.among("0123456789abcdefABCDEF").many1())
          .next(PT_IDENTIFIER.not());
  Pattern PT_CHAR_PREFIX = Patterns.string("#\\");

  Parser<?> SCA_CHAR_PREFIX = PT_CHAR_PREFIX.toScanner("char prefix");
  Parser<?> SCA_SPECIAL_CHAR_NAMES = TERM_SPECIAL_CHAR_NAMES.tokenizer().next(PAR_IDENTIFIER.not());

  Parser<?> SCA_CHAR = Patterns.sequence(PT_CHAR_PREFIX, PT_SINGLE_CHAR).toScanner("char");
  Parser<?> SCA_HEX_NUMBER = Patterns.sequence(PT_CHAR_PREFIX, PT_HEX).toScanner("hex number");
  Parser<?> SCA_SPECIAL_CHAR = Parsers.sequence(SCA_CHAR_PREFIX, SCA_SPECIAL_CHAR_NAMES).label("special char");
  Parser<?> s_sharp_char = Parsers.or(SCA_CHAR, SCA_SPECIAL_CHAR, SCA_HEX_NUMBER).source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_SHARP_CHAR)));

  Parser<?> s_string = Scanners.DOUBLE_QUOTE_STRING
          .map((a) -> (Tokens.fragment(a, Tag.TAG_QUOTE_STRING)));

  // Boolean
  Terminals TERM_BOOLEAN = Terminals
          .operators("#t", "#f");
  Parser<?> s_boolean = TERM_BOOLEAN.tokenizer().source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_BOOLEAN)));

  Parser<?> PAR_LITERALS = Parsers.or(s_string, s_numbers, s_boolean, s_sharp_char, s_identifier);

  /**
   * Some built-in elements
   */
  // Keyword
  List<String> STRS_KEYWORD = asList(
          "and", "begin", "case", "cond", "define",
          "delay", "do", "else", "if", "lambda", "let",
          "let*", "letrec", "quasiquote", "or", "set!",
          "unquote", "unquote-splicing");
  Terminals TERM_KEYWORDS = Terminals
        .operators(STRS_KEYWORD);
  Parser<?> s_keywords = TERM_KEYWORDS.tokenizer().next(PAR_IDENTIFIER.not()).source()
        .map((a) -> (Tokens.fragment(a, Tag.TAG_KEYWORD)));

  // Built-in Procedures
  List<String> STRS_BUILTIN_PROCEDURE = asList("*", "+", "-", "/",
          "<", "<=", "=", "=>", ">", ">=",
          "abs", "acos", "angle", "append",
          "apply", "asin", "assert", "assertion-violation", "atan",
          "begin0", "boolean=?", "boolean?", "caar",
          "cadr", "call-with-current-continuation", "call-with-values", "call/cc", "car",
          "cdddar", "cddddr", "cdr", "ceiling",
          "char->integer", "char<=?", "char<?", "char=?", "char>=?",
          "char>?", "char?", "complex?", "condition?",
          "cons", "consi", "cos", "define-syntax",
          "denominator", "div", "div-and-mod", "div0", "div0-and-mod0",
          "dot", "dw", "dynamic-wind", "eq?",
          "equal?", "eqv?", "error", "even?", "exact",
          "exact-integer-sqrt", "exact?", "exp", "export", "expt",
          "finite?", "floor", "for-each", "gcd", "identifier-syntax",
          "imag-part", "import", "inexact", "inexact?",
          "infinite?", "integer->char", "integer-valued?", "integer?",
          "lcm", "length", "let*-values",
          "let-syntax", "let-values", "letrec*", "letrec-syntax",
          "library", "list", "list->string", "list->vector", "list-ref",
          "list-tail", "list?", "log", "magnitude", "make-polar",
          "make-rectangular", "make-string", "make-vector", "map", "max",
          "min", "mod", "mod0", "nan?", "negative?",
          "not", "null", "null?", "number->string", "number?",
          "numerator", "odd?", "pair?", "positive?",
          "procedure?", "quote", "raise", "raise-continuable",
          "rational-valued?", "rational?", "rationalize", "real-part", "real-valued?",
          "real?", "reverse", "round", "set-car!",
          "set-cdr!", "sin", "sqrt", "string", "string->list",
          "string->number", "string->symbol", "string-append", "string-copy", "string-for-each",
          "string-length", "string-ref", "string<=?", "string<?", "string=?",
          "string>=?", "string>?", "string?", "substring", "symbol->string",
          "symbol=?", "symbol?", "syntax-rules", "tan", "throw",
          "truncate", "values", "vector",
          "vector->list", "vector-fill!", "vector-for-each", "vector-length", "vector-map",
          "vector-ref", "vector-set!", "vector?", "with-exception-handler", "zero?");
  Terminals TERM_BUILTIN_PROCEDURES = Terminals
          .operators(STRS_BUILTIN_PROCEDURE);
  Parser<?> s_builtin_procedures = TERM_BUILTIN_PROCEDURES.tokenizer().next(PAR_IDENTIFIER.not()).source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_PROCEDURE)));

  Parser<?> PAR_BUILTIN_ELEMENTS = Parsers.or(s_keywords, s_builtin_procedures);

  // Bad elements
  Parser<?> PAR_ELEMENT = Parsers.or(s_whitespace, PAR_COMMENT,
          PAR_BUILTIN_ELEMENTS, PAR_LITERALS, PAR_OPERATORS);
  Pattern PT_ANY_CHAR = Patterns.isChar(CharPredicates.ALWAYS);
  Parser<String> PAR_ANY_CHAR = PT_ANY_CHAR.toScanner("any char").source();
  Parser<?> s_bad_element = PAR_ANY_CHAR
          .map((a) -> (Tokens.fragment(a, Tag.TAG_BAD_CHARACTER)));

  Parser<Object> PAR_TOKEN = Parsers.or(PAR_ELEMENT, s_bad_element);

  Parser<Token> s_token = PAR_TOKEN
          .map((a) -> {
            if (null != a) {
//              System.out.println("a: " + a.toString());
              if (a.getClass() == Tokens.Fragment.class) {
                token_frag = (Tokens.Fragment)a;
//                System.out.println("type: " + ((Tokens.Fragment)a).tag().toString());
//                System.out.println("text: " + ((Tokens.Fragment)a).text());
              } else {
                token_frag = null;
//                System.out.println("type: " + a.getClass().getName());
              }
            } else {
              token_frag = null;
//              System.out.println("a: null");
            }
            return a;
          }).token()
          .map((a) -> {
            token_index = a.index();
            token_length = a.length();
//            System.out.println("index: " + a.index() + ", length: " + a.length());
            return a;
          }).atomic();

  @Override
  public void start(CharSequence buffer, int startOffset, int endOffset, int initialState)
  {
//    System.out.println("startOffset: " + startOffset + ", endOffset: " + endOffset
//            + ". initialState: " + initialState);

    this.buffer = buffer;
    this.start = startOffset;
    this.end = startOffset;
    this.bufferEnd = endOffset;
    this.token_frag = null;
    decodeState(initialState);
    advance();
  }


  @Override
  public void advance()
  {
    if (start >= bufferEnd)
    {
      type = null;
      return;
    }
    if (end >= bufferEnd)
    {
      start = end;
      type = null;
      return;
    }
    this.token_frag = null;
    start = end;
    myBuffer = buffer.subSequence(this.start, this.bufferEnd);

    try {
      s_token.parse(myBuffer);

    } catch (Exception e) {
//      System.out.println("Exception: " + e.getMessage());

      if (null == token_frag) {
//        System.out.println("no valid token");
        type = null;
        return;
      }
    }

    switch ((Tag)token_frag.tag()) {

      case TAG_LINE_COMMENT:
        type = SchemeTokens.LINE_COMMENT;
        break;

      case TAG_BLOCK_COMMENT:
        type = SchemeTokens.BLOCK_COMMENT;
        break;

      case TAG_DATUM_COMMENT_PREFIX:
        type = SchemeTokens.DATUM_COMMENT_PRE;
        break;

      case TAG_WHITE_SPACE:
        type = SchemeTokens.WHITESPACE;
        break;

      case TAG_OP_SINGLE_CHAR: {
        String opStr = token_frag.text();
        switch (opStr) {
          case "(":
            type = SchemeTokens.LEFT_PAREN;

            break;
          case ")":
            type = SchemeTokens.RIGHT_PAREN;

            break;
          case "[":
            type = SchemeTokens.LEFT_SQUARE;

            break;
          case "]":
            type = SchemeTokens.RIGHT_SQUARE;

            break;
          case "'":
            type = SchemeTokens.QUOTE;

            break;
          case "`":
            type = SchemeTokens.QUASIQUOTE;

            break;
          case ",":
            type = SchemeTokens.UNQUOTE;

            break;
          default:
            type = SchemeTokens.SPECIAL;
            break;
        }

        break;
      }

      case TAG_OP_SHARP_MARK:
        type = SchemeTokens.SHARP_MARK;
        break;

      case TAG_OP_ABBREVIATIONS: {
        String opStr = token_frag.text();
        switch (opStr) {
          case ",@":
            type = SchemeTokens.UNQUOTE_SPLICING;
            break;

          case "#'":
            type = SchemeTokens.SYNTAX;
            break;

          case "#`":
            type = SchemeTokens.QUASISYNTAX;
            break;

          case "#,":
            type = SchemeTokens.UNSYNTAX;
            break;

          case "#,@":
            type = SchemeTokens.UNSYNTAX_SPLICING;
            break;

          default:
            type = SchemeTokens.SPECIAL;
            break;
        }

        break;
      }

      case TAG_NUMBER:
        type = SchemeTokens.NUMBER_LITERAL;
        break;

      case TAG_BOOLEAN:
        type = SchemeTokens.BOOLEAN_LITERAL;
        break;

      case TAG_QUOTE_STRING:
        type = SchemeTokens.STRING_LITERAL;
        break;

      case TAG_SHARP_CHAR:
        type = SchemeTokens.CHAR_LITERAL;
        break;

      case TAG_KEYWORD:
        type = SchemeTokens.KEYWORD;
        break;

      case TAG_PROCEDURE:
        type = SchemeTokens.PROCEDURE;
        break;

      case TAG_IDENTIFIER:
        type = SchemeTokens.PLAIN_LITERAL;
        break;

      case TAG_BAD_CHARACTER:
        type = SchemeTokens.BAD_CHARACTER;
        break;

      default:
        type = null;
        return;
    }

    cursor += token_length;
    end = start + token_length;
    if (end > bufferEnd) {
      end = bufferEnd;
    }
  }

  @Override
  public int getState()
  {
    return encodeState();
  }

  @Override
  public IElementType getTokenType()
  {
    return type;
  }

  @Override
  public int getTokenStart()
  {
    return start;
  }

  @Override
  public int getTokenEnd()
  {
    return end;
  }

  @Override
  public CharSequence getBufferSequence()
  {
    return buffer;
  }

  @Override
  public int getBufferEnd()
  {
    return bufferEnd;
  }

  private void decodeState(int state)
  {
  }

  private int encodeState()
  {
    return 0;
  }
}
