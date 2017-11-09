package schemely.repl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.impl.light.LightElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import schemely.file.SchemeFileType;

/**
 * @author Colin Fleming
 */
public class SchemeConsoleElement extends LightElement implements PsiNamedElement
{
  @NotNull
  private final String name;

  public SchemeConsoleElement(PsiManager manager, @NotNull String name)
  {
    super(manager, SchemeFileType.SCHEME_LANGUAGE);
    this.name = name;
  }

  @Override
  public String getText()
  {
    return name;
  }

  @Override
  public PsiElement copy()
  {
    return null;
  }

  @Override
  @NotNull
  public String getName()
  {
    return name;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException
  {
    throw new UnsupportedOperationException("Can't set name for console elements");
  }

  @Override
  public String toString()
  {
    return "Console element " + name;
  }
}
