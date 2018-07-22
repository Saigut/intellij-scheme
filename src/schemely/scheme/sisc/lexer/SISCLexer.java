package schemely.scheme.sisc.lexer;

import schemely.lexer.SchemeLexer;
import schemely.lexer.Tokens;

/**
 * @author Colin Fleming
 */
public class SISCLexer extends SchemeLexer
{
  @Override
  protected boolean implementationSpecific()
  {
    if (peek() == '#')
    {
      if (peek(1) == '%')
      {
        // TODO - what do these mean?
        cursor += 2;
        if (isIdentifierInitial(peek()))
        {
          cursor++;
          while (isIdentifierSubsequent(peek()))
          {
            cursor++;
          }
          type = Tokens.IDENTIFIER;
        }
        else
        {
          bad();
        }
        return true;
      }
      else if (Character.isDigit(peek(1)))
      {
        cursor += 2;
        while (Character.isDigit(peek()))
        {
          cursor++;
        }
        if (peek() == '=')
        {
          cursor++;
          type = SISCTokens.PTR_DEF;
        }
        else if (peek() == '#')
        {
          cursor++;
          type = SISCTokens.PTR_REF;
        }
        else if (peek() == '(')
        {
          cursor++;
          type = Tokens.OPEN_VECTOR;
        }
        else
        {
          bad();
        }
        return true;
      }
      else if (peek(1) == '\'')
      {
        cursor += 2;
        type = SISCTokens.SYNTAX_QUOTE;
        return true;
      }
    }
    else if ((peek() == '.') && isIdentifierInitial(peek(1)))
    {
      // TODO - find out what the story is here - does SISC allow any symbol to start with '.'?
      cursor++;
      readIdentifier();
      return true;
    }
    else if (peek() == '|')
    {
      cursor++;
      if (isIdentifierInitial(peek()))
      {
        cursor++;
        while (isIdentifierSubsequent(peek()))
        {
          cursor++;
        }
        if (peek() == '|')
        {
          cursor++;
          type = Tokens.IDENTIFIER;
        }
        else
        {
          bad();
        }
      }
      else
      {
        bad();
      }
      return true;
    }
    else if (has(3) && lookingAt("->") && isIdentifierInitial(peek(2)))
    {
      cursor += 2;
      readIdentifier();
      return true;
    }

    return test("#!eof", Tokens.SPECIAL) ||
           test("#!+inf", Tokens.SPECIAL) ||
           test("#!-inf", Tokens.SPECIAL) ||
           test("#!nan", Tokens.SPECIAL) ||
           test("#!void", Tokens.SPECIAL) ||
           test("#\\backspace", Tokens.CHAR_LITERAL) ||
           test("#\\nul", Tokens.CHAR_LITERAL) ||
           test("#\\page", Tokens.CHAR_LITERAL) ||
           test("#\\return", Tokens.CHAR_LITERAL) ||
           test("#\\rubout", Tokens.CHAR_LITERAL) ||
           test("#\\tab", Tokens.CHAR_LITERAL);

    // TODO unicode char literals
    // TODO boxes
  }
}
