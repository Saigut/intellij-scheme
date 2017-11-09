package schemely.lexer;

import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;
import dk.brics.automaton.RunAutomaton;
import org.jparsec.*;
import org.jparsec.pattern.CharPredicates;
import org.jparsec.pattern.Pattern;
import org.jparsec.pattern.Patterns;

import static org.jparsec.Scanners.lineComment;

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
    S_COMMENT,
    S_WHITE_SPACE,
    S_OPERATOR,
    S_NUMBER,
    S_QUOTE_STRING,
    S_QUOTE_CHAR,
    S_LITERAL,
    S_BAD_ELEMENT,

    S_KEYWORD
  }

  /**
   * Elements
   */

  // Blanks and Conments
  Parser<?> s_line_comment = lineComment(";").source()
          .map((a) -> (org.jparsec.Tokens.fragment(";", Tag.S_COMMENT)));
  Parser<?> s_whitespace = Scanners.WHITESPACES
          .map((a) -> (org.jparsec.Tokens.fragment("WHITE_SPACE", Tag.S_WHITE_SPACE)));

  // Operators
  Pattern PT_OPERATORS = Patterns.among("()[]'`,#\\");
  Parser<String> PS_OPERATORS = PT_OPERATORS.toScanner("operator").source();

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
  Pattern literal_valid = Patterns
          .or(Patterns.isChar(CharPredicates.IS_LETTER),
                  Patterns.isChar(CharPredicates.IS_DIGIT),
                  Patterns.among("!@$%^&*-+_=:|/?<>."));
  Pattern P_LITERAL = literal_valid.many1();
  Parser<String> LITERAL = P_LITERAL.toScanner("literal").source();

  Parser<?> s_string = Scanners.DOUBLE_QUOTE_STRING
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_QUOTE_STRING)));
  Parser<?> s_char = Scanners.SINGLE_QUOTE_CHAR
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_QUOTE_CHAR)));
  Parser<?> s_literal = LITERAL
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_LITERAL)));

  Parser<?> s_literals = Parsers.or(s_string, s_char, s_literal);


  // Keyword
  Terminals KEYWORD_TERM = Terminals
        .operators("define", "cond", "else", "if", "lambda", "car", "cdr", "cons",
                "let", "let*", "set!", "do");
  Parser<?> s_keywords = KEYWORD_TERM.tokenizer().next(LITERAL.not()).source()
        .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_KEYWORD)));



  // Bad char
  Parser<?> s_element = Parsers.or(s_whitespace, s_line_comment,
          s_operators, s_numbers, s_keywords, s_literals);
//  Parser<?> s_bad_element = s_element.not().source()
//          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_BAD_ELEMENT)));
  Pattern PT_ANY_CHAR = Patterns.isChar(CharPredicates.ALWAYS);
  Parser<String> PS_ANY_CHAR = PT_ANY_CHAR.toScanner("any char").source();
  Parser<?> s_bad_element = PS_ANY_CHAR
          .map((a) -> (org.jparsec.Tokens.fragment(a, Tag.S_BAD_ELEMENT)));


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

      case S_COMMENT:
        type = Tokens.COMMENT;
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

      case S_QUOTE_CHAR:
        type = Tokens.CHAR_LITERAL;
        break;

      case S_KEYWORD:
        type = Tokens.KEYWORD;
        break;

      case S_LITERAL:
        type = Tokens.PLAIN_LITERAL;
        break;

      case S_BAD_ELEMENT:
        type = Tokens.PLAIN_LITERAL;
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
    type = Tokens.COMMENT;
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
