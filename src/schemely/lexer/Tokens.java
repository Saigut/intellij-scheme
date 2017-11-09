package schemely.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.parser.SchemeElementType;

public interface Tokens
{
  // Special characters
  IElementType SHARP = new SchemeElementType("#");
  IElementType OPEN_VECTOR = new SchemeElementType("#(");
  IElementType LEFT_PAREN = new SchemeElementType("(");
  IElementType RIGHT_PAREN = new SchemeElementType(")");

  IElementType LEFT_CURLY = new SchemeElementType("{");
  IElementType RIGHT_CURLY = new SchemeElementType("}");

  IElementType LEFT_SQUARE = new SchemeElementType("[");
  IElementType RIGHT_SQUARE = new SchemeElementType("]");

  IElementType QUOTE_MARK = new SchemeElementType("'");
  IElementType BACKQUOTE = new SchemeElementType("`");
  IElementType COMMA = new SchemeElementType(",");
  IElementType COMMA_AT = new SchemeElementType(",@");

  // Comments
  IElementType COMMENT = new SchemeElementType("comment");
  IElementType BLOCK_COMMENT = new SchemeElementType("block comment");

  TokenSet COMMENTS = TokenSet.create(COMMENT, BLOCK_COMMENT);

  // Literals
  IElementType STRING_LITERAL = new SchemeElementType("string literal");
  IElementType NUMBER_LITERAL = new SchemeElementType("number literal");
  IElementType CHAR_LITERAL = new SchemeElementType("character literal");
  IElementType BOOLEAN_LITERAL = new SchemeElementType("boolean literal");
  IElementType PLAIN_LITERAL = new SchemeElementType("plain literal");

  TokenSet LITERALS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, CHAR_LITERAL, BOOLEAN_LITERAL, PLAIN_LITERAL);

  IElementType IDENTIFIER = new SchemeElementType("identifier");
  IElementType KEYWORD = new SchemeElementType("keyword");

  IElementType DOT = new SchemeElementType(".");

  IElementType SPECIAL = new SchemeElementType("special");

  // Control characters
  IElementType WHITESPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

  // Useful token sets
  TokenSet WHITESPACE_SET = TokenSet.create(WHITESPACE);
  TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER);
  TokenSet STRINGS = TokenSet.create(STRING_LITERAL);

  TokenSet PREFIXES = TokenSet.create(QUOTE_MARK, BACKQUOTE, COMMA, COMMA_AT);
  TokenSet BRACES = TokenSet.create(LEFT_PAREN, LEFT_CURLY, LEFT_SQUARE, RIGHT_PAREN, RIGHT_CURLY, RIGHT_SQUARE);
  TokenSet OPEN_BRACES = TokenSet.create(LEFT_PAREN, LEFT_CURLY, LEFT_SQUARE);
}
