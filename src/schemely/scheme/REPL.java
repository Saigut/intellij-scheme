package schemely.scheme;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiNamedElement;
import com.intellij.ui.content.Content;
import schemely.repl.SchemeConsoleView;

import java.util.Collection;

/**
* @author Colin Fleming
*/
public interface REPL
{
  Key<REPL> REPL_KEY = Key.create("Scheme.REPL");
  Key<Content> CONTENT_KEY = Key.create("Scheme.REPL.Content");

  void execute(String command);

  void start() throws REPLException;

  void stop() throws REPLException;

  boolean isActive();

  boolean isExecuting();

  SchemeConsoleView getConsoleView();

  // Guaranteed to be called after start()
  AnAction[] getToolbarActions() throws REPLException;

  Collection<PsiNamedElement> getSymbolVariants(PsiManager manager, PsiElement symbol);
}
