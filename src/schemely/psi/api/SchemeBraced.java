package schemely.psi.api;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface SchemeBraced extends SchemePsiElement
{
  @NotNull
  PsiElement getFirstBrace();

  @Nullable
  PsiElement getLastBrace();
}
