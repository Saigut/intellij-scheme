package main.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import main.psi.impl.SchemeFile;


public abstract class SchemePsiElementFactory
{
  public static SchemePsiElementFactory getInstance(Project project)
  {
    return project.getService(SchemePsiElementFactory.class);
  }

  public abstract ASTNode createSymbolNodeFromText(@NotNull String newName);

  public abstract boolean hasSyntacticalErrors(@NotNull PsiElement element);

  public abstract SchemeFile createSchemeFileFromText(String text);
}
