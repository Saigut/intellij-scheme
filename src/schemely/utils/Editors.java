package schemely.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;

/**
 * @author Colin Fleming
 */
public class Editors
{
  public static void scrollDown(final Editor editor)
  {
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        editor.getCaretModel().moveToOffset(editor.getDocument().getTextLength());
        editor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
      }
    });
  }
}
