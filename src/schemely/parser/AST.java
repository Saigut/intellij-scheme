package schemely.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.lexer.Tokens;
import schemely.psi.stubs.elements.SchemeStubFileElementType;

public interface AST extends Tokens
{
  final IStubFileElementType FILE = new SchemeStubFileElementType();

  final IElementType LIST = new SchemeElementType("list");
  final IElementType VECTOR = new SchemeElementType("vector");

  final IElementType LITERAL = new SchemeElementType("literal");
  final IElementType IDENTIFIER = new SchemeElementType("identifier");
  final IElementType KEYWORD = new SchemeElementType("keyword");
  final IElementType SPECIAL = new SchemeElementType("special");

  final IElementType QUOTED = new SchemeElementType("quoted");
  final IElementType BACKQUOTED = new SchemeElementType("backquoted");

  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);
}
