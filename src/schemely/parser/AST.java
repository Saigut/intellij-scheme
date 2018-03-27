package schemely.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.lexer.Tokens;
import schemely.psi.stubs.elements.SchemeStubFileElementType;

public interface AST extends Tokens
{
  IStubFileElementType FILE = new SchemeStubFileElementType();

  IElementType LIST = new SchemeElementType("list");
  IElementType VECTOR = new SchemeElementType("vector");

  IElementType LITERAL = new SchemeElementType("literal");
  IElementType IDENTIFIER = new SchemeElementType("identifier");
  IElementType KEYWORD = new SchemeElementType("keyword");
  IElementType SPECIAL = new SchemeElementType("special");

  IElementType QUOTED = new SchemeElementType("quoted");
  IElementType BACKQUOTED = new SchemeElementType("backquoted");

  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);
}
