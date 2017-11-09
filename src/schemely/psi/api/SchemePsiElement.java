package schemely.psi.api;

import com.intellij.psi.PsiElement;

/**
 * @author Colin Fleming
 */
public interface SchemePsiElement extends PsiElement
{

  int getQuotingLevel();
}
