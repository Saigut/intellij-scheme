package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class SchemeFormDefineBase extends SchemeFormLocalBase
{
    PsiElement declareName;

    public SchemeFormDefineBase(ASTNode node, @NotNull String formName)
    {
        super(node, formName);
    }

    public void setDeclareName(PsiElement name) {
        declareName = name;
    }

    public PsiElement getDeclareName() {
        return declareName;
    }
}
