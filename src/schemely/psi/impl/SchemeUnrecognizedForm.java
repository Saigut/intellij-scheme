package schemely.psi.impl;

import com.intellij.lang.ASTNode;

public class SchemeUnrecognizedForm extends SchemePsiElementBase
{
    public SchemeUnrecognizedForm(ASTNode node)
    {
        super(node, "SchemeUnrecognizedForm");
    }
}
