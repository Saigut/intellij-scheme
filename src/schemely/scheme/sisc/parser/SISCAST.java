package schemely.scheme.sisc.parser;

import com.intellij.psi.tree.IElementType;
import schemely.parser.SchemeElementType;

/**
 * @author Colin Fleming
 */
public interface SISCAST
{
  IElementType PTR_DEF = new SchemeElementType("pointer def");
  IElementType PTR_REF = new SchemeElementType("pointer ref");
}
