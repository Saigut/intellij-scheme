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
    S_LINE_COMMENT,
    S_BLOCK_COMMENT,
    S_DATUM_COMMENT_PREFIX,
    S_WHITE_SPACE,
    S_OPERATOR,
    S_NUMBER,
    S_QUOTE_STRING,
    S_SHARP_CHAR,
    S_IDENTIFIER,
    S_BAD_CHARACTER,

    S_KEYWORD
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
   * Elements
   */
  // Blanks and Conments
  Pattern PT_DATUM_COMMENT_PREFIX = Patterns.string("#;");
  Parser<?> SCA_DATUM_COMMENT_PREFIX = PT_DATUM_COMMENT_PREFIX.toScanner("datum comment prefix");
  Parser<?> s_datum_comment_prefix = SCA_DATUM_COMMENT_PREFIX.source()
          .map((a) -> (Tokens.fragment("#;", Tag.S_DATUM_COMMENT_PREFIX)));

  Parser<?> SCA_LINE_COMMENT = Patterns.lineComment(";").toScanner(";");
  Parser<?> s_line_comment = SCA_LINE_COMMENT.source()
          .map((a) -> (Tokens.fragment(";", Tag.S_LINE_COMMENT)));
  Parser<?> SCA_BLOCK_COMMENT_CONTENT =
          notChar2('|', '#').many().toScanner("commented block");
  Parser<?> s_block_comment = Parsers.sequence(Scanners.string("#|"),
                  SCA_BLOCK_COMMENT_CONTENT, Scanners.string("|#")).source()
          .map((a) -> (Tokens.fragment("#||#", Tag.S_BLOCK_COMMENT)));
  Parser<?> PAR_COMMENT = Parsers.or(s_datum_comment_prefix, s_line_comment, s_block_comment);

  Parser<?> s_whitespace = Scanners.WHITESPACES
          .map((a) -> (Tokens.fragment("WHITE_SPACE", Tag.S_WHITE_SPACE)));

  // Operators
  Pattern PT_OPS = Patterns.among("()[]'`,");
  Pattern PT_OP_SHARP_PAREN = Patterns.isChar('#').next(Patterns.isChar('(').peek());
  Parser<String> PAR_OPERATORS = Patterns.or(PT_OPS, PT_OP_SHARP_PAREN)
          .toScanner("operator").source();
  Parser<?> s_operators = PAR_OPERATORS
          .map((a) -> (Tokens.fragment(a, Tag.S_OPERATOR)));

  // Numbers
  Pattern PT_RIGHT_INTEGER = Patterns.sequence(Patterns.among("+-").optional(), Patterns.INTEGER);
  Parser<String> PAR_RIGHT_INTEGER = PT_RIGHT_INTEGER.toScanner("integer").source();
  Parser<?> s_numbers = Parsers.or(
          PAR_RIGHT_INTEGER,
          Scanners.DECIMAL,
          Scanners.DEC_INTEGER, Scanners.OCT_INTEGER,
          Scanners.HEX_INTEGER,
          Scanners.SCIENTIFIC_NOTATION)
          .map((a) -> (Tokens.fragment(a, Tag.S_NUMBER)));

  // Identifier
  Pattern PT_IDENTIFIER_CHAR_VALID = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  Patterns.isChar(CharPredicates.IS_DIGIT),
                  Patterns.among("!@$%^&*-+_=:|/?<>."));
  Pattern PT_IDENTIFIER = PT_IDENTIFIER_CHAR_VALID.many1();
  Parser<String> PAR_IDENTIFIER = PT_IDENTIFIER.toScanner("identifier").source();
  Parser<?> s_identifier = PAR_IDENTIFIER
          .map((a) -> (Tokens.fragment(a, Tag.S_IDENTIFIER)));


  // Special character and hexadecimal number
  Terminals TERM_SPECIAL_CHAR_NAMES = Terminals
          .operators("alarm", "backspace", "delete", "esc", "linefeed", "newline", "page", "return",
                  "space", "tab", "vtab");
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
          .map((a) -> (Tokens.fragment(a, Tag.S_SHARP_CHAR)));


  Parser<?> s_string = Scanners.DOUBLE_QUOTE_STRING
          .map((a) -> (Tokens.fragment(a, Tag.S_QUOTE_STRING)));

  Parser<?> PAR_LITERALS = Parsers.or(s_string, s_sharp_char, s_identifier);


  // Keyword
  Terminals TERM_KEYWORDS = Terminals
        .operators("and", "begin", "car", "cdr", "cond", "cons", "define", "define-record-type","define-syntax","do", "else", "if",
                "lambda", "let", "let*", "library", "list", "not", "or", "set!", "unless", "when");
  Parser<?> s_keywords = TERM_KEYWORDS.tokenizer().next(PAR_IDENTIFIER.not()).source()
        .map((a) -> (Tokens.fragment(a, Tag.S_KEYWORD)));


  // Bad char
  Parser<?> PAR_ELEMENT = Parsers.or(s_whitespace, PAR_COMMENT,
          s_operators, s_numbers, s_keywords, PAR_LITERALS);
