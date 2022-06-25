package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import main.SchemeIcons;
import main.psi.util.SchemePsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

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

    @Override
    public ItemPresentation getPresentation()
    {
        PsiElement child = SchemePsiUtil.getNormalChildAt(getMe(), 1);
        if (child == null) {
            return getPresentation1();
        }
        return ((NavigatablePsiElement)child).getPresentation();
    }

    public ItemPresentation getPresentation1()
    {
        return new ItemPresentation()
        {
            public String getPresentableText()
            {
                return "<undefined>";
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
