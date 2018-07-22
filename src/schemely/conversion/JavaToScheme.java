package schemely.conversion;


import com.intellij.lang.StdLanguages;
import com.intellij.psi.*;

import java.util.*;

class JavaToScheme
{
  private final Map<String, String> imports = new HashMap<String, String>();

  String escapeKeyword(String name)
  {
    //    if (ScalaNamesUtil.isKeyword(name)) return "`" + name + "`"; else return name;
    return name;
  }

  String convertPsiToText(PsiElement element)
  {
    if (element == null)
    {
      return "";
    }
    if (element.getLanguage() != StdLanguages.JAVA)
    {
      return "";
    }
    StringBuilder res = new StringBuilder("");
    if (element instanceof PsiDocCommentOwner)
    {
      PsiDocCommentOwner owner = (PsiDocCommentOwner) element;
      if (owner.getDocComment() != null)
      {
        res.append(owner.getDocComment().getText()).append("\n");
      }
    }

    if (element instanceof PsiFile)
    {
      PsiFile f = (PsiFile) element;
      for (PsiElement child : f.getChildren())
      {
        res.append(convertPsiToText(child)).append("\n");
      }
    }
    else if (element instanceof PsiWhiteSpace)
    {
      res.append(element.getText());
    }
    else if (element instanceof PsiImportStatement)
    {
      PsiImportStatement i = (PsiImportStatement) element;
      PsiJavaCodeReferenceElement importReference = i.getImportReference();
      String referenceName = importReference.getReferenceName();
      String importValue = convertPsiToText(importReference);

      res.append("(define-alias ");
      res.append(escapeKeyword(referenceName));
      res.append(" ");
      res.append(importValue);
      //      TODO
      //      if (i.isOnDemand())
      //      {
      //        res.append("._");
      //      }
      res.append(")");

      imports.put(importValue, referenceName);
    }
    else if (element instanceof PsiImportList)
    {
      PsiImportList i = (PsiImportList) element;
      for (PsiImportStatementBase imp : i.getAllImportStatements())
      {
        res.append(convertPsiToText(imp)).append("\n");
      }
    }
    else if (element instanceof PsiClass)
    {
      PsiClass c = (PsiClass) element;

      if (c instanceof PsiAnonymousClass)
      {
        // TODO handle anonymous classes
        res.append("; Anonymous classes not supported\n");
      }
      else
      {
        res.append("(define-simple-class ");
        res.append(escapeKeyword(c.getName()));
        res.append(" (");
        String separator = "";
        if (c.getExtendsList() != null)
        {
          for (PsiJavaCodeReferenceElement referenceElement : c.getExtendsList().getReferenceElements())
          {
            res.append(separator);
            separator = " ";
            res.append(convertPsiToText(referenceElement));
          }
        }
        if (c.getImplementsList() != null)
        {
          for (PsiJavaCodeReferenceElement referenceElement : c.getImplementsList().getReferenceElements())
          {
            res.append(separator);
            separator = " ";
            res.append(convertPsiToText(referenceElement));
          }
        }
        res.append(")");
        if (c.isInterface())
        {
          res.append(" interface: #t");
        }
        res.append(convertPsiToText(c.getModifierList()).replace("abstract", ""));

        for (PsiField field : c.getFields())
        {
          res.append("\n  ").append(convertPsiToText(field));
        }
        for (PsiMethod method : c.getMethods())
        {
          res.append("\n  ").append(convertPsiToText(method));
        }
        //      TODO
        //      for (PsiClass clazz : c.getInnerClasses())
        //      {
        //        res.append("  ").append(convertPsiToText(clazz)).append("\n");
        //      }
        res.append(")");
      }
    }
    else if (element instanceof PsiModifierList)
    {
      PsiModifierList m = (PsiModifierList) element;
      //todo: synchronized, native, abstract, annotations, enum?
      List<String> accessModifiers = new ArrayList<String>();
      if (m.hasModifierProperty("protected"))
      {
        accessModifiers.add("protected");
      }
      else if (m.hasModifierProperty("private"))
      {
        accessModifiers.add("private");
      }
      else if (!m.hasModifierProperty("public"))
      {
        accessModifiers.add("package");
      }
      for (String modifier : collection("volatile", "transient", "final"))
      {
        if (m.hasModifierProperty(modifier))
        {
          accessModifiers.add(modifier);
        }
      }
      if (accessModifiers.size() == 1)
      {
        res.append(" access: '").append(accessModifiers.iterator().next());
      }
      else if (accessModifiers.size() > 1)
      {
        res.append(" access: '(");
        String separator = "";
        for (String modifier : accessModifiers)
        {
          res.append(separator);
          separator = " ";
          res.append(modifier);
        }
        res.append(")");
      }
    }
    else if (element instanceof PsiReferenceParameterList)
    {
      // TODO Generics?
      //      PsiReferenceParameterList r = (PsiReferenceParameterList) element;
      //      res.append(convertPsiElements(r.getTypeParameterElements(), "[", "]"));
    }
    else if (element instanceof PsiField)
    {
      PsiField f = (PsiField) element;
      res.append("(");
      res.append(escapeKeyword(f.getName())).append(" :: ");
      res.append(convertPsiToText(f.getTypeElement())).append(" ");

      if (f.getInitializer() != null)
      {
        res.append(" init: ").append(convertPsiToText(f.getInitializer()));
      }

      res.append(convertPsiToText(f.getModifierList())).append(" ");
      if (f.hasModifierProperty("static"))
      {
        res.append(" allocation: 'class");
      }
      //      else
      //      {
      //          res.append(" = ")
      //          import lang.psi.types._
      //          res.append(ScType.create(f.getType, f.getProject) match {
      //            case Int => "0"
      //            case Boolean => "false"
      //            case Long => "0L"
      //            case Byte => "0"
      //            case Double => ".0"
      //            case Float => ".0"
      //            case Short => "0"
      //            case Unit => "{}"
      //            case Char => "0"
      //            case _ => "null"
      //          })
      //      }
      res.append(")");
    }
    else if (element instanceof PsiTypeElement)
    {
      PsiTypeElement t = (PsiTypeElement) element;
      res.append(t.getType().getPresentableText());
      /*if (t.getText.endsWith("[]")) {
        res.append("Array[").append(convertPsiToText(t.getFirstChild)).append("]")
      } else {
        t.getFirstChild match {
          else if (element instanceof PsiKeyword) { PsiKeyword k = (PsiKeyword) element;
            k.getText match {
              case "int" => res.append("Int")
              case "long" => res.append("Long")
              case "boolean" => res.append("Boolean")
              case "short" => res.append("Short")
              case "double" => res.append("Double")
              case "void" => res.append("Unit")
              case "float" => res.append("Float")
              case "byte" => res.append("Byte")
              case "char" => res.append("Char")
            }
          }
          case x => res.append(convertPsiToText(x))
        }
      }*/
    }
    else if (element instanceof PsiLiteralExpression)
    {
      res.append(element.getText());
    }
    else if (element instanceof PsiMethodCallExpression)
    {
      PsiMethodCallExpression m = (PsiMethodCallExpression) element;
      PsiReferenceExpression e = m.getMethodExpression();
      PsiElement psiElement = e.resolve();
      if (psiElement instanceof PsiMethod)
      {
        PsiMethod psiMethod = (PsiMethod) psiElement;
        String methodName = psiMethod.isConstructor() ? "*init*" : psiMethod.getName();
        PsiClass psiClass = findAncestor(psiMethod, PsiClass.class);

        if (psiMethod.isConstructor())
        {
          res.append("(invoke-special ");
          res.append(unqualify(psiClass.getQualifiedName()));
          res.append(" (this) '*init*");
          res.append(convertPsiToText(m.getArgumentList()));
          res.append(")");
        }
        else
        {
          PsiExpression methodTarget = e.getQualifierExpression();
          if ((methodTarget == null) || (methodTarget instanceof PsiThisExpression))
          {
            res.append("((this):");
            res.append(methodName);
            res.append(convertPsiToText(m.getArgumentList()));
            res.append(")");
          }
          else if (methodTarget instanceof PsiSuperExpression)
          {
            res.append("(invoke-special ");
            // TODO maybe shorten
            res.append(unqualify(psiClass.getQualifiedName()));
            res.append(" (this) '");
            res.append(escapeKeyword(methodName));
            res.append(convertPsiToText(m.getArgumentList()));
            res.append(")");
          }
          else if ((methodTarget instanceof PsiReferenceExpression) ||
                   (methodTarget instanceof PsiMethodCallExpression))
          {
            res.append("(");
            res.append(convertPsiToText(methodTarget));
            res.append(":");
            res.append(methodName);
            res.append(" ");
            res.append(convertPsiToText(m.getArgumentList()));
            res.append(")");
          }
          else
          {
            res.append("; Method qualifier expression ");
            res.append(methodTarget.getClass().getSimpleName());
            res.append(" not supported\n");
          }
        }
      }
      else
      {
        res.append("; Method expression ");
        res.append(psiElement.getClass().getSimpleName());
        res.append(" not supported\n");
      }
    }
    else if (element instanceof PsiReferenceExpression)
    {
      PsiReferenceExpression p = (PsiReferenceExpression) element;
      PsiElement referent = p.resolve();

      if (referent instanceof PsiField)
      {
        PsiField field = (PsiField) referent;
        if (field.getModifierList().hasModifierProperty("static"))
        {
          PsiClass psiClass = field.getContainingClass();
          res.append("(");
          res.append(unqualify(psiClass.getQualifiedName()));
          res.append(":.");
          res.append(field.getName());
          res.append(")");
        }
        else
        {
          res.append("; Referent non-static expression ");
          res.append(referent == null ? "null" : referent.getClass().getSimpleName());
          res.append(" not supported\n");
        }
      }
      else if (referent instanceof PsiParameter)
      {
        PsiParameter parameter = (PsiParameter) referent;
        res.append(escapeKeyword(parameter.getName()));
      }
      else
      {
        res.append("; Referent expression ");
        res.append(referent == null ? "null" : referent.getClass().getSimpleName());
        res.append(" not supported\n");
      }

      //      if (p.getQualifierExpression() != null)
      //      {
      //        res.append(convertPsiToText(p.getQualifierExpression())).append(".");
      //      }
      //      res.append(escapeKeyword(p.getReferenceName()));
      //      res.append(convertPsiToText(p.getParameterList()));
    }
    else if (element instanceof PsiJavaCodeReferenceElement)
    {
      PsiJavaCodeReferenceElement p = (PsiJavaCodeReferenceElement) element;
      if (p.getQualifier() != null)
      {
        res.append(convertPsiToText(p.getQualifier())).append(".");
      }
      res.append(escapeKeyword(p.getReferenceName()));
      res.append(convertPsiToText(p.getParameterList()));
    }
    else if (element instanceof PsiParameterList)
    {
      PsiParameterList p = (PsiParameterList) element;
      String separator = "";
      for (PsiElement parameter : p.getParameters())
      {
        res.append(separator);
        separator = " ";
        res.append(convertPsiToText(parameter));
      }
    }
    else if (element instanceof PsiExpressionList)
    {
      PsiExpressionList e = (PsiExpressionList) element;
      if (e.getExpressions().length != 0)
      {
        String separator = "";
        for (PsiExpression expr : e.getExpressions())
        {
          res.append(separator);
          separator = " ";
          res.append(convertPsiToText(expr));
        }
      }
    }
    else if (element instanceof PsiNewExpression)
    {
      PsiNewExpression n = (PsiNewExpression) element;
      if (n.getAnonymousClass() == null)
      {
        res.append("(");
        if (n.getArrayInitializer() != null)
        {
          // TODO check
          res.append(n.getType().getPresentableText());
          res.append(convertPsiElements(n.getArrayInitializer().getInitializers(), "(", ")"));
        }
        else if (n.getArrayDimensions().length > 0)
        {
          // TODO check
          res.append(n.getType().getPresentableText());
          res.append(convertPsiElements(n.getArrayDimensions(), "(", ")"));
        }
        else
        {
          res.append(n.getType().getPresentableText());
          if (n.getArgumentList().getExpressions().length != 0)
          {
            res.append(convertPsiToText(n.getArgumentList()));
          }
        }
        res.append(")");
      }
      else
      {
        res.append(convertPsiToText(n.getAnonymousClass()));
      }
    }
    else if (element instanceof PsiMethod)
    {
      PsiMethod m = (PsiMethod) element;
      res.append("((");
      if (!m.isConstructor())
      {
        res.append(escapeKeyword(m.getName()));
      }
      else
      {
        res.append("*init*");
      }
      res.append(convertPsiToText(m.getParameterList()));
      res.append(")");
      if (!m.isConstructor())
      {
        res.append(" :: ").append(convertPsiToText(m.getReturnTypeElement()));
      }
      res.append(convertPsiToText(m.getModifierList())).append(" ");
      PsiModifierList modifierList = m.getModifierList();
      if (modifierList.hasModifierProperty("abstract"))
      {
        res.append("#!abstract");
      }
      else if (m.getBody() != null)
      {
        PsiCodeBlock body = m.getBody();
        if ((body.getStatements().length == 1) && (body.getStatements()[0] instanceof PsiReturnStatement))
        {
          PsiReturnStatement returnStatement = (PsiReturnStatement) body.getStatements()[0];
          res.append(convertPsiToText(returnStatement.getReturnValue()));
        }
        else
        {
          res.append(convertPsiToText(body));
        }
      }
      res.append(")");
    }
    else if (element instanceof PsiCodeBlock)
    {
      PsiCodeBlock b = (PsiCodeBlock) element;
      String separator = "";
      for (PsiStatement st : b.getStatements())
      {
        res.append(separator);
        separator = "\n";
        res.append(convertPsiToText(st));
      }
    }
    else if (element instanceof PsiReturnStatement)
    {
      PsiReturnStatement r = (PsiReturnStatement) element;
      res.append(convertPsiToText(r.getReturnValue())).append("; return\n");
    }
    else if (element instanceof PsiExpressionStatement)
    {
      PsiExpressionStatement e = (PsiExpressionStatement) element;
      res.append(convertPsiToText(e.getExpression()));
    }
    else if (element instanceof PsiParameter)
    {
      PsiParameter p = (PsiParameter) element;
      res.append(escapeKeyword(p.getName())).append(" :: ").append(convertPsiToText(p.getTypeElement()));
    }
    else
    {
      res.append("; Element ");
      res.append(element.getClass().getSimpleName());
      res.append(" not supported\n");
    }
    //statements
    //else
    //    if (element instanceof PsiIfStatement)
    //    {
    //      PsiIfStatement f = (PsiIfStatement) element;
    //      res.append("if (").append(convertPsiToText(f.getCondition())).append(") ").
    //        append(convertPsiToText(f.getThenBranch()));
    //      if (f.getElseElement() != null)
    //      {
    //        res.append("\nelse ").append(convertPsiToText(f.getElseBranch()));
    //      }
    //    }
    //    else if (element instanceof PsiBlockStatement)
    //    {
    //      PsiBlockStatement b = (PsiBlockStatement) element;
    //      res.append(convertPsiToText(b.getCodeBlock()));
    //    }
    //    else if (element instanceof PsiWhileStatement)
    //    {
    //      PsiWhileStatement w = (PsiWhileStatement) element;
    //      res.append("while (").append(convertPsiToText(w.getCondition())).append(") ").
    //        append(convertPsiToText(w.getBody()));
    //    }
    //    else if (element instanceof PsiDoWhileStatement)
    //    {
    //      PsiDoWhileStatement d = (PsiDoWhileStatement) element;
    //      res.append("do ").append(convertPsiToText(d.getBody())).append("while (").
    //        append(convertPsiToText(d.getCondition())).append(")");
    //    }
    //    else if (element instanceof PsiAssertStatement)
    //    {
    //      PsiAssertStatement a = (PsiAssertStatement) element;
    //      res.append("assert(").append(convertPsiToText(a.getAssertCondition()));
    //      PsiExpression v = a.getAssertDescription();
    //      if (v != null)
    //      {
    //        res.append(", ").append(convertPsiToText(v));
    //      }
    //      res.append(")");
    //    }
    //    else if (element instanceof PsiBreakStatement)
    //    {
    //      PsiBreakStatement b = (PsiBreakStatement) element;
    //      if (b.getLabelIdentifier() != null)
    //      {
    //        res.append("break //todo: label break is not supported");
    //      }
    //      else
    //      {
    //        res.append("break //todo: break is not supported");
    //      }
    //    }
    //    else if (element instanceof PsiContinueStatement)
    //    {
    //      PsiContinueStatement c = (PsiContinueStatement) element;
    //      res.append("continue //todo: continue is not supported");
    //    }
    //    else if (element instanceof PsiDeclarationStatement)
    //    {
    //      PsiDeclarationStatement d = (PsiDeclarationStatement) element;
    //      for (PsiElement decl : d.getDeclaredElements())
    //      {
    //        res.append(convertPsiToText(decl)).append("\n");
    //      }
    //      res.delete(res.length() - 1, res.length());
    //    }
    //    else if (element instanceof PsiExpressionListStatement)
    //    {
    //      PsiExpressionListStatement e = (PsiExpressionListStatement) element;
    //      for (PsiExpression expr : e.getExpressionList().getExpressions())
    //      {
    //        res.append(convertPsiToText(expr)).append("\n");
    //      }
    //      res.delete(res.length() - 1, res.length());
    //    }
    //    else if (element instanceof PsiForStatement)
    //    {
    //      PsiForStatement f = (PsiForStatement) element;
    //      if (f.getInitialization() != null && !(f.getInitialization() instanceof PsiEmptyStatement))
    //      {
    //        res.append("\n{\n").append(convertPsiToText(f.getInitialization())).append("\n");
    //      }
    //      PsiExpression fCondition = f.getCondition();
    //      String
    //        condition =
    //        ((fCondition instanceof PsiEmptyStatement) || fCondition == null) ? "true" : convertPsiToText(f.getCondition());
    //      res.append("while (").append(condition).append(") ");
    //      if (f.getUpdate() != null)
    //      {
    //        res.append("{\n");
    //      }
    //      res.append(convertPsiToText(f.getBody()));
    //      if (f.getUpdate() != null)
    //      {
    //        res.append("\n").append(convertPsiToText(f.getUpdate())).append("\n}");
    //      }
    //      if (f.getInitialization() != null && !(f.getInitialization() instanceof PsiEmptyStatement))
    //      {
    //        res.append("\n}");
    //      }
    //    }
    //    else if (element instanceof PsiForeachStatement)
    //    {
    //      PsiForeachStatement f = (PsiForeachStatement) element;
    //      res.append("for (").append(escapeKeyword(f.getIterationParameter().getName())).append(" : ").
    //        append(convertPsiToText(f.getIteratedValue())).append(") ").
    //        append(convertPsiToText(f.getBody()));
    //    }
    //    else if (element instanceof PsiLabeledStatement)
    //    {
    //      PsiLabeledStatement s = (PsiLabeledStatement) element;
    //      res.append(convertPsiToText(s.getStatement())).append("//todo: labels is not supported");
    //    }
    //    else if (element instanceof PsiThrowStatement)
    //    {
    //      PsiThrowStatement t = (PsiThrowStatement) element;
    //      res.append("throw ").append(convertPsiToText(t.getException()));
    //    }
    //    else if (element instanceof PsiSynchronizedStatement)
    //    {
    //      PsiSynchronizedStatement s = (PsiSynchronizedStatement) element;
    //      res.append(convertPsiToText(s.getLockExpression())).append(" synchronized ").
    //        append(convertPsiToText(s.getBody()));
    //    }
    //    else if (element instanceof PsiSwitchLabelStatement)
    //    {
    //      PsiSwitchLabelStatement s = (PsiSwitchLabelStatement) element;
    //      res.append("case ").append(s.isDefaultCase() ? "_" : convertPsiToText(s.getCaseValue())).
    //        append(" => ");
    //    }
    //    else if (element instanceof PsiSwitchStatement)
    //    {
    //      PsiSwitchStatement s = (PsiSwitchStatement) element;
    //      res.append(convertPsiToText(s.getExpression())).append(" match ").
    //        append(convertPsiToText(s.getBody()));
    //    }
    //    else if (element instanceof PsiTryStatement)
    //    {
    //      PsiTryStatement t = (PsiTryStatement) element;
    //      res.append("try ").append(convertPsiToText(t.getTryBlock()));
    //      PsiCatchSection[] catchs = t.getCatchSections();
    //      if (catchs.length > 0)
    //      {
    //        res.append("\ncatch {\n");
    //        for (PsiCatchSection section : catchs)
    //        {
    //          res.append("case ").append(convertPsiToText(section.getParameter())).append(" => ").
    //            append(convertPsiToText(section.getCatchBlock()));
    //        }
    //        res.append("}");
    //      }
    //      if (t.getFinallyBlock() != null)
    //      {
    //        res.append("\n finally ").append(convertPsiToText(t.getFinallyBlock()));
    //      }
    //    }
    //    //expressions
    //    else if (element instanceof PsiArrayAccessExpression)
    //    {
    //      PsiArrayAccessExpression a = (PsiArrayAccessExpression) element;
    //      res.append(convertPsiToText(a.getArrayExpression())).append("(").
    //        append(convertPsiToText(a.getIndexExpression())).append(")");
    //    }
    //    else if (element instanceof PsiArrayInitializerExpression)
    //    {
    //      PsiArrayInitializerExpression a = (PsiArrayInitializerExpression) element;
    //      res.append("Array(");
    //      for (PsiExpression init : a.getInitializers())
    //      {
    //        res.append(convertPsiToText(init)).append(", ");
    //      }
    //      res.delete(res.length() - 2, res.length());
    //      res.append(")");
    //    }
    //    else if (element instanceof PsiAssignmentExpression)
    //    {
    //      PsiAssignmentExpression a = (PsiAssignmentExpression) element;
    //      if (!(a.getParent() instanceof PsiExpression))
    //      {
    //        res.append(convertPsiToText(a.getLExpression())).append(" ").
    //          append(a.getOperationSign().getText()).append(" ").append(convertPsiToText(a.getRExpression()));
    //      }
    //      else
    //      {
    //        res.append("({").append(convertPsiToText(a.getLExpression())).append(" ").
    //          append(a.getOperationSign().getText()).append(" ").append(convertPsiToText(a.getRExpression())).
    //          append("; ").append(convertPsiToText(a.getLExpression())).append("})");
    //      }
    //    }
    //    else if (element instanceof PsiBinaryExpression)
    //    {
    //      PsiBinaryExpression b = (PsiBinaryExpression) element;
    //      res.append(convertPsiToText(b.getLOperand())).append(" ").
    //        append(b.getOperationSign().getText()).append(" ").append(convertPsiToText(b.getROperand()));
    //    }
    //    else if (element instanceof PsiClassObjectAccessExpression)
    //    {
    //      PsiClassObjectAccessExpression c = (PsiClassObjectAccessExpression) element;
    //      res.append("classOf[").append(convertPsiToText(c.getOperand())).append("]");
    //    }
    //    else if (element instanceof PsiConditionalExpression)
    //    {
    //      PsiConditionalExpression c = (PsiConditionalExpression) element;
    //      res.append("if (").append(convertPsiToText(c.getCondition())).append(") ").
    //        append(convertPsiToText(c.getThenExpression()))
    //        .append(" else ")
    //        .append(convertPsiToText(c.getElseExpression()));
    //    }
    //    else if (element instanceof PsiInstanceOfExpression)
    //    {
    //      PsiInstanceOfExpression i = (PsiInstanceOfExpression) element;
    //      res.append(convertPsiToText(i.getOperand())).append(".isinstanceof[").
    //        append(convertPsiToText(i.getCheckType())).append("]");
    //    }
    //    else if (element instanceof PsiPrefixExpression)
    //    {
    //      PsiPrefixExpression p = (PsiPrefixExpression) element;
    //      IElementType tokenType = p.getOperationTokenType();
    //      if (tokenType == JavaTokenType.PLUSPLUS)
    //      {
    //        res.append("({i += 1; i - 1})".replace("i", convertPsiToText(p.getOperand())));
    //      }
    //      else if (tokenType == JavaTokenType.MINUSMINUS)
    //      {
    //        res.append("({i -= 1; i + 1})".replace("i", convertPsiToText(p.getOperand())));
    //      }
    //      else
    //      {
    //        res.append(p.getOperationSign().getText()).append(convertPsiToText(p.getOperand()));
    //      }
    //    }
    //    else if (element instanceof PsiPostfixExpression)
    //    {
    //      PsiPostfixExpression p = (PsiPostfixExpression) element;
    //      IElementType tokenType = p.getOperationTokenType();
    //      if (tokenType == JavaTokenType.PLUSPLUS)
    //      {
    //        res.append("({i += 1; i})".replace("i", convertPsiToText(p.getOperand())));
    //      }
    //      else if (tokenType == JavaTokenType.MINUSMINUS)
    //      {
    //        res.append("({i -= 1; i})".replace("i", convertPsiToText(p.getOperand())));
    //      }
    //    }
    //    else if (element instanceof PsiParenthesizedExpression)
    //    {
    //      PsiParenthesizedExpression p = (PsiParenthesizedExpression) element;
    //      res.append("(").append(convertPsiToText(p.getExpression())).append(")");
    //    }
    //    }
    //    else if (element instanceof PsiTypeCastExpression)
    //    {
    //      PsiTypeCastExpression t = (PsiTypeCastExpression) element;
    //      res.append(convertPsiToText(t.getOperand())).append(".asinstanceof[").
    //        append(convertPsiToText(t.getCastType())).append("]");
    //    }
    //    else if (element instanceof PsiThisExpression)
    //    {
    //      PsiThisExpression t = (PsiThisExpression) element;
    //      if (t.getQualifier() != null)
    //      {
    //        res.append(convertPsiToText(t.getQualifier())).append(".");
    //      }
    //      res.append("this");
    //    }
    //    else if (element instanceof PsiSuperExpression)
    //    {
    //      PsiSuperExpression s = (PsiSuperExpression) element;
    //      if (s.getQualifier() != null)
    //      {
    //        res.append(convertPsiToText(s.getQualifier())).append(".");
    //      }
    //      res.append("super");
    //    }
    //    //declarations
    //    else if (element instanceof PsiLocalVariable)
    //    {
    //      PsiLocalVariable l = (PsiLocalVariable) element;
    //      res.append(convertPsiToText(l.getModifierList())).append(" ");
    //      if (l.hasModifierProperty("final"))
    //      {
    //        res.append(" val ");
    //      }
    //      else
    //      {
    //        res.append(" var ");
    //      }
    //      res.append(escapeKeyword(l.getName())).append(" : ");
    //      res.append(convertPsiToText(l.getTypeElement()));
    //      if (l.getInitializer() != null)
    //      {
    //        res.append(" = ").append(convertPsiToText(l.getInitializer()));
    //      }
    //      else
    //      {
    //        res.append(" = ");
    //        //          import lang.psi.types._
    //        //          res.append(ScType.create(l.getType, l.getProject) match {
    //        //            case Int => "0"
    //        //            case Boolean => "false"
    //        //            case Long => "0L"
    //        //            case Byte => "0"
    //        //            case Double => ".0"
    //        //            case Float => ".0"
    //        //            case Short => "0"
    //        //            case Unit => "{}"
    //        //            case Char => "0"
    //        //            case _ => "null"
    //        //          })
    //      }
    //    }
    //    /*else if (element instanceof PsiAnonymousClass) { PsiAnonymousClass a = (PsiAnonymousClass) element;
    //      a.get
    //    }*/
    //    else if (element instanceof PsiPackageStatement)
    //    {
    //      PsiPackageStatement p = (PsiPackageStatement) element;
    //      res.append("package ");
    //      res.append(convertPsiToText(p.getPackageReference()));
    //    }
    //    else if (element instanceof PsiImportStaticStatement)
    //    {
    //      PsiImportStaticStatement i = (PsiImportStaticStatement) element;
    //      res.append("import ");
    //      res.append(convertPsiToText(i.getImportReference()));
    //      if (i.isOnDemand())
    //      {
    //        res.append("._");
    //      }
    //    }
    //    else if (element instanceof PsiAnnotation)
    //    {
    //      PsiAnnotation annot = (PsiAnnotation) element;
    //      res.append("@").append(escapeKeyword(annot.getNameReferenceElement().getText()));
    //      PsiNameValuePair[] attributes = annot.getParameterList().getAttributes();
    //      if (attributes.length > 0)
    //      {
    //        res.append("(");
    //        for (PsiNameValuePair attribute : attributes)
    //        {
    //          // TODO wrap vararg annotation arguments in Array(..)
    //          if (attribute.getName() != null)
    //          {
    //            res.append(escapeKeyword(attribute.getName()));
    //            res.append(" = ");
    //          }
    //          res.append(convertPsiToText(attribute.getValue()));
    //        }
    //        res.append(")");
    //      }
    //      res.append(" ");
    //    }
    //    else if (element instanceof PsiComment)
    //    {
    //      PsiComment comment = (PsiComment) element;
    //      res.append(comment.getText());
    //    }
    //    else if (element instanceof PsiEmptyStatement)
    //    {
    //    }
    //    else
    //    {
    //      throw new UnsupportedOperationException("PsiElement: " + element + " is not supported for this" + " converter.");
    //    }
    return res.toString();
  }

  private String unqualify(String qualifiedName)
  {
    return imports.containsKey(qualifiedName) ? imports.get(qualifiedName) : qualifiedName;
  }

  private static <T extends PsiElement> T findAncestor(PsiElement element, Class<T> elementClass)
  {
    PsiElement ret = element;
    while (!elementClass.isAssignableFrom(ret.getClass()))
    {
      ret = ret.getContext();
    }
    return elementClass.cast(ret);
  }

  private <T extends PsiElement> String convertPsiElements(T[] elements, String init, String term)
  {
    StringBuilder builder = new StringBuilder();
    if (elements.length > 0)
    {
      builder.append(init);
      String separator = "";
      for (PsiElement element : elements)
      {
        builder.append(separator);
        separator = " ";
        builder.append(convertPsiToText(element));
      }
      builder.append(term);
    }
    return builder.toString();
  }

  private static <T> Collection<T> collection(T... array)
  {
    return Arrays.asList(array);
  }
}