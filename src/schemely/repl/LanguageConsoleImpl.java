/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package schemely.repl;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.DataManager;
import com.intellij.ide.impl.TypeSafeDataProviderAdapter;
import com.intellij.lang.Language;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataSink;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.actionSystem.TypeSafeDataProvider;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollingModel;
import com.intellij.openapi.editor.actions.EditorActionUtil;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.impl.DelegateColorScheme;
import com.intellij.openapi.editor.event.VisibleAreaEvent;
import com.intellij.openapi.editor.event.VisibleAreaListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.impl.EditorFactoryImpl;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.fileEditor.impl.EditorWindow;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.fileEditor.impl.FileEditorManagerImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.impl.PsiDocumentManagerImpl;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.impl.PsiManagerEx;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.FileContentUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.update.MergingUpdateQueue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.FocusManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collections;

/**
 * Copied from IDEA core and heavily modified.
 * @author Colin Fleming
 * @author Gregory.Shrago
 */
public class LanguageConsoleImpl implements Disposable, TypeSafeDataProvider
{
  private final Project myProject;

  private final EditorEx myConsoleEditor;
  private final EditorEx myHistoryViewer;
  private final Document myEditorDocument;
  protected PsiFile myFile;

  private final Splitter splitter;

  private String myTitle;

  private Editor myCurrentEditor;

  private final MergingUpdateQueue myUpdateQueue;

  private Editor myFullEditor;

  public LanguageConsoleImpl(Project project, String title, Language language)
  {
    myProject = project;
    myTitle = title;
    installEditorFactoryListener();
    EditorFactory editorFactory = EditorFactory.getInstance();
    myEditorDocument = editorFactory.createDocument("");
    setLanguage(language);
    myConsoleEditor = (EditorEx) editorFactory.createEditor(myEditorDocument, myProject);

    myCurrentEditor = myConsoleEditor;
    myHistoryViewer =
      (EditorEx) editorFactory.createViewer(((EditorFactoryImpl) editorFactory).createDocument(true), myProject);
    final EditorColorsScheme colorsScheme = myConsoleEditor.getColorsScheme();
    DelegateColorScheme scheme = new DelegateColorScheme(colorsScheme)
    {
      @Override
      public Color getDefaultBackground()
      {
        Color color = getColor(ConsoleViewContentType.CONSOLE_BACKGROUND_KEY);
        return color == null ? super.getDefaultBackground() : color;
      }
    };
    myConsoleEditor.setColorsScheme(scheme);
    myHistoryViewer.setColorsScheme(scheme);

    splitter = new Splitter(true, 0.75f);
    splitter.setHonorComponentsMinimumSize(true);
    splitter.setDividerWidth(4);

    splitter.setFirstComponent(myHistoryViewer.getComponent());
    splitter.setSecondComponent(myConsoleEditor.getComponent());
    setupComponents();

    splitter.putClientProperty(DataManager.CLIENT_PROPERTY_DATA_PROVIDER, new TypeSafeDataProviderAdapter(this));

    myUpdateQueue = new MergingUpdateQueue("ConsoleUpdateQueue", 300, true, null);
    Disposer.register(this, myUpdateQueue);
  }

  private void setConsoleFilePinned(FileEditorManagerEx fileManager)
  {
    if (myFullEditor == null)
    {
      return;
    }
    EditorWindow editorWindow =
      EditorWindow.DATA_KEY.getData(DataManager.getInstance().getDataContext(myFullEditor.getComponent()));
    if (editorWindow == null)
    {
      editorWindow = fileManager.getCurrentWindow();
    }
    if (editorWindow != null)
    {
      editorWindow.setFilePinned(myFile.getVirtualFile(), true);
    }
  }

  private void setupComponents()
  {
    setupEditorDefault(myConsoleEditor);
    setupEditorDefault(myHistoryViewer);

    myConsoleEditor.addEditorMouseListener(EditorActionUtil.createEditorPopupHandler(IdeActions.GROUP_CUT_COPY_PASTE));

    myHistoryViewer.getComponent().setMinimumSize(new Dimension(0, 0));
    myHistoryViewer.getComponent().setPreferredSize(new Dimension(0, 0));
//    myConsoleEditor.getSettings().setAdditionalLinesCount(2);
    myConsoleEditor.setHighlighter(EditorHighlighterFactory.getInstance()
                                     .createEditorHighlighter(myProject, myFile.getVirtualFile()));
    myHistoryViewer.setCaretEnabled(false);

    myConsoleEditor.getScrollingModel().addVisibleAreaListener(new ConsoleVisibleAreaListener(myConsoleEditor));
    myHistoryViewer.getScrollingModel().addVisibleAreaListener(new ConsoleVisibleAreaListener(myHistoryViewer));

    myHistoryViewer.getContentComponent().addKeyListener(new KeyAdapter()
    {
      public void keyTyped(KeyEvent event)
      {
        if (myFullEditor == null && UIUtil.isReallyTypedEvent(event))
        {
          myConsoleEditor.getContentComponent().requestFocus();
          myConsoleEditor.processKeyTyped(event);
        }
      }
    });
    for (AnAction action : createActions())
    {
      action.registerCustomShortcutSet(action.getShortcutSet(), myConsoleEditor.getComponent());
    }
    registerActionShortcuts(myHistoryViewer.getComponent());
  }

