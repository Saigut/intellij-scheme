package schemely.actions;

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeBundle;
import schemely.SchemeIcons;
import schemely.file.SchemeFileType;
import schemely.lexer.Tokens;


public class NewSchemeFileAction extends NewSchemeActionBase
{
  private static final String DUMMY = "dummy.";

  public NewSchemeFileAction()
  {
    super(SchemeBundle.message("newfile.menu.action.text"),
          SchemeBundle.message("newfile.menu.action.description"),
          SchemeIcons.SCHEME_ICON);
  }

  protected String getActionName(PsiDirectory directory, String newName)
  {
    return SchemeBundle.message("newfile.menu.action.text");
  }

  protected String getDialogPrompt()
  {
    return SchemeBundle.message("newfile.dlg.prompt");
  }

  protected String getDialogTitle()
  {
    return SchemeBundle.message("newfile.dlg.title");
  }

  protected String getCommandName()
  {
    return SchemeBundle.message("newfile.command.name");
  }

  @NotNull
  protected PsiElement[] doCreate(String newName, PsiDirectory directory) throws Exception
  {
    PsiFile file = createFileFromTemplate(directory, newName, "SchemeFile.scm");
    PsiElement lastChild = file.getLastChild();
    Project project = directory.getProject();
    if (lastChild != null && lastChild.getNode() != null && lastChild.getNode().getElementType() != Tokens.WHITESPACE)
    {
      file.add(createWhiteSpace(project));
    }
    file.add(createWhiteSpace(project));
    PsiElement child = file.getLastChild();
    return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
  }

  private static PsiElement createWhiteSpace(Project project)
  {
    PsiFile
      dummyFile =
      PsiFileFactory.getInstance(project)
        .createFileFromText(DUMMY + SchemeFileType.SCHEME_FILE_TYPE.getDefaultExtension(), "\n");
    return dummyFile.getFirstChild();
  }

}
