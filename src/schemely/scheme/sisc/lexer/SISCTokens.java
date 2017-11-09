package schemely.scheme.sisc.lexer;

import com.intellij.psi.tree.IElementType;
import schemely.parser.SchemeElementType;

/**
 * @author Colin Fleming
 */
public interface SISCTokens
{
  IElementType PTR_DEF = new SchemeElementType("pointer def");
  IElementType PTR_REF = new SchemeElementType("pointer ref");
  IElementType SYNTAX_QUOTE = new SchemeElementType("syntax quote");
}
