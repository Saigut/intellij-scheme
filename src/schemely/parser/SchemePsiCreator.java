package schemely.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;


public interface SchemePsiCreator
{
  PsiElement createElement(ASTNode node);

  PsiFile createFile(FileViewProvider viewProvider);
}
