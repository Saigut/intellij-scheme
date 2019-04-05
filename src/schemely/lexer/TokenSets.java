package schemely.lexer;

import com.intellij.psi.tree.TokenSet;
import schemely.parser.AST;


public class TokenSets implements Tokens
{
  // TODO CMF
  public static final TokenSet REFERENCE_NAMES = TokenSet.create(AST.AST_BASIC_ELE_SYMBOL);
}
