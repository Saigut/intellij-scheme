package schemely.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import schemely.lexer.Tokens;
import schemely.psi.stubs.elements.SchemeStubFileElementType;

public interface AST extends Tokens
{
  final IStubFileElementType AST_FILE = new SchemeStubFileElementType();

  final IElementType AST_LIST = new SchemeElementType("ast list");
  final IElementType AST_VECTOR = new SchemeElementType("ast vector");

  final IElementType AST_PLAIN_LITERAL = new SchemeElementType("ast plain literal");
  final IElementType AST_OTHER_LITERAL = new SchemeElementType("ast other literal");
  final IElementType AST_IDENTIFIER = new SchemeElementType("ast identifier");
  final IElementType AST_KEYWORD = new SchemeElementType("ast keyword");
  final IElementType AST_SPECIAL = new SchemeElementType("ast special");

  final IElementType AST_QUOTED = new SchemeElementType("ast quoted");
  final IElementType AST_BACKQUOTED = new SchemeElementType("ast backquoted");

  TokenSet AST_LIST_LIKE_FORMS = TokenSet.create(AST_LIST, AST_VECTOR);

  TokenSet AST_ELEMENTS = TokenSet.create(AST_LIST, AST_VECTOR,
          AST_PLAIN_LITERAL, AST_OTHER_LITERAL, AST_IDENTIFIER, AST_KEYWORD,
          AST_SPECIAL, AST_QUOTED, AST_BACKQUOTED);

  TokenSet AST_BRACES = TokenSet.create(LEFT_CURLY, LEFT_PAREN, LEFT_SQUARE, RIGHT_CURLY, RIGHT_PAREN, RIGHT_SQUARE);
}
