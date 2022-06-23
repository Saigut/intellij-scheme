package main.psi.impl;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SchemeFormLetBase extends SchemeFormLocalBase
{
    public SchemeFormLetBase(ASTNode node, @NotNull String formName)
    {
        super(node, formName);
    }
}
