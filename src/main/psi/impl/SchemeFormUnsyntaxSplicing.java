package main.psi.impl;

import com.intellij.lang.ASTNode;

public class SchemeFormUnsyntaxSplicing extends SchemePsiElementBase
{
    public SchemeFormUnsyntaxSplicing(ASTNode node)
    {
        super(node, "SchemeFormUnquoteSplicing");
    }
}