  protected AnAction[] createActions()
  {
    return AnAction.EMPTY_ARRAY;
  }

  private static void setupEditorDefault(EditorEx editor)
  {
    editor.getContentComponent().setFocusCycleRoot(false);
    editor.setHorizontalScrollbarVisible(true);
    editor.setVerticalScrollbarVisible(true);
    editor.getColorsScheme().setColor(EditorColors.CARET_ROW_COLOR, null);
    editor.setBorder(null);
    editor.getContentComponent().setFocusCycleRoot(false);

    EditorSettings editorSettings = editor.getSettings();
    editorSettings.setAdditionalLinesCount(0);
    editorSettings.setAdditionalColumnsCount(1);
    editorSettings.setRightMarginShown(false);
    editorSettings.setFoldingOutlineShown(true);
    editorSettings.setLineNumbersShown(false);
    editorSettings.setLineMarkerAreaShown(false);
    editorSettings.setIndentGuidesShown(false);
    editorSettings.setVirtualSpace(false);
    editorSettings.setLineCursorWidth(1);
  }

  public PsiFile getFile()
  {
    return myFile;
  }

  public EditorEx getHistoryViewer()
  {
    return myHistoryViewer;
  }

  public Document getEditorDocument()
  {
    return myEditorDocument;
  }

  public EditorEx getConsoleEditor()
  {
    return myConsoleEditor;
  }

  public Project getProject()
  {
    return myProject;
  }

  public String getTitle()
  {
    return myTitle;
  }

  public void printToHistory(String text, TextAttributes attributes)
  {
    text = StringUtil.convertLineSeparators(text);
    Document history = myHistoryViewer.getDocument();
//    MarkupModel markupModel = history.getMarkupModel(myProject);
    int offset = history.getTextLength();
    appendToHistoryDocument(history, text);
//    markupModel.addRangeHighlighter(offset,
//                                    history.getTextLength(),
//                                    HighlighterLayer.SYNTAX,
//                                    attributes,
//                                    HighlighterTargetArea.EXACT_RANGE);
  }

  protected void appendToHistoryDocument(@NotNull Document history, @NotNull String text)
  {
    history.insertString(history.getTextLength(), text);
  }

  public JComponent getComponent()
  {
    return splitter;
  }

  public void dispose()
  {
    EditorFactory editorFactory = EditorFactory.getInstance();
    editorFactory.releaseEditor(myConsoleEditor);
    editorFactory.releaseEditor(myHistoryViewer);

    VirtualFile virtualFile = myFile.getVirtualFile();
    assert virtualFile != null;
    FileEditorManager editorManager = FileEditorManager.getInstance(getProject());
    boolean isOpen = editorManager.isFileOpen(virtualFile);
    if (isOpen)
    {
      editorManager.closeFile(virtualFile);
    }
  }

  public void calcData(DataKey key, DataSink sink)
  {
    if (OpenFileDescriptor.NAVIGATE_IN_EDITOR == key)
    {
      sink.put(OpenFileDescriptor.NAVIGATE_IN_EDITOR, myConsoleEditor);
      return;
    }
//    Object o = ((FileEditorManagerImpl) FileEditorManager.getInstance(getProject())).getData(key.getName(),
//                                                                                             myConsoleEditor,
//                                                                                             myFile.getVirtualFile());
//    sink.put(key, o);
  }

