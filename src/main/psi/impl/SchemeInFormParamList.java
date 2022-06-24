package main.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import main.SchemeIcons;
import main.psi.util.SchemePsiUtil;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SchemeInFormParamList extends SchemePsiElementBase
{
    public SchemeInFormParamList(ASTNode node)
    {
        super(node, "SchemeInFormParamList");
    }

    @Override
    public ItemPresentation getPresentation()
    {
        return new ItemPresentation()
        {
            public String getPresentableText()
            {
                PsiElement child = SchemePsiUtil.getNormalChildAt(getMe(), 0);
                if (child != null) {
                    boolean firstParam = true;
                    String text = child.getText();
                    ASTNode nextNode = SchemePsiUtil.getNodeNextNonLeafSibling(child.getNode());
                    if (nextNode == null) {
                        return text;
                    }
                    text += ": (";
                    while (nextNode != null) {
                        if (firstParam) {
                            firstParam = false;
                        } else {
                            text += " ";
                        }
                        text += nextNode.getText();
                        nextNode = SchemePsiUtil.getNodeNextNonLeafSibling(nextNode);
                    }
                    text += ")";
                    return text;
                } else {
                    return "<undefined>";
                }
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
