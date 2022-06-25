package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import main.SchemeIcons;
import main.psi.util.SchemePsiUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SchemeFormLibrary extends SchemePsiElementBase
{
    public SchemeFormLibrary(ASTNode node)
    {
        super(node, "SchemeFormLibrary");
    }

    @Override
    public ItemPresentation getPresentation()
    {
        return new ItemPresentation()
        {
            public String getPresentableText()
            {
                PsiElement child = SchemePsiUtil.getNormalChildAt(getMe(), 1);
                if (child == null) {
                    return "<undefined>";
                }
                if (child.getFirstChild() == null) {
                    return "<undefined>";
                }
                PsiElement name_child = SchemePsiUtil.getPsiLastNonLeafChild(child);
                if (name_child == null) {
                    return "<undefined>";
                }
                return "library: " + name_child.getText();
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
