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
  private int lex_start_pos;
  private int lexed_end_pos;
  private int token_length;
  private Tokens.Fragment token_frag;
  private int bufferEnd;
  protected IElementType type;

  enum Tag {
    TAG_LINE_COMMENT,
    TAG_BLOCK_COMMENT,
    TAG_DATUM_COMMENT_PREFIX,
    TAG_WHITE_SPACE,
    TAG_OP_SINGLE_CHAR,
    TAG_OP_OPEN_VECTOR,
    TAG_OP_ABBREVIATIONS,
    TAG_NUMBER,
    TAG_BOOLEAN,
    TAG_QUOTE_STRING,
    TAG_SHARP_CHAR,
    TAG_NAME_LITERAL,
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
  Parser<?> SCA_DATUM_COMMENT_PREFIX = Scanners.string("#;");
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
  Parser<?> s_op_open_vector = Parsers.or(Scanners.string("#("), Scanners.string("#vu8("))
          .source().map((a) -> (Tokens.fragment(a, Tag.TAG_OP_OPEN_VECTOR)));
  List<String> STRS_ABBREVIATION = asList(",@", "#'", "#`", "#,", "#,@");
  Terminals TERM_ABBREVIATIONS = Terminals
          .operators(STRS_ABBREVIATION);
  Parser<?> s_op_abbreviations = TERM_ABBREVIATIONS.tokenizer().source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_OP_ABBREVIATIONS)));
  Parser<?> PAR_OPERATORS = Parsers.or(s_op_abbreviations, s_op_open_vector, s_op_single_char);

  // Identifier
  Pattern PT_INLINE_HEX_ESCAPE = Patterns.sequence(Patterns.string("\\x"),
          Patterns.many1(CharPredicates.IS_HEX_DIGIT), Patterns.isChar(';'));
  Parser<?> PAR_INLINE_HEX_ESCAPE = PT_INLINE_HEX_ESCAPE.toScanner("inline hex escape").source();
  Pattern PT_EXTENDED_ALPHABETIC_CHAR = Patterns.among("!$%&*+-./:<=>?@^_~");
  Pattern PT_NAME_LITERAL_CHAR_VALID = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  Patterns.isChar(CharPredicates.IS_DIGIT),
                  PT_EXTENDED_ALPHABETIC_CHAR, PT_INLINE_HEX_ESCAPE);
  Pattern PT_NAME_LITERAL_FIRST_CHAR = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  PT_EXTENDED_ALPHABETIC_CHAR, PT_INLINE_HEX_ESCAPE);
  Pattern PT_NAME_LITERAL = PT_NAME_LITERAL_FIRST_CHAR.next(PT_NAME_LITERAL_CHAR_VALID.many());
  Parser<String> PAR_NAME_LITERAL = PT_NAME_LITERAL.toScanner("name literal").source();
  Parser<?> s_name_literal = PAR_NAME_LITERAL
          .map((a) -> (Tokens.fragment(a, Tag.TAG_NAME_LITERAL)));

  // Numbers
  Pattern PT_RIGHT_INTEGER = Patterns.sequence(Patterns.among("+-").optional(), Patterns.INTEGER);
  Parser<String> PAR_RIGHT_INTEGER = PT_RIGHT_INTEGER.toScanner("integer").source();
  Parser<?> PAR_BIN_INTEGER = Patterns.string("#b").or(Patterns.string("#B"))
          .next(Patterns.many1(CharPredicates.range('0', '1'))).toScanner("bin integer").source();
  Parser<?> PAR_OCT_INTEGER = Patterns.string("#o").or(Patterns.string("#O"))
          .next(Patterns.many1(CharPredicates.range('0', '7'))).toScanner("oct integer").source();
  Parser<?> PAR_DEC_INTEGER = Patterns.string("#d").or(Patterns.string("#D"))
          .next(Patterns.many1(CharPredicates.range('0', '9'))).toScanner("dec integer").source();
  Parser<?> PAR_HEX_INTEGER = Patterns.string("#x").or(Patterns.string("#X"))
          .next(Patterns.many1(CharPredicates.IS_HEX_DIGIT)).toScanner("hex integer").source();
  Parser<?> s_numbers = Parsers.or(
                  PAR_RIGHT_INTEGER, Scanners.DEC_INTEGER,
                  Scanners.DECIMAL, Scanners.SCIENTIFIC_NOTATION,
                  PAR_BIN_INTEGER, PAR_OCT_INTEGER, PAR_DEC_INTEGER, PAR_HEX_INTEGER)
          .map((a) -> (Tokens.fragment("number", Tag.TAG_NUMBER)));

  // Characters
  List<String> STRS_SPECIAL_CHAR_NAME = asList("nul", "alarm",
          "backspace", "tab", "linefeed",
          "newline", "vtab", "page", "return", "esc",
          "space", "delete", "vtab", "λ",
          "rubout", "bel", "vt", "nel", "ls");
  Terminals TERM_SPECIAL_CHAR_NAMES = Terminals
          .operators(STRS_SPECIAL_CHAR_NAME);
  Pattern PT_SINGLE_CHAR = Patterns.ANY_CHAR.next(PT_NAME_LITERAL.not());
  Pattern PT_HEX_CHAR = Patterns.sequence(Patterns.among("xX"), Patterns.many1(CharPredicates.IS_HEX_DIGIT))
          .next(PT_NAME_LITERAL.not());
  Pattern PT_CHAR_PREFIX = Patterns.string("#\\");

  Parser<?> SCA_CHAR_PREFIX = PT_CHAR_PREFIX.toScanner("char prefix");
  Parser<?> SCA_SPECIAL_CHAR_NAMES = TERM_SPECIAL_CHAR_NAMES.tokenizer().next(PAR_NAME_LITERAL.not());

  Parser<?> SCA_SINGLE_CHAR = Patterns.sequence(PT_CHAR_PREFIX, PT_SINGLE_CHAR).toScanner("char");
  Parser<?> SCA_HEX_CHAR = Patterns.sequence(PT_CHAR_PREFIX, PT_HEX_CHAR).toScanner("hex char");
  Parser<?> SCA_SPECIAL_CHAR = Parsers.sequence(SCA_CHAR_PREFIX, SCA_SPECIAL_CHAR_NAMES).label("special char");
  Parser<?> s_sharp_char = Parsers.or(SCA_SPECIAL_CHAR, SCA_HEX_CHAR, SCA_SINGLE_CHAR).source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_SHARP_CHAR)));

  // String
  Parser<?> PAR_STRING = Scanners.DOUBLE_QUOTE_STRING
          .map((a) -> (Tokens.fragment(a, Tag.TAG_QUOTE_STRING)));

  // Boolean
  Terminals TERM_BOOLEAN = Terminals
          .operators("#t", "#f", "#T", "#F");
  Parser<?> s_boolean = TERM_BOOLEAN.tokenizer().source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_BOOLEAN)));

  Parser<?> PAR_LITERALS = Parsers.or(PAR_STRING, s_numbers, s_boolean, s_name_literal, s_sharp_char);

  /**
   * Some built-in elements
   */
  // Keyword
  List<String> STRS_KEYWORD = asList(
          "and", "begin", "case", "cond", "define",
          "delay", "do", "else", "if", "lambda", "let",
          "let*", "letrec", "or", "quote", "quasiquote",
          "quasisyntax", "set!", "syntax", "unquote", "unquote-splicing",
          "unsyntax", "unsyntax-splicing");
  Terminals TERM_KEYWORDS = Terminals
        .operators(STRS_KEYWORD);
  Parser<?> s_keywords = TERM_KEYWORDS.tokenizer().next(PAR_NAME_LITERAL.not()).source()
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
  Parser<?> s_builtin_procedures = TERM_BUILTIN_PROCEDURES.tokenizer().next(PAR_NAME_LITERAL.not()).source()
          .map((a) -> (Tokens.fragment(a, Tag.TAG_PROCEDURE)));

  Parser<?> PAR_BUILTIN_ELEMENTS = Parsers.or(s_keywords, s_builtin_procedures);

  // Bad elements
  Parser<?> PAR_ELEMENT = Parsers.or(s_whitespace, PAR_COMMENT,
          PAR_BUILTIN_ELEMENTS, PAR_LITERALS, PAR_OPERATORS);
  Parser<String> PAR_ANY_CHAR = Scanners.ANY_CHAR.source();
  Parser<?> s_bad_element = PAR_ANY_CHAR
          .map((a) -> (Tokens.fragment(a, Tag.TAG_BAD_CHARACTER)));

  Parser<Object> PAR_TOKEN = Parsers.or(PAR_ELEMENT, s_bad_element);

  Parser<Token> s_token = PAR_TOKEN
          .map((a) -> {
            if (null != a) {
//              System.out.println("a: " + a);
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
    this.lex_start_pos = startOffset;
    this.lexed_end_pos = startOffset;
    this.bufferEnd = endOffset;
    this.token_frag = null;
    decodeState(initialState);
    advance();
  }

  int cur_token_start = 0;
  int cur_token_end = 0;

  @Override
  public void advance()
  {
    if (lex_start_pos >= bufferEnd)
    {
      type = null;
      return;
    }

    this.token_frag = null;
    if (lexed_end_pos >= bufferEnd)
    {
      lex_start_pos = lexed_end_pos;
      type = null;
      return;
    }

    lex_start_pos = lexed_end_pos;
    cur_token_start = lex_start_pos;
    CharSequence myBuffer = buffer.subSequence(this.lex_start_pos, this.bufferEnd);

    try {
      s_token.parse(myBuffer);

    } catch (Exception e) {
//      System.out.println("advance Exception: " + e.getMessage());

      if (null == token_frag) {
//        System.out.println("advance no valid token");
        type = null;
        return;
      }
    }

    lexed_end_pos = lex_start_pos + token_length;
    if (lexed_end_pos > bufferEnd) {
      lexed_end_pos = bufferEnd;
    }
    cur_token_end = lexed_end_pos;

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

      case TAG_OP_OPEN_VECTOR:
        type = SchemeTokens.OPEN_VECTOR;
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
        return;

      case TAG_SHARP_CHAR:
        type = SchemeTokens.CHAR_LITERAL;
        break;

      case TAG_KEYWORD:
        type = SchemeTokens.KEYWORD;
        break;

      case TAG_PROCEDURE:
        type = SchemeTokens.PROCEDURE;
        break;

      case TAG_NAME_LITERAL:
        type = SchemeTokens.NAME_LITERAL;
        break;

      case TAG_BAD_CHARACTER:
        type = SchemeTokens.BAD_CHARACTER;
        break;

      default:
        type = null;
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
    return cur_token_start;
  }

  @Override
  public int getTokenEnd()
  {
    return cur_token_end;
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
