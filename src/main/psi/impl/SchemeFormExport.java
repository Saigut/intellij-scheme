package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import main.SchemeIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SchemeFormExport extends SchemePsiElementBase
{
    public SchemeFormExport(ASTNode node)
    {
        super(node, "SchemeFormExport");
    }

    @Override
    public ItemPresentation getPresentation()
    {
        return new ItemPresentation()
        {
            public String getPresentableText()
            {
                return "export:";
            }

            @Nullable
            public Icon getIcon(boolean open)
            {
                return getMe().getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS);
            }
        };
    }

    @Override
    public Icon getIcon(int flags)
    {
        return SchemeIcons.SYMBOL;
    }

    private PsiElement getMe()
    {
        return this;
    }
}
