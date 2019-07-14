package schemely.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import dk.brics.automaton.RunAutomaton;
import org.jparsec.*;
import org.jparsec.pattern.CharPredicates;
import org.jparsec.pattern.Pattern;
import org.jparsec.pattern.Patterns;


/**
 * @author Colin Fleming
 */
public class SchemeLexer extends LexerBase
{
  private static final RunAutomaton NUMBER = SchemeNumber.number();

  private CharSequence buffer;
  private CharSequence myBuffer;
  private int start;
  private int end;
  private int token_index;
  private int token_length;
  private org.jparsec.Tokens.Fragment token_frag;
  protected int cursor;
  private int bufferEnd;
  protected IElementType type;



  enum Tag {
    S_LINE_COMMENT,
    S_BLOCK_COMMENT,
    S_DATUM_COMMENT_PRE,
    S_WHITE_SPACE,
    S_OPERATOR,
    S_NUMBER,
    S_QUOTE_STRING,
    S_CHAR,
    S_LITERAL,
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
  Pattern PT_DATUM_COMMENT_PRE = Patterns.string("#;");
  Parser<?> P_DATUM_COMMENT_PRE = PT_DATUM_COMMENT_PRE.toScanner("datum comment prefix");
  Parser<?> s_datum_comment_pre = P_DATUM_COMMENT_PRE.source()
          .map((a) -> (org.jparsec.Tokens.fragment("#;", Tag.S_DATUM_COMMENT_PRE)));

  Parser<?> P_LINE_COMMENT = Patterns.lineComment(";").toScanner(";");
  Parser<?> s_line_comment = P_LINE_COMMENT.source()
          .map((a) -> (org.jparsec.Tokens.fragment(";", Tag.S_LINE_COMMENT)));
  Parser<?> s_block_commented =
          notChar2('|', '#').many().toScanner("commented block");
  Parser<?> s_block_comment = Parsers.sequence(Scanners.string("#|"), s_block_commented, Scanners.string("|#")).source()
          .map((a) -> (org.jparsec.Tokens.fragment("#||#", Tag.S_BLOCK_COMMENT)));
  Parser<?> s_comment = Parsers.or(s_datum_comment_pre, s_line_comment, s_block_comment);

  Parser<?> s_whitespace = Scanners.WHITESPACES
          .map((a) -> (org.jparsec.Tokens.fragment("WHITE_SPACE", Tag.S_WHITE_SPACE)));

  // Operators
  Pattern PT_OPS = Patterns.among("()[]'`,");
  Pattern PT_OP_SHARP = Patterns.isChar('#').next(Patterns.isChar('(').peek());
  Parser<String> PS_OPERATORS = Patterns.or(PT_OPS, PT_OP_SHARP).toScanner("operator").source();

