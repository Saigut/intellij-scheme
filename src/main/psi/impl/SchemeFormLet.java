package main.psi.impl;

import com.intellij.lang.ASTNode;

public class SchemeFormLet extends SchemeFormLocalBase
{
    public SchemeFormLet(ASTNode node)
    {
        super(node, "SchemeFormLet");
    }
}
