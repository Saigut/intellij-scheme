package main.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import main.psi.impl.*;
import main.psi.impl.list.SchemeList;
import main.psi.util.SchemePsiUtil;


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
    }
    else if (elementType == AST.AST_BASIC_ELE_STR)
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
    else if (elementType == AST.AST_BASIC_ELE_SYMBOL_DEFINE)
    {
      return new SchemeSymbolDefine(node);
    }
    else if (elementType == AST.AST_BODY_IN_FORM_BODY)
    {
      return new SchemeInFormBody(node);
    }
    else if (elementType == AST.AST_BODY_IN_FORM_PARAM_LIST)
    {
      return new SchemeInFormParamList(node);
    }
    else if (elementType == AST.AST_BODY_IN_FORM_PARAM_LIST_LET_INNER)
    {
      return new SchemeInFormParamListLetInner(node);
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
    else if (elementType == AST.AST_FORM_DEFINE)
    {
      SchemeFormDefine form = new SchemeFormDefine(node);
      setupDefineForm(form);
      return form;
    }
    else if (elementType == AST.AST_FORM_DEFINE_RECORD_TYPE)
    {
      SchemeFormDefineRecordType form = new SchemeFormDefineRecordType(node);
      setupDefineRecordType(form);
      return form;
    }
    else if (elementType == AST.AST_FORM_DEFINE_SYNTAX)
    {
      SchemeFormDefineSyntax form = new SchemeFormDefineSyntax(node);
      setupDefineSyntax(form);
      return form;
    }
    else if (elementType == AST.AST_FORM_DO)
    {
      return new SchemeFormDo(node);
    }
    else if (elementType == AST.AST_FORM_IF)
    {
      return new SchemeFormIf(node);
    }
    else if (elementType == AST.AST_FORM_LET)
    {
      SchemeFormLet form = new SchemeFormLet(node);
//      setupLetForm(form);
      return form;
    }
    else if (elementType == AST.AST_FORM_LET_A)
    {
      return new SchemeFormLetA(node);
    }
    else if (elementType == AST.AST_FORM_LETREC)
    {
      return new SchemeFormLetrec(node);
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
    else if (elementType == AST.AST_FORM_QUASIQUOTE)
    {
      return new SchemeFormQuasiquote(node);
    }
    else if (elementType == AST.AST_FORM_QUASISYNTAX)
    {
      return new SchemeFormQuasisyntax(node);
    }
    else if (elementType == AST.AST_FORM_QUOTE)
    {
      return new SchemeFormQuote(node);
    }
    else if (elementType == AST.AST_FORM_PROCEDURE)
    {
      SchemeFormProcedure form = new SchemeFormProcedure(node);
//      setupProcedure(form);
      return form;
    }
    else if (elementType == AST.AST_FORM_SET)
    {
      return new SchemeFormSet(node);
    }
    else if (elementType == AST.AST_FORM_SYNTAX)
    {
      return new SchemeFormSyntax(node);
    }
    else if (elementType == AST.AST_FORM_UNLESS)
    {
      return new SchemeFormUnless(node);
    }
    else if (elementType == AST.AST_FORM_UNQUOTE)
    {
      return new SchemeFormUnquote(node);
    }
    else if (elementType == AST.AST_FORM_UNQUOTE_SPLICING)
    {
      return new SchemeFormUnquoteSplicing(node);
    }
    else if (elementType == AST.AST_FORM_UNSYNTAX)
    {
      return new SchemeFormUnsyntax(node);
    }
    else if (elementType == AST.AST_FORM_UNSYNTAX_SPLICING)
    {
      return new SchemeFormUnsyntaxSplicing(node);
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
      System.out.println(">>> Unexpected AST Node Type: " + elementType);
      return new SchemeUnrecognizedForm(node);
    }

//    throw new Error("Unexpected ASTNode: " + node.getElementType());
  }

  private boolean setupLetForm(SchemeFormLetBase form)
  {
    ASTNode node = form.getNode();
    form.clareLocalDefinitions();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return false;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL) {
      form.addLocalDefinition(defNode.getPsi());
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
      if (defNode == null) {
        return false;
      }
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType != AST.AST_TEMP_LIST && defNodeType != AST.AST_UNRECOGNIZED_FORM) {
      return false;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return true;
    }
    while (defNode != null) {
      ASTNode localDefinition = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
      if (localDefinition != null && localDefinition.getElementType() == AST.AST_BASIC_ELE_SYMBOL) {
        form.addLocalDefinition(localDefinition.getPsi());
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
    }
//    PsiElement[] defs = form.getLocalDefinitions();
//    for (PsiElement def : defs) {
//      System.out.println("let localDefinition: " + def.getText());
//    }
    return true;
  }

  private boolean setupDefineForm(SchemeFormDefine form) {
    ASTNode node = form.getNode();
    form.clareLocalDefinitions();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return false;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      form.setDeclareName(defNode.getPsi());
      return true;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return false;
    }
    if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      form.setDeclareName(defNode.getPsi());
      return true;
//      ASTNode localDefinition;
//      localDefinition = defNode.getTreeNext();
//      while (localDefinition != null) {
//        if (localDefinition.getElementType() == AST.AST_BASIC_ELE_SYMBOL) {
//          form.addLocalDefinition(localDefinition.getPsi());
//        }
//        localDefinition = localDefinition.getTreeNext();
//      }
//      return true;
    }
    return false;
  }

  private boolean setupDefineSyntax(SchemeFormDefineSyntax form) {
    ASTNode node = form.getNode();
    ASTNode declareName = getDeclareName(node);
    if (declareName == null) {
      return false;
    } else {
      form.setDeclareName(declareName.getPsi());
      return true;
    }
  }

  private boolean setupDefineRecordType(SchemeFormDefineRecordType form) {
    ASTNode node = form.getNode();
    ASTNode declareName = getDeclareName(node);
    if (declareName == null) {
      return false;
    } else {
      form.setDeclareName(declareName.getPsi());
      return true;
    }
  }

  private boolean setupProcedure(SchemeFormProcedure form) {
    ASTNode node = form.getNode();
    form.clareLocalDefinitions();
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return false;
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType == AST.AST_BASIC_ELE_SYMBOL) {
      form.addLocalDefinition(defNode.getPsi());
      return true;
    }
    if (defNodeType != AST.AST_TEMP_LIST && defNodeType != AST.AST_UNRECOGNIZED_FORM) {
      return false;
    }
    defNode = SchemePsiUtil.getNonLeafChildAt(defNode, 0);
    if (defNode == null) {
      return true;
    }
    while (defNode != null) {
      if (defNode.getElementType() == AST.AST_BASIC_ELE_SYMBOL) {
        form.addLocalDefinition(defNode.getPsi());
      }
      defNode = SchemePsiUtil.getNodeNextNonLeafSibling(defNode);
    }
    return true;
  }

  private ASTNode getDeclareName(ASTNode node)
  {
    ASTNode defNode = SchemePsiUtil.getNonLeafChildAt(node, 1);
    if (defNode == null) {
      return null;
    }
    IElementType defNodeType = defNode.getElementType();
    if (defNodeType == AST.AST_BASIC_ELE_SYMBOL_DEFINE) {
      return defNode;
    } else {
      return null;
    }
  }

  public PsiFile createFile(FileViewProvider viewProvider)
  {
    return new SchemeFile(viewProvider);
  }
}
