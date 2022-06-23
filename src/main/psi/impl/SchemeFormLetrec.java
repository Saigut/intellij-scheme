package main.psi.impl;

import com.intellij.lang.ASTNode;

public class SchemeFormLetrec extends SchemeFormLetBase
{
    public SchemeFormLetrec(ASTNode node)
    {
        super(node, "SchemeFormLetrec");
    }
}
