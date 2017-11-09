package schemely.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import schemely.lexer.SchemeLexer;
import schemely.lexer.Tokens;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class SchemeSyntaxHighlighter extends SyntaxHighlighterBase implements Tokens
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

  @NonNls
  static final String COMMENT_ID = "Scheme Comment";
  @NonNls
  static final String BLOCK_COMMENT_ID = "Scheme Block Comment";
  @NonNls
  static final String IDENTIFIER_ID = "Identifier";
  @NonNls
  static final String NUMBER_ID = "Scheme Numbers";
  @NonNls
  static final String STRING_ID = "Scheme Strings";
  @NonNls
  static final String BAD_CHARACTER_ID = "Bad character";
  @NonNls
  static final String BRACES_ID = "Scheme Braces";
  @NonNls
  static final String PAREN_ID = "Scheme Parentheses";
  @NonNls
  static final String LITERAL_ID = "Scheme Literal";
  @NonNls
  static final String CHAR_ID = "Scheme Character";
  @NonNls
  static final String KEYWORD_ID = "Keyword";
  @NonNls
  static final String SPECIAL_ID = "Special";

  @NonNls
  static final String QUOTED_TEXT_ID = "Quoted text";
  @NonNls
  static final String QUOTED_STRING_ID = "Quoted string";
  @NonNls
  static final String QUOTED_NUMBER_ID = "Quoted number";

  @NonNls
  static final String DOT_ID = "Dot";
  @NonNls
  static final String COMMA_ID = "Comma";


  // Registering TextAttributes
  static
  {
    createTextAttributesKey(COMMENT_ID, defaultFor(SyntaxHighlighterColors.LINE_COMMENT));
    createTextAttributesKey(BLOCK_COMMENT_ID, defaultFor(SyntaxHighlighterColors.JAVA_BLOCK_COMMENT));
    createTextAttributesKey(IDENTIFIER_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(NUMBER_ID, defaultFor(SyntaxHighlighterColors.NUMBER));
    createTextAttributesKey(STRING_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BRACES_ID, defaultFor(SyntaxHighlighterColors.BRACES));
    createTextAttributesKey(PAREN_ID, defaultFor(SyntaxHighlighterColors.PARENTHS));
    createTextAttributesKey(LITERAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(CHAR_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BAD_CHARACTER_ID, defaultFor(HighlighterColors.BAD_CHARACTER));
    createTextAttributesKey(KEYWORD_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(SPECIAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(QUOTED_TEXT_ID, brighter(HighlighterColors.TEXT));
    createTextAttributesKey(QUOTED_STRING_ID, brighter(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(QUOTED_NUMBER_ID, brighter(SyntaxHighlighterColors.NUMBER));
    createTextAttributesKey(DOT_ID, brighter(SyntaxHighlighterColors.DOT));
    createTextAttributesKey(COMMA_ID, brighter(SyntaxHighlighterColors.COMMA));
  }

  public static TextAttributesKey LINE_COMMENT = createTextAttributesKey(COMMENT_ID);
  public static TextAttributesKey BLOCK_COMMENT = createTextAttributesKey(BLOCK_COMMENT_ID);
  public static TextAttributesKey IDENTIFIER = createTextAttributesKey(IDENTIFIER_ID);
  public static TextAttributesKey NUMBER = createTextAttributesKey(NUMBER_ID);
  public static TextAttributesKey STRING = createTextAttributesKey(STRING_ID);
  public static TextAttributesKey BRACE = createTextAttributesKey(BRACES_ID);
  public static TextAttributesKey PAREN = createTextAttributesKey(PAREN_ID);
  public static TextAttributesKey LITERAL = createTextAttributesKey(LITERAL_ID);
  public static TextAttributesKey CHAR = createTextAttributesKey(CHAR_ID);
  public static TextAttributesKey BAD_CHARACTER = createTextAttributesKey(BAD_CHARACTER_ID);
  public static TextAttributesKey KEYWORD = createTextAttributesKey(KEYWORD_ID);
  public static TextAttributesKey SPECIAL = createTextAttributesKey(SPECIAL_ID);
  public static TextAttributesKey QUOTED_TEXT = createTextAttributesKey(QUOTED_TEXT_ID);
  public static TextAttributesKey QUOTED_STRING = createTextAttributesKey(QUOTED_STRING_ID);
  public static TextAttributesKey QUOTED_NUMBER = createTextAttributesKey(QUOTED_NUMBER_ID);
  public static TextAttributesKey DOT = createTextAttributesKey(DOT_ID);
  public static TextAttributesKey COMMA = createTextAttributesKey(COMMA_ID);

  public static TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{createTextAttributesKey(COMMENT_ID)};
  public static TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{createTextAttributesKey(BLOCK_COMMENT_ID)};
  public static TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{createTextAttributesKey(IDENTIFIER_ID)};
  public static TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{createTextAttributesKey(NUMBER_ID)};
  public static TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{createTextAttributesKey(STRING_ID)};
  public static TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{createTextAttributesKey(BRACES_ID)};
  public static TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{createTextAttributesKey(PAREN_ID)};
  public static TextAttributesKey[] LITERAL_KEYS = new TextAttributesKey[]{createTextAttributesKey(LITERAL_ID)};
  public static TextAttributesKey[] CHAR_KEYS = new TextAttributesKey[]{createTextAttributesKey(CHAR_ID)};
  public static TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{createTextAttributesKey(BAD_CHARACTER_ID)};
  public static TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{createTextAttributesKey(KEYWORD_ID)};
  public static TextAttributesKey[] SPECIAL_KEYS = new TextAttributesKey[]{createTextAttributesKey(SPECIAL_ID)};
  public static TextAttributesKey[] QUOTED_TEXT_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_TEXT_ID)};
  public static TextAttributesKey[] QUOTED_STRING_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_STRING_ID)};
  public static TextAttributesKey[] QUOTED_NUMBER_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_NUMBER_ID)};
  public static TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{createTextAttributesKey(DOT_ID)};
  public static TextAttributesKey[] COMMA_KEYS = new TextAttributesKey[]{createTextAttributesKey(COMMA_ID)};
  public static TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  static
  {
    newFillMap(ATTRIBUTES, LINE_COMMENT_KEYS, Tokens.COMMENT);
    newFillMap(ATTRIBUTES, BLOCK_COMMENT_KEYS, Tokens.BLOCK_COMMENT);
    newFillMap(ATTRIBUTES, NUMBER_KEYS, Tokens.NUMBER_LITERAL);
    newFillMap(ATTRIBUTES, STRING_KEYS, Tokens.STRING_LITERAL);
    newFillMap(ATTRIBUTES, BRACE_KEYS, Tokens.LEFT_SQUARE, Tokens.RIGHT_SQUARE, Tokens.LEFT_CURLY, Tokens.RIGHT_CURLY);
    newFillMap(ATTRIBUTES, PAREN_KEYS, Tokens.LEFT_PAREN, Tokens.RIGHT_PAREN);
//    newFillMap(ATTRIBUTES, LITERAL_KEYS, PLAIN_LITERAL, Tokens.BOOLEAN_LITERAL);
    newFillMap(ATTRIBUTES, CHAR_KEYS, Tokens.CHAR_LITERAL);
    newFillMap(ATTRIBUTES, SPECIAL_KEYS, Tokens.SPECIAL);
    newFillMap(ATTRIBUTES, IDENTIFIER_KEYS, Tokens.IDENTIFIERS);
    newFillMap(ATTRIBUTES, KEYWORD_KEYS, Tokens.KEYWORD);
    newFillMap(ATTRIBUTES, DOT_KEYS, Tokens.DOT);
    newFillMap(ATTRIBUTES, COMMA_KEYS, Tokens.COMMA, Tokens.COMMA_AT);
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
}