  Parser<?> s_operators = PS_OPERATORS
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_OPERATOR)));

  // Numbers
  Pattern PT_RIGHT_INTEGER = Patterns.sequence(Patterns.among("+-").optional(), Patterns.INTEGER);
  Parser<String> PS_RIGHT_INTEGER = PT_RIGHT_INTEGER.toScanner("integer").source();
  Parser<?> s_numbers = Parsers.or(
          PS_RIGHT_INTEGER,
          Scanners.DECIMAL,
          Scanners.DEC_INTEGER, Scanners.OCT_INTEGER,
          Scanners.HEX_INTEGER,
          Scanners.SCIENTIFIC_NOTATION)
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_NUMBER)));

  // Literals
  Pattern PT_LITERAL_VALID = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  Patterns.isChar(CharPredicates.IS_DIGIT),
                  Patterns.among("!@$%^&*-+_=:|/?<>."));
  Pattern PT_LITERAL = PT_LITERAL_VALID.many1();
  Parser<String> LITERAL = PT_LITERAL.toScanner("literal").source();

  Parser<?> s_literal = LITERAL
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_LITERAL)));


  Terminals CHAR_NAMES = Terminals
          .operators("alarm", "backspace", "delete", "esc", "linefeed", "newline", "page", "return",
                  "space", "tab", "vtab");
  Pattern PT_SINGLE_CHAR = Patterns.ANY_CHAR.next(PT_LITERAL.not());
  Pattern PT_HEX = Patterns.sequence(Patterns.among("xX"), Patterns.among("0123456789abcdefABCDEF").many1())
          .next(PT_LITERAL.not());
  Pattern PT_CHAR_PRE = Patterns.string("#\\");

  Parser<?> s_char_pre = PT_CHAR_PRE.toScanner("char prefix");
  Parser<?> s_char_names = CHAR_NAMES.tokenizer().next(LITERAL.not());

  Parser<?> s_char_char = Patterns.sequence(PT_CHAR_PRE, PT_SINGLE_CHAR).toScanner("char char");
  Parser<?> s_char_hex = Patterns.sequence(PT_CHAR_PRE, PT_HEX).toScanner("char hex");
  Parser<?> s_char_name = Parsers.sequence(s_char_pre, s_char_names);
  Parser<?> s_char = Parsers.or(s_char_char, s_char_name, s_char_hex).source()
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_CHAR)));


  Parser<?> s_string = Scanners.DOUBLE_QUOTE_STRING
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_QUOTE_STRING)));

  Parser<?> s_literals = Parsers.or(s_string, s_char, s_literal);


  // Keyword
  Terminals KEYWORD_TERM = Terminals
        .operators("and", "begin", "car", "cdr", "cond", "cons", "define", "define-syntax","do", "else", "if",
                "lambda", "let", "let*", "library", "list", "not", "or", "set!", "unless", "when");
  Parser<?> s_keywords = KEYWORD_TERM.tokenizer().next(LITERAL.not()).source()
        .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_KEYWORD)));


  // Bad char
  Parser<?> s_element = Parsers.or(s_whitespace, s_comment,
          s_operators, s_numbers, s_keywords, s_literals);
