package schemely.conversion;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;

class JavaToSchemeAction extends AnAction
{
  @Override
  public void update(AnActionEvent e)
  {
    Presentation presentation = e.getPresentation();

    try
    {
      DataContext dataContext = e.getDataContext();
      Object data = dataContext.getData(DataConstants.PSI_FILE);
      if (data instanceof PsiJavaFile)
      {
        PsiJavaFile file = (PsiJavaFile) data;
        PsiDirectory directory = file.getContainingDirectory();
        if ((directory != null) && directory.isWritable())
        {
          presentation.setEnabled(true);
          presentation.setVisible(true);
        }
      }
      else
      {
        presentation.setEnabled(false);
        presentation.setVisible(false);
      }
    }
    catch (Exception ignore)
    {
      presentation.setEnabled(false);
      presentation.setVisible(false);
    }
  }

  @Override
  public void actionPerformed(AnActionEvent e)
  {
    Object data = e.getDataContext().getData(DataConstants.PSI_FILE);
    if (data instanceof PsiJavaFile)
    {
      final PsiJavaFile file = (PsiJavaFile) data;

      ApplicationManager.getApplication().runWriteAction(new Runnable()
      {
        @Override
        public void run()
        {
          PsiDirectory directory = file.getContainingDirectory();
          String name = file.getName().substring(0, file.getName().length() - 5);

          PsiFile newFile = directory.createFile(name + ".scm");
          JavaToScheme toScheme = new JavaToScheme();
          String newText = toScheme.convertPsiToText(file).trim();
          Document document = PsiDocumentManager.getInstance(newFile.getProject()).getDocument(newFile);
          document.insertString(0, newText);
          PsiDocumentManager.getInstance(newFile.getProject()).commitDocument(document);
          CodeStyleManager manager = CodeStyleManager.getInstance(newFile.getProject());
          CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(newFile.getProject());
          int keep_blank_lines_in_code = settings.KEEP_BLANK_LINES_IN_CODE;
          int keep_blank_lines_in_declarations = settings.KEEP_BLANK_LINES_IN_DECLARATIONS;
          int keep_blank_lines_before_rbrace = settings.KEEP_BLANK_LINES_BEFORE_RBRACE;
          settings.KEEP_BLANK_LINES_IN_CODE = 0;
          settings.KEEP_BLANK_LINES_IN_DECLARATIONS = 0;
          settings.KEEP_BLANK_LINES_BEFORE_RBRACE = 0;
          manager.reformatText(newFile, 0, newFile.getTextLength());
          settings.KEEP_BLANK_LINES_IN_CODE = keep_blank_lines_in_code;
          settings.KEEP_BLANK_LINES_IN_DECLARATIONS = keep_blank_lines_in_declarations;
          settings.KEEP_BLANK_LINES_BEFORE_RBRACE = keep_blank_lines_before_rbrace;
          newFile.navigate(true);
        }
      });
    }
  }
}