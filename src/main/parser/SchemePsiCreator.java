package main.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import main.psi.impl.*;
import main.psi.impl.list.SchemeList;


public class SchemePsiCreator
{
  public PsiElement createElement(ASTNode node)
  {
    IElementType elementType = node.getElementType();

    if (elementType == AST.AST_TEMP_LIST)
    {
      return new SchemeList(node);
    }
    else if (elementType == AST.AST_ELE_VECTOR)
    {
      return new SchemeVector(node);
    }
    else if (elementType == AST.AST_ELE_DATUM_COMMENT)
    {
      return new SchemeEleDatumComment(node);
    }
    else if (elementType == AST.AST_FORM_QUOTE)
    {
      return new SchemeQuoted(node);
    }
    else if (elementType == AST.AST_FORM_QUASIQUOTE)
    {
      return new SchemeQuoted(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_NUM)
    {
      return new SchemeEleNumber(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_CHAR)
    {
      return new SchemeEleChar(node);

    } else if (elementType == AST.AST_BASIC_ELE_STR)
    {
      return new SchemeEleString(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_BOOL)
    {
      return new SchemeEleBoolean(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_KEYWORD)
    {
      return new SchemeKeyword(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_PROCEDURE)
    {
      return new SchemeProcedure(node);
    }
    else if (elementType == AST.AST_BASIC_ELE_SYMBOL)
    {
      return new SchemeSymbol(node);
    }
    else if (elementType == AST.AST_BODY_OF_FORM)
    {
      return new SchemeBodyOfForm(node);
    }
    else if (elementType == AST.AST_FORM_AND)
    {
      return new SchemeFormAnd(node);
    }
    else if (elementType == AST.AST_FORM_BEGIN)
    {
      return new SchemeFormBegin(node);
    }
    else if (elementType == AST.AST_FORM_CAR)
    {
      return new SchemeFormCar(node);
    }
    else if (elementType == AST.AST_FORM_CDR)
    {
      return new SchemeFormCdr(node);
    }
    else if (elementType == AST.AST_FORM_COND)
    {
      return new SchemeFormCond(node);
    }
    else if (elementType == AST.AST_FORM_CONS)
    {
      return new SchemeFormCons(node);
    }
    // (define ...)
    else if (elementType == AST.AST_FORM_DEFINE)
    {
      return new SchemeFormDefine(node);
    }
    else if (elementType == AST.AST_FORM_DEFINE_RECORD_TYPE)
    {
      return new SchemeFormDefineRecordType(node);
    }
    else if (elementType == AST.AST_FORM_DEFINE_SYNTAX)
    {
      return new SchemeFormDefineSyntax(node);
    }
    else if (elementType == AST.AST_FORM_IF)
    {
      return new SchemeFormIf(node);
    }
    else if (elementType == AST.AST_FORM_LET)
    {
      return new SchemeFormLet(node);
    }
    else if (elementType == AST.AST_FORM_LIBRARY)
    {
      return new SchemeFormLibrary(node);
    }
    else if (elementType == AST.AST_FORM_LIST)
    {
      return new SchemeFormList(node);
    }
    else if (elementType == AST.AST_FORM_NOT)
    {
      return new SchemeFormNot(node);
    }
    else if (elementType == AST.AST_FORM_OR)
    {
      return new SchemeFormOr(node);
    }
    else if (elementType == AST.AST_FORM_PROCEDURE)
    {
      return new SchemeFormProcedure(node);
    }
    else if (elementType == AST.AST_FORM_SET)
    {
      return new SchemeFormSet(node);
    }
    else if (elementType == AST.AST_FORM_UNLESS)
    {
      return new SchemeFormUnless(node);
    }
    else if (elementType == AST.AST_FORM_WHEN)
    {
      return new SchemeFormWhen(node);
    }
    else if (elementType == AST.AST_BAD_CHARACTER)
    {
      return new SchemeBadCharacter(node);
    }
    else if (elementType == AST.AST_UNRECOGNIZED_FORM)
    {
      return new SchemeUnrecognizedForm(node);
    }
    else if (elementType == AST.AST_BAD_ELEMENT)
    {
      return new SchemeBadElement(node);
    }
    else if (elementType == AST.AST_FORM_CALL_PROCEDURE)
    {
      return new SchemeFormCallProcedure(node);
    }
    else
    {
      System.out.println(">>> Unexpected AST Node Type: " + elementType.toString());
      return new SchemeUnrecognizedForm(node);
    }

//    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }

  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SchemeFile(viewProvider);
  }
}
