package schemely.navigation;

import com.intellij.codeInsight.navigation.MethodNavigationOffsetProvider;
import com.intellij.codeInsight.navigation.MethodUpDownUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import schemely.psi.impl.SchemeFile;
import schemely.psi.impl.list.SchemeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Colin Fleming
 */
public class SchemeNavigationOffsetProvider implements MethodNavigationOffsetProvider
{
  @Override
  public int[] getMethodNavigationOffsets(PsiFile file, int caretOffset)
  {
    if (file instanceof SchemeFile)
    {
      List<PsiElement> array = new ArrayList<PsiElement>();
      for (PsiElement element : file.getChildren())
      {
        if (element instanceof SchemeList)
        {
          PsiElement navigationItem = ((SchemeList) element).getFunctionNavigationItem();
          if (navigationItem != null)
          {
            array.add(navigationItem);
          }
        }
      }
      return MethodUpDownUtil.offsetsFromElements(array);
    }
    return null;
  }
}
