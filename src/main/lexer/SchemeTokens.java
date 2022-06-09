package main.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import main.parser.SchemeElementType;

public interface SchemeTokens
{
  // Special characters
  IElementType SHARP_MARK = new SchemeElementType("#");
  IElementType OPEN_VECTOR = new SchemeElementType("#(");
  IElementType LEFT_PAREN = new SchemeElementType("(");
  IElementType RIGHT_PAREN = new SchemeElementType(")");

  IElementType LEFT_CURLY = new SchemeElementType("{");
  IElementType RIGHT_CURLY = new SchemeElementType("}");

  IElementType LEFT_SQUARE = new SchemeElementType("[");
  IElementType RIGHT_SQUARE = new SchemeElementType("]");

  IElementType QUOTE = new SchemeElementType("'");
  IElementType QUASIQUOTE = new SchemeElementType("`");
  IElementType UNQUOTE = new SchemeElementType(",");
  IElementType UNQUOTE_SPLICING = new SchemeElementType(",@");
  IElementType SYNTAX = new SchemeElementType("#'");
  IElementType QUASISYNTAX = new SchemeElementType("#`");
  IElementType UNSYNTAX = new SchemeElementType("#,");
  IElementType UNSYNTAX_SPLICING = new SchemeElementType("#,@");

  // Comments
  IElementType LINE_COMMENT = new SchemeElementType("line comment");
  IElementType BLOCK_COMMENT = new SchemeElementType("block comment");
  IElementType DATUM_COMMENT_PRE = new SchemeElementType("datum comment prefix");
  IElementType DATUM_COMMENT = new SchemeElementType("datum comment");

  TokenSet COMMENTS = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, DATUM_COMMENT);

  // Literals
  IElementType STRING_LITERAL = new SchemeElementType("string literal");
  IElementType STRING_QUOTE_CHAR = new SchemeElementType("string quote char");
  IElementType STRING_CHAR = new SchemeElementType("string char");
  IElementType STRING_ESCAPE = new SchemeElementType("string escape");
  IElementType NUMBER_LITERAL = new SchemeElementType("number literal");
  IElementType CHAR_LITERAL = new SchemeElementType("character literal");
  IElementType BOOLEAN_LITERAL = new SchemeElementType("boolean literal");
  IElementType NAME_LITERAL = new SchemeElementType("name literal");

  TokenSet LITERALS = TokenSet.create(NAME_LITERAL);

  IElementType IDENTIFIER = new SchemeElementType("identifier");
  IElementType KEYWORD = new SchemeElementType("keyword");
  IElementType PROCEDURE = new SchemeElementType("procedure");

  IElementType DOT = new SchemeElementType(".");

  IElementType SPECIAL = new SchemeElementType("special");

  // Control characters
  IElementType WHITESPACE = TokenType.WHITE_SPACE;
  IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

  // Useful token sets
  TokenSet WHITESPACE_SET = TokenSet.create(WHITESPACE);
  TokenSet IDENTIFIERS = TokenSet.create(IDENTIFIER, NAME_LITERAL, PROCEDURE);
  TokenSet STRINGS = TokenSet.create(STRING_LITERAL, NUMBER_LITERAL, CHAR_LITERAL, BOOLEAN_LITERAL);

  TokenSet DATUM_PREFIXES = TokenSet.create(QUOTE, QUASIQUOTE, UNQUOTE, UNQUOTE_SPLICING,
          SYNTAX, QUASISYNTAX, UNSYNTAX, UNSYNTAX_SPLICING);
  TokenSet BRACES = TokenSet.create(LEFT_PAREN, LEFT_CURLY, LEFT_SQUARE, RIGHT_PAREN, RIGHT_CURLY, RIGHT_SQUARE);
  TokenSet OPEN_BRACES = TokenSet.create(LEFT_PAREN, LEFT_CURLY, LEFT_SQUARE);
}