  private void installEditorFactoryListener()
  {
    myProject.getMessageBus()
      .connect(this)
      .subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerAdapter()
      {
        @Override
        public void fileOpened(FileEditorManager source, VirtualFile file)
        {
          if (file != myFile.getVirtualFile())
          {
            return;
          }
          if (myConsoleEditor != null)
          {
            for (FileEditor fileEditor : source.getAllEditors())
            {
              if (!(fileEditor instanceof TextEditor))
              {
                continue;
              }
              final Editor editor = ((TextEditor) fileEditor).getEditor();
              registerActionShortcuts(editor.getComponent());
              editor.getContentComponent().addFocusListener(new FocusListener()
              {
                public void focusGained(FocusEvent e)
                {
                  myCurrentEditor = editor;
                }

                public void focusLost(FocusEvent e)
                {
                }
              });
            }
          }
        }

        @Override
        public void fileClosed(FileEditorManager source, VirtualFile file)
        {
        }
      });
  }

  protected void registerActionShortcuts(JComponent component)
  {
//    Iterable<AnAction> actionList =
//      (Iterable<AnAction>) myConsoleEditor.getComponent().getClientProperty(AnAction.ourClientProperty);
//    if (actionList != null)
//    {
//      for (AnAction anAction : actionList)
//      {
//        anAction.registerCustomShortcutSet(anAction.getShortcutSet(), component);
//      }
//    }
  }

  public Editor getCurrentEditor()
  {
    return myCurrentEditor;
  }

  public void setLanguage(Language language)
  {
    PsiFile prevFile = myFile;
    if (prevFile != null)
    {
      VirtualFile file = prevFile.getVirtualFile();
      assert file instanceof LightVirtualFile;
      ((LightVirtualFile) file).setValid(false);
      ((PsiManagerEx) prevFile.getManager()).getFileManager().setViewProvider(file, null);
    }

    @NonNls String name = getTitle();
    LightVirtualFile newVFile = new LightVirtualFile(name, language, myEditorDocument.getText());
    FileDocumentManagerImpl.registerDocument(myEditorDocument, newVFile);
    myFile =
      ((PsiFileFactoryImpl) PsiFileFactory.getInstance(myProject)).trySetupPsiForFile(newVFile, language, true, false);
    if (myFile == null)
    {
      throw new AssertionError("file=null, name=" + name + ", language=" + language.getDisplayName());
    }
    PsiDocumentManagerImpl.cachePsi(myEditorDocument, myFile);
    FileContentUtil.reparseFiles(myProject, Collections.<VirtualFile>singletonList(newVFile), false);

    if (prevFile != null)
    {
      FileEditorManagerEx editorManager = FileEditorManagerEx.getInstanceEx(getProject());
      VirtualFile file = prevFile.getVirtualFile();
      if (file != null && myFullEditor != null)
      {
        myFullEditor = null;
        FileEditor prevEditor = editorManager.getSelectedEditor(file);
        boolean focusEditor;
        int offset;
        if (prevEditor != null)
        {
          offset =
            prevEditor instanceof TextEditor ? ((TextEditor) prevEditor).getEditor().getCaretModel().getOffset() : 0;
          Component owner = FocusManager.getCurrentManager().getFocusOwner();
          focusEditor = owner != null && SwingUtilities.isDescendingFrom(owner, prevEditor.getComponent());
        }
        else
        {
          focusEditor = false;
          offset = 0;
        }
        editorManager.closeFile(file);
        myFullEditor =
          editorManager.openTextEditor(new OpenFileDescriptor(getProject(), newVFile, offset), focusEditor);
        setConsoleFilePinned(editorManager);
      }
    }
  }

  public void setInputText(final String query)
  {
    ApplicationManager.getApplication().runWriteAction(new Runnable()
    {
      public void run()
      {
        myConsoleEditor.getDocument().setText(query);
      }
    });
  }

  public static void printToConsole(final LanguageConsoleImpl console,
                                    final String string,
                                    ConsoleViewContentType mainType,
                                    ConsoleViewContentType additionalType)
  {
    TextAttributes mainAttributes = mainType.getAttributes();
    final TextAttributes attributes;
    if (additionalType == null)
    {
      attributes = mainAttributes;
    }
    else
    {
      attributes = additionalType.getAttributes().clone();
      attributes.setBackgroundColor(mainAttributes.getBackgroundColor());
    }
    ApplicationManager.getApplication().invokeLater(new Runnable()
    {
      public void run()
      {
        console.printToHistory(string, attributes);
      }
    }, ModalityState.stateForComponent(console.getComponent()));
  }

  private class ConsoleVisibleAreaListener implements VisibleAreaListener
  {
    private final Editor editor;

    public ConsoleVisibleAreaListener(Editor editor)
    {
      this.editor = editor;
    }

    public void visibleAreaChanged(VisibleAreaEvent e)
    {
      ScrollingModel model = editor.getScrollingModel();
      int offset = model.getHorizontalScrollOffset();
      int historyOffset = model.getHorizontalScrollOffset();
      if (historyOffset != offset)
      {
        try
        {
          model.disableAnimation();
          model.scrollHorizontally(offset);
        }
        finally
        {
          model.enableAnimation();
        }
      }
    }
  }
}
