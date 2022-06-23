package main.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import main.lexer.SchemeLexer;
import main.lexer.SchemeTokens;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SchemeSyntaxHighlighter extends SyntaxHighlighterBase implements SchemeTokens
{
  private static final Map<IElementType, TextAttributesKey[]> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey[]>();

  @NotNull
  public Lexer getHighlightingLexer()
  {
    return new SchemeLexer();
  }

  @NotNull
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
  {
    TextAttributesKey[] Keys = ATTRIBUTES.get(tokenType);
    if (null == Keys) {
//      System.out.println("tokenType: " + tokenType.toString());
      return EMPTY_KEYS;
    } else {
      return Keys;
    }
  }

  public static final String COMMENT_ID = "Scheme Comment";
  public static final String IDENTIFIER_ID = "Scheme Identifier";
  public static final String NUMBER_ID = "Scheme Numbers";
  public static final String STRING_ID = "Scheme Strings";
  public static final String STRING_ESCAPE_ID = "Scheme String Escape";
  public static final String BAD_CHARACTER_ID = "Scheme Bad character";
  public static final String BRACES_ID = "Scheme Braces";
  public static final String PAREN_ID = "Scheme Parentheses";
  public static final String LITERAL_ID = "Scheme Literal";
  public static final String CHAR_ID = "Scheme Character";
  public static final String KEYWORD_ID = "Scheme Keyword";
  public static final String PROCEDURE_ID = "Scheme Procedure";
  public static final String SPECIAL_ID = "Scheme Special";
  public static final String QUOTED_TEXT_ID = "Scheme Quoted text";
  public static final String QUOTED_STRING_ID = "Scheme Quoted string";
  public static final String QUOTED_NUMBER_ID = "Scheme Quoted number";
  public static final String DOT_ID = "Scheme Dot";
  public static final String ABBREVIATION_ID = "Scheme Abbreviation";

  public static TextAttributesKey COMMENT = createTextAttributesKey(COMMENT_ID, defaultFor(SyntaxHighlighterColors.LINE_COMMENT));
  public static TextAttributesKey IDENTIFIER = createTextAttributesKey(IDENTIFIER_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
  public static TextAttributesKey NUMBER = createTextAttributesKey(NUMBER_ID, defaultFor(SyntaxHighlighterColors.NUMBER));
  public static TextAttributesKey STRING = createTextAttributesKey(STRING_ID, defaultFor(SyntaxHighlighterColors.STRING));
  public static TextAttributesKey STRING_ESCAPE = createTextAttributesKey(STRING_ESCAPE_ID, defaultFor(SyntaxHighlighterColors.VALID_STRING_ESCAPE));
  public static TextAttributesKey BRACE = createTextAttributesKey(BRACES_ID, defaultFor(SyntaxHighlighterColors.BRACES));
  public static TextAttributesKey PAREN = createTextAttributesKey(PAREN_ID, defaultFor(SyntaxHighlighterColors.PARENTHS));
  public static TextAttributesKey LITERAL = createTextAttributesKey(LITERAL_ID, defaultFor(HighlighterColors.TEXT));
  public static TextAttributesKey CHAR = createTextAttributesKey(CHAR_ID, defaultFor(SyntaxHighlighterColors.STRING));
  public static TextAttributesKey BAD_CHARACTER = createTextAttributesKey(BAD_CHARACTER_ID, defaultFor(HighlighterColors.BAD_CHARACTER));
  public static TextAttributesKey KEYWORD = createTextAttributesKey(KEYWORD_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
  public static TextAttributesKey PROCEDURE = createTextAttributesKey(PROCEDURE_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
  public static TextAttributesKey SPECIAL = createTextAttributesKey(SPECIAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
  public static TextAttributesKey QUOTED_TEXT = createTextAttributesKey(QUOTED_TEXT_ID, brighter(HighlighterColors.TEXT));
  public static TextAttributesKey QUOTED_STRING = createTextAttributesKey(QUOTED_STRING_ID, brighter(SyntaxHighlighterColors.STRING));
  public static TextAttributesKey QUOTED_NUMBER = createTextAttributesKey(QUOTED_NUMBER_ID, brighter(SyntaxHighlighterColors.NUMBER));
  public static TextAttributesKey DOT = createTextAttributesKey(DOT_ID, brighter(SyntaxHighlighterColors.DOT));
  public static TextAttributesKey ABBREVIATION = createTextAttributesKey(ABBREVIATION_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));

  public static TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  static
  {
    newFillMap(ATTRIBUTES, pack(COMMENT),
            SchemeTokens.LINE_COMMENT, SchemeTokens.BLOCK_COMMENT, SchemeTokens.DATUM_COMMENT);
    newFillMap(ATTRIBUTES, pack(NUMBER), SchemeTokens.NUMBER_LITERAL);
    newFillMap(ATTRIBUTES, pack(STRING), SchemeTokens.STRING_LITERAL);
    newFillMap(ATTRIBUTES, pack(BRACE),
            SchemeTokens.LEFT_SQUARE, SchemeTokens.RIGHT_SQUARE, SchemeTokens.LEFT_CURLY, SchemeTokens.RIGHT_CURLY);
    newFillMap(ATTRIBUTES, pack(PAREN), SchemeTokens.LEFT_PAREN, SchemeTokens.RIGHT_PAREN);
    newFillMap(ATTRIBUTES, pack(CHAR), SchemeTokens.CHAR_LITERAL);
    newFillMap(ATTRIBUTES, pack(SPECIAL), SchemeTokens.SPECIAL);
    newFillMap(ATTRIBUTES, pack(KEYWORD), SchemeTokens.KEYWORD, SchemeTokens.BOOLEAN_LITERAL);
    newFillMap(ATTRIBUTES, pack(PROCEDURE), SchemeTokens.PROCEDURE);
    newFillMap(ATTRIBUTES, pack(DOT), SchemeTokens.DOT);
    newFillMap(ATTRIBUTES, pack(ABBREVIATION),
            SchemeTokens.QUOTE, SchemeTokens.QUASIQUOTE, SchemeTokens.UNQUOTE, SchemeTokens.UNQUOTE_SPLICING,
            SchemeTokens.SYNTAX, SchemeTokens.QUASISYNTAX, SchemeTokens.UNSYNTAX, SchemeTokens.UNSYNTAX_SPLICING);
  }

  protected static void newFillMap(@NotNull Map<IElementType, TextAttributesKey[]> map, TextAttributesKey[] value, @NotNull TokenSet keys) {
    newFillMap(map, value, keys.getTypes());
  }

  protected static void newFillMap(@NotNull Map<IElementType, TextAttributesKey[]> map, TextAttributesKey[] value, @NotNull IElementType... types) {
    IElementType[] var3 = types;
    int var4 = types.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      IElementType type = var3[var5];
      map.put(type, value);
    }
  }

  private static TextAttributes defaultFor(TextAttributesKey key)
  {
    return key.getDefaultAttributes();
  }

  private static TextAttributes brighter(TextAttributesKey key)
  {
    TextAttributes attributes = key.getDefaultAttributes().clone();
    Color foregroundColor = attributes.getForegroundColor();
    if (foregroundColor != null)
    {
      attributes.setForegroundColor(foregroundColor.brighter());
    }
    else
    {
      attributes.setForegroundColor(Color.darkGray);
    }
    return attributes;
  }

//  private static void setBrighter(TextAttributesKey key)
//  {
//    TextAttributes attributes = key.getDefaultAttributes();
//    Color foregroundColor = attributes.getForegroundColor();
//    if (foregroundColor != null)
//    {
//      attributes.setForegroundColor(foregroundColor.brighter());
//    }
//    else
//    {
//      attributes.setForegroundColor(JBColor.DARK_GRAY);
//    }
//  }
}
