package schemely.highlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import schemely.SchemeIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class SchemeColorsAndFontsPage implements ColorSettingsPage
{
  @NotNull
  public String getDisplayName()
  {
    return "Scheme";
  }

  @Nullable
  public Icon getIcon()
  {
    return SchemeIcons.SCHEME_ICON;
  }

  @NotNull
  public AttributesDescriptor[] getAttributeDescriptors()
  {
    return ATTRS;
  }

  private static final
  AttributesDescriptor[]
    ATTRS =
    new AttributesDescriptor[]{desc(SchemeSyntaxHighlighter.IDENTIFIER_ID, SchemeSyntaxHighlighter.IDENTIFIER),
                               desc(SchemeSyntaxHighlighter.COMMENT_ID, SchemeSyntaxHighlighter.LINE_COMMENT),
                               desc(SchemeSyntaxHighlighter.NUMBER_ID, SchemeSyntaxHighlighter.NUMBER),
                               desc(SchemeSyntaxHighlighter.STRING_ID, SchemeSyntaxHighlighter.STRING),
                               desc(SchemeSyntaxHighlighter.BRACES_ID, SchemeSyntaxHighlighter.BRACE),
                               desc(SchemeSyntaxHighlighter.PAREN_ID, SchemeSyntaxHighlighter.PAREN),
                               desc(SchemeSyntaxHighlighter.BAD_CHARACTER_ID, SchemeSyntaxHighlighter.BAD_CHARACTER),
                               desc(SchemeSyntaxHighlighter.CHAR_ID, SchemeSyntaxHighlighter.CHAR),
                               desc(SchemeSyntaxHighlighter.LITERAL_ID, SchemeSyntaxHighlighter.LITERAL),
                               desc(SchemeSyntaxHighlighter.KEYWORD_ID, SchemeSyntaxHighlighter.KEYWORD),
                               desc(SchemeSyntaxHighlighter.SPECIAL_ID, SchemeSyntaxHighlighter.SPECIAL),
                               desc(SchemeSyntaxHighlighter.QUOTED_TEXT_ID, SchemeSyntaxHighlighter.QUOTED_TEXT),
                               desc(SchemeSyntaxHighlighter.QUOTED_STRING_ID, SchemeSyntaxHighlighter.QUOTED_STRING),
                               desc(SchemeSyntaxHighlighter.QUOTED_NUMBER_ID, SchemeSyntaxHighlighter.QUOTED_NUMBER),
    };

  private static AttributesDescriptor desc(String displayName, TextAttributesKey key)
  {
    return new AttributesDescriptor(displayName, key);
  }

  @NotNull
  public ColorDescriptor[] getColorDescriptors()
  {
    return new ColorDescriptor[0];
  }

  @NotNull
  public SyntaxHighlighter getHighlighter()
  {
    return new SchemeSyntaxHighlighter();
  }

  @NonNls
  @NotNull
  public String getDemoText()
  {
    return ";; Test highlighting\n" +
           "\n" +
           "(define string \"Some string\")\n" +
           "\n" +
           "(define quoted '(my quoted 3 items \"with quoted string\"))\n" +
           "\n" +
           "(define char #\\c)\n" +
           "\n" +
           "(define special #!eof)\n" +
           "\n" +
           "(let ((x '(1 3 5 7 9)))\n" +
           "  (do ((x x (cdr x))\n" +
           "       (sum 0 (+ sum (car x))))\n" +
           "      ((null? x) sum)))";
  }

  @Nullable
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap()
  {
    Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
    map.put("def", SchemeSyntaxHighlighter.IDENTIFIER);
    return map;
  }
}