//  Parser<?> s_bad_element = s_element.not().source()
//          .map((a) -> (Tokens.fragment(a, Tag.S_BAD_CHARACTER)));
  Pattern PT_ANY_CHAR = Patterns.isChar(CharPredicates.ALWAYS);
  Parser<String> PAR_ANY_CHAR = PT_ANY_CHAR.toScanner("any char").source();
  Parser<?> s_bad_element = PAR_ANY_CHAR
          .map((a) -> (Tokens.fragment(a, Tag.S_BAD_CHARACTER)));


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

      case S_LINE_COMMENT:
        type = SchemeTokens.LINE_COMMENT;
        break;

      case S_BLOCK_COMMENT:
        type = SchemeTokens.BLOCK_COMMENT;
        break;

      case S_DATUM_COMMENT_PREFIX:
        type = SchemeTokens.DATUM_COMMENT_PRE;
        break;

      case S_WHITE_SPACE:
        type = SchemeTokens.WHITESPACE;
        break;

      case S_OPERATOR:

        String opStr = token_frag.text();
        if (opStr.equals("(")) {
          type = SchemeTokens.LEFT_PAREN;

        } else if (opStr.equals(")")) {
          type = SchemeTokens.RIGHT_PAREN;

        } else if (opStr.equals("[")) {
          type = SchemeTokens.LEFT_SQUARE;

        } else if (opStr.equals("]")) {
          type = SchemeTokens.RIGHT_SQUARE;

        } else if (opStr.equals("'")) {
          type = SchemeTokens.QUOTE_MARK;

        } else if (opStr.equals("`")) {
          type = SchemeTokens.BACKQUOTE;

        } else if (opStr.equals(",")) {
          type = SchemeTokens.COMMA;

        } else if (opStr.equals("#")) {
          type = SchemeTokens.SHARP;

        } /*else if (opStr.equals(";")) {
          type = Tokens.OP_SEMI;

        } else if (opStr.equals("\\")) {
          type = Tokens.OP_BACKSLASH;

        }*/ else {
          type = SchemeTokens.SPECIAL;
        }

        break;

      case S_NUMBER:
        type = SchemeTokens.NUMBER_LITERAL;
        break;

      case S_QUOTE_STRING:
        type = SchemeTokens.STRING_LITERAL;
        break;

      case S_SHARP_CHAR:
        type = SchemeTokens.CHAR_LITERAL;
        break;

      case S_KEYWORD:
        type = SchemeTokens.KEYWORD;
        break;

      case S_IDENTIFIER:
        type = SchemeTokens.PLAIN_LITERAL;
        break;

      case S_BAD_CHARACTER:
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
