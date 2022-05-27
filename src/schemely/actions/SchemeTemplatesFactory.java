package schemely.actions;

import com.intellij.ide.fileTemplates.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import schemely.SchemeBundle;
import schemely.SchemeIcons;

import java.util.Properties;


public class SchemeTemplatesFactory implements FileTemplateGroupDescriptorFactory
{
  @NonNls
  private static final String SCHEME_FILE = "SchemeFile.scm";

  public FileTemplateGroupDescriptor getFileTemplatesDescriptor()
  {
    FileTemplateGroupDescriptor
      group =
      new FileTemplateGroupDescriptor(SchemeBundle.message("file.template.group.title.scheme"),
                                      SchemeIcons.SCHEME_ICON);
    group.addTemplate(new FileTemplateDescriptor(SCHEME_FILE, SchemeIcons.SCHEME_ICON));
    return group;
  }
}