//  Parser<?> s_bad_element = s_element.not().source()
//          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_BAD_CHARACTER)));
  Pattern PT_ANY_CHAR = Patterns.isChar(CharPredicates.ALWAYS);
  Parser<String> PS_ANY_CHAR = PT_ANY_CHAR.toScanner("any char").source();
  Parser<?> s_bad_element = PS_ANY_CHAR
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_BAD_CHARACTER)));


  Parser<Object> token = Parsers.or(s_element, s_bad_element);

  Parser<Token> a_token = token
          .map((a) -> {
            if (null != a) {
//              System.out.println("a: " + a.toString());
              if (a.getClass() == org.jparsec.Tokens.Fragment.class) {
                token_frag = (org.jparsec.Tokens.Fragment)a;
//                System.out.println("type: " + ((org.jparsec.Tokens.Fragment)a).tag().toString());
//                System.out.println("text: " + ((org.jparsec.Tokens.Fragment)a).text());
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
      a_token.parse(myBuffer);

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
        type = Tokens.LINE_COMMENT;
        break;

      case S_BLOCK_COMMENT:
        type = Tokens.BLOCK_COMMENT;
        break;

      case S_DATUM_COMMENT_PRE:
        type = Tokens.DATUM_COMMENT_PRE;
        break;

      case S_WHITE_SPACE:
        type = Tokens.WHITESPACE;
        break;

      case S_OPERATOR:

        String opStr = token_frag.text();
        if (opStr.equals("(")) {
          type = Tokens.LEFT_PAREN;

        } else if (opStr.equals(")")) {
          type = Tokens.RIGHT_PAREN;

        } else if (opStr.equals("[")) {
          type = Tokens.LEFT_SQUARE;

        } else if (opStr.equals("]")) {
          type = Tokens.RIGHT_SQUARE;

        } else if (opStr.equals("'")) {
          type = Tokens.QUOTE_MARK;

        } else if (opStr.equals("`")) {
          type = Tokens.BACKQUOTE;

        } else if (opStr.equals(",")) {
          type = Tokens.COMMA;

        } else if (opStr.equals("#")) {
          type = Tokens.SHARP;

        } /*else if (opStr.equals(";")) {
          type = Tokens.OP_SEMI;

        } else if (opStr.equals("\\")) {
          type = Tokens.OP_BACKSLASH;

        }*/ else {
          type = Tokens.SPECIAL;
        }

        break;

      case S_NUMBER:
        type = Tokens.NUMBER_LITERAL;
        break;

      case S_QUOTE_STRING:
        type = Tokens.STRING_LITERAL;
        break;

      case S_CHAR:
        type = Tokens.CHAR_LITERAL;
        break;

      case S_KEYWORD:
        type = Tokens.KEYWORD;
        break;

      case S_LITERAL:
        type = Tokens.PLAIN_LITERAL;
        break;

      case S_BAD_CHARACTER:
        type = Tokens.BAD_CHARACTER;
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

  protected boolean implementationSpecific()
  {
    return false;
  }

  protected boolean test(char ch, IElementType type)
  {
    if (peek() == ch)
    {
      this.type = type;
      cursor++;
      return true;
    }
    return false;
  }

  protected boolean test(String str, IElementType type)
  {
    if (lookingAt(str))
    {
      this.type = type;
      cursor += str.length();
      return true;
    }
    return false;
  }

  private void readNumber()
  {
    int length = NUMBER.run(buffer.toString(), cursor);
    if (length >= 0)
    {
      cursor += length;
      this.type = Tokens.NUMBER_LITERAL;
    }
    else
    {
      bad();
    }
  }

  protected void readIdentifier()
  {
    cursor++;
    while (isIdentifierSubsequent(peek()))
    {
      cursor++;
    }
    this.type = Tokens.IDENTIFIER;
  }

  protected boolean isIdentifierInitial(char ch)
  {
    return Character.isLetter(ch) || in(ch, "!$%&*/:<=>?~_^@");
  }

  protected boolean isIdentifierSubsequent(char ch)
  {
    return isIdentifierInitial(ch) || Character.isDigit(ch) || in(ch, ".+-");
  }

  private void readSingleChar(IElementType type)
  {
    this.type = type;
    cursor++;
  }

  private void readWhitespace()
  {
    type = Tokens.WHITESPACE;
    while (Character.isWhitespace(peek()))
    {
      cursor++;
    }
  }

  private void readSingleLineComment()
  {
    char next;
    cursor++;
    next = peek();
    while (more() && (next != '\r') && (next != '\n'))
    {
      cursor++;
      next = peek();
    }
    type = Tokens.LINE_COMMENT;
  }

  private void readMultiLineComment()
  {
    cursor += 2;
    int depth = 1;

    while (has(2) && (depth > 0))
    {
      if (lookingAt("#|"))
      {
        depth++;
        cursor += 2;
      }
      else if (lookingAt("|#"))
      {
        depth--;
        cursor += 2;
      }
      else
      {
        cursor++;
      }
    }
    type = Tokens.BLOCK_COMMENT;
  }

  private void readString()
  {
    cursor++;
    while (more() && peek() != '"')
    {
      if ((peek() == '\\') && has(2))
      {
        cursor += 2;
      }
      else
      {
        cursor++;
      }
    }
    if (peek() == '"')
    {
      cursor++;
    }
    type = Tokens.STRING_LITERAL;
  }

  protected boolean supportsLongComments()
  {
    return true;
  }

  protected boolean lookingAt(String str)
  {
    return has(str.length()) && buffer.subSequence(cursor, cursor + str.length()).toString().equalsIgnoreCase(str);
  }

  private boolean in(char ch, String options)
  {
    return options.indexOf(ch) >= 0;
  }

  protected char peek()
  {
    if (more())
    {
      return buffer.charAt(cursor);
    }
    return 0;
  }

  protected char peek(int offset)
  {
    if (has(offset + 1))
    {
      return buffer.charAt(cursor + offset);
    }
    return 0;
  }

  private boolean more()
  {
    return cursor < bufferEnd;
  }

  protected boolean has(int n)
  {
    return (cursor + n) <= bufferEnd;
  }

  protected void bad()
  {
    cursor++;
    this.type = Tokens.BAD_CHARACTER;
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
