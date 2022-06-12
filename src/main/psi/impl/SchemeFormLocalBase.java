package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;

public class SchemeFormLocalBase extends SchemePsiElementBase
{
    ArrayList<PsiElement> localDefinitions = new ArrayList<>();
    public SchemeFormLocalBase(ASTNode node, String name)
    {
        super(node, name);
    }

    public void clareLocalDefinitions() {
        localDefinitions.clear();
    }

    public void addLocalDefinition(PsiElement def) {
        localDefinitions.add(def);
    }

    public PsiElement[] getLocalDefinitions() {
        return localDefinitions.toArray(new PsiElement[0]);
    }
}
