package main.parser;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import main.psi.stubs.elements.SchemeStubFileElementType;

public interface AST
{
  IStubFileElementType AST_FILE = new SchemeStubFileElementType();


  // Intermediate type. Not the final AST Node
  IElementType AST_TEMP_LIST = new SchemeElementType("ast temp list");

  // Basic element
  IElementType AST_BASIC_ELE_BOOL = new SchemeElementType("ast bool");
  IElementType AST_BASIC_ELE_NUM = new SchemeElementType("ast number");
  IElementType AST_BASIC_ELE_CHAR = new SchemeElementType("ast character");
  IElementType AST_BASIC_ELE_STR = new SchemeElementType("ast string");
  IElementType AST_BASIC_ELE_KEYWORD = new SchemeElementType("ast keyword");
  IElementType AST_BASIC_ELE_SYMBOL = new SchemeElementType("ast symbol");

  // Other element
  IElementType AST_ELE_VECTOR = new SchemeElementType("ast vector");
  IElementType AST_ELE_DATUM_COMMENT = new SchemeElementType("ast datum comment");

  // Forms
  IElementType AST_FORM_DEFINE = new SchemeElementType("ast define");
  IElementType AST_FORM_DEFINE_RECORD_TYPE = new SchemeElementType("ast define-record-type");
  IElementType AST_FORM_DEFINE_SYNTAX = new SchemeElementType("ast define-syntax");
  IElementType AST_FORM_PROCEDURE = new SchemeElementType("ast procedure");   // lambda
  IElementType AST_FORM_CALL_PROCEDURE = new SchemeElementType("ast call procedure");
  IElementType AST_FORM_LET = new SchemeElementType("ast let");
  IElementType AST_FORM_SET = new SchemeElementType("ast set");
  IElementType AST_FORM_QUOTE = new SchemeElementType("ast quote");
  IElementType AST_FORM_BACKQUOTE = new SchemeElementType("ast backquote");

  // Data structure forms
  IElementType AST_FORM_CAR = new SchemeElementType("ast car");
  IElementType AST_FORM_CDR = new SchemeElementType("ast cdr");
  IElementType AST_FORM_CONS = new SchemeElementType("ast cons");
  IElementType AST_FORM_LIST = new SchemeElementType("ast list");

  // Library form
  IElementType AST_FORM_LIBRARY = new SchemeElementType("ast library");

  // Running process form
  IElementType AST_FORM_BEGIN = new SchemeElementType("ast begin");
  IElementType AST_FORM_IF = new SchemeElementType("ast if");
  IElementType AST_FORM_COND = new SchemeElementType("ast cond");
  IElementType AST_FORM_WHEN = new SchemeElementType("ast when");
  IElementType AST_FORM_UNLESS = new SchemeElementType("ast unless");
  IElementType AST_FORM_DO = new SchemeElementType("ast do");

  // Logic form
  IElementType AST_FORM_AND = new SchemeElementType("ast and");
  IElementType AST_FORM_OR = new SchemeElementType("ast or");
  IElementType AST_FORM_NOT = new SchemeElementType("ast not");

  // Macro form


  IElementType AST_BAD_CHARACTER = new SchemeElementType("ast bad character");
  IElementType AST_UNRECOGNIZED_FORM = new SchemeElementType("ast unrecognized form");
  IElementType AST_BAD_ELEMENT = new SchemeElementType("ast bad element");

  TokenSet AST_ELEMENTS = TokenSet.create(AST_TEMP_LIST,
          AST_BASIC_ELE_BOOL, AST_BASIC_ELE_NUM, AST_BASIC_ELE_CHAR, AST_BASIC_ELE_STR, AST_BASIC_ELE_KEYWORD, AST_BASIC_ELE_SYMBOL,
          AST_ELE_VECTOR,
          AST_FORM_DEFINE, AST_FORM_DEFINE_RECORD_TYPE, AST_FORM_DEFINE_SYNTAX,
          AST_FORM_PROCEDURE, AST_FORM_CALL_PROCEDURE, AST_FORM_LET, AST_FORM_SET, AST_FORM_QUOTE, AST_FORM_BACKQUOTE,
          AST_FORM_CAR, AST_FORM_CDR, AST_FORM_CONS, AST_FORM_LIST,
          AST_FORM_LIBRARY,
          AST_FORM_BEGIN, AST_FORM_IF, AST_FORM_COND, AST_FORM_WHEN, AST_FORM_UNLESS,
          AST_FORM_AND, AST_FORM_OR, AST_FORM_NOT,
          AST_BAD_CHARACTER, AST_UNRECOGNIZED_FORM, AST_BAD_ELEMENT);

  TokenSet LEAF_ELEMENTS = TokenSet.create(
          AST_BASIC_ELE_BOOL, AST_BASIC_ELE_NUM, AST_BASIC_ELE_CHAR,
          AST_BASIC_ELE_STR, AST_BASIC_ELE_KEYWORD, AST_BASIC_ELE_SYMBOL,
          AST_BAD_CHARACTER, AST_BAD_ELEMENT);

  TokenSet DEFINE_FORMS = TokenSet.create(
          AST_FORM_DEFINE, AST_FORM_DEFINE_RECORD_TYPE, AST_FORM_DEFINE_SYNTAX);
}
