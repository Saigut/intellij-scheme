package schemely.scheme.sisc.parser;

import com.intellij.psi.tree.IElementType;
import schemely.parser.SchemeElementType;

/**
 * @author Colin Fleming
 */
public interface SISCAST
{
  final IElementType PTR_DEF = new SchemeElementType("pointer def");
  final IElementType PTR_REF = new SchemeElementType("pointer ref");
}
