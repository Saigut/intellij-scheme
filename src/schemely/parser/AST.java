package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.lexer.Tokens;
import schemely.psi.stubs.elements.SchemeStubFileElementType;

public interface AST extends Tokens
{
  final IStubFileElementType FILE = new SchemeStubFileElementType();

  final IElementType LIST = new SchemeElementType("ast list");
  final IElementType VECTOR = new SchemeElementType("ast vector");

  final IElementType PLAIN_LITERAL = new SchemeElementType("ast plain literal");
  final IElementType OTHER_LITERAL = new SchemeElementType("ast other literal");
  final IElementType IDENTIFIER = new SchemeElementType("ast identifier");
  final IElementType KEYWORD = new SchemeElementType("ast keyword");
  final IElementType SPECIAL = new SchemeElementType("ast special");

  final IElementType QUOTED = new SchemeElementType("ast quoted");
  final IElementType BACKQUOTED = new SchemeElementType("ast backquoted");

  TokenSet LIST_LIKE_FORMS = TokenSet.create(LIST, VECTOR);

  TokenSet BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);
}
