package main.highlighter;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
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

  @NonNls
  public static final String COMMENT_ID = "Scheme Comment";
//  @NonNls
//  public static final String BLOCK_COMMENT_ID = "Scheme Block Comment";
//  @NonNls
//  public static final String DATUM_COMMENT_ID = "Scheme Datum Comment";
  @NonNls
  public static final String IDENTIFIER_ID = "Identifier";
  @NonNls
  public static final String NUMBER_ID = "Scheme Numbers";
  @NonNls
  public static final String STRING_ID = "Scheme Strings";
  @NonNls
  public static final String BAD_CHARACTER_ID = "Bad character";
  @NonNls
  public static final String BRACES_ID = "Scheme Braces";
  @NonNls
  public static final String PAREN_ID = "Scheme Parentheses";
  @NonNls
  public static final String LITERAL_ID = "Scheme Literal";
  @NonNls
  public static final String CHAR_ID = "Scheme Character";
  @NonNls
  public static final String KEYWORD_ID = "Keyword";
  @NonNls
  public static final String PROCEDURE_ID = "Procedure";
  @NonNls
  public static final String SPECIAL_ID = "Special";

  @NonNls
  public static final String QUOTED_TEXT_ID = "Quoted text";
  @NonNls
  public static final String QUOTED_STRING_ID = "Quoted string";
  @NonNls
  public static final String QUOTED_NUMBER_ID = "Quoted number";

  @NonNls
  public static final String DOT_ID = "Dot";
  @NonNls
  public static final String ABBREVIATION_ID = "Abbreviation";


  // Registering TextAttributes
  static
  {
    createTextAttributesKey(COMMENT_ID, defaultFor(SyntaxHighlighterColors.LINE_COMMENT));
//    createTextAttributesKey(BLOCK_COMMENT_ID, DefaultLanguageHighlighterColors.BLOCK_COMMENT);
//    createTextAttributesKey(DATUM_COMMENT_ID, DefaultLanguageHighlighterColors.LINE_COMMENT);
    createTextAttributesKey(NUMBER_ID, defaultFor(SyntaxHighlighterColors.NUMBER));
    createTextAttributesKey(STRING_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BRACES_ID, defaultFor(SyntaxHighlighterColors.BRACES));
    createTextAttributesKey(PAREN_ID, defaultFor(SyntaxHighlighterColors.PARENTHS));
    createTextAttributesKey(LITERAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(CHAR_ID, defaultFor(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(BAD_CHARACTER_ID, defaultFor(HighlighterColors.BAD_CHARACTER));
    createTextAttributesKey(KEYWORD_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(PROCEDURE_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(SPECIAL_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));

    createTextAttributesKey(IDENTIFIER_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
    createTextAttributesKey(QUOTED_TEXT_ID, brighter(HighlighterColors.TEXT));
    createTextAttributesKey(QUOTED_STRING_ID, brighter(SyntaxHighlighterColors.STRING));
    createTextAttributesKey(QUOTED_NUMBER_ID, brighter(SyntaxHighlighterColors.NUMBER));
    createTextAttributesKey(DOT_ID, brighter(SyntaxHighlighterColors.DOT));
    createTextAttributesKey(ABBREVIATION_ID, defaultFor(SyntaxHighlighterColors.KEYWORD));
  }

  public static TextAttributesKey COMMENT = createTextAttributesKey(COMMENT_ID);
//  public static TextAttributesKey BLOCK_COMMENT = createTextAttributesKey(BLOCK_COMMENT_ID);
//  public static TextAttributesKey DATUM_COMMENT = createTextAttributesKey(DATUM_COMMENT_ID);
  public static TextAttributesKey IDENTIFIER = createTextAttributesKey(IDENTIFIER_ID);
  public static TextAttributesKey NUMBER = createTextAttributesKey(NUMBER_ID);
  public static TextAttributesKey STRING = createTextAttributesKey(STRING_ID);
  public static TextAttributesKey BRACE = createTextAttributesKey(BRACES_ID);
  public static TextAttributesKey PAREN = createTextAttributesKey(PAREN_ID);
  public static TextAttributesKey LITERAL = createTextAttributesKey(LITERAL_ID);
  public static TextAttributesKey CHAR = createTextAttributesKey(CHAR_ID);
  public static TextAttributesKey BAD_CHARACTER = createTextAttributesKey(BAD_CHARACTER_ID);
  public static TextAttributesKey KEYWORD = createTextAttributesKey(KEYWORD_ID);
  public static TextAttributesKey PROCEDURE = createTextAttributesKey(PROCEDURE_ID);
  public static TextAttributesKey SPECIAL = createTextAttributesKey(SPECIAL_ID);
  public static TextAttributesKey QUOTED_TEXT = createTextAttributesKey(QUOTED_TEXT_ID);
  public static TextAttributesKey QUOTED_STRING = createTextAttributesKey(QUOTED_STRING_ID);
  public static TextAttributesKey QUOTED_NUMBER = createTextAttributesKey(QUOTED_NUMBER_ID);
  public static TextAttributesKey DOT = createTextAttributesKey(DOT_ID);
  public static TextAttributesKey ABBREVIATION = createTextAttributesKey(ABBREVIATION_ID);

  public static TextAttributesKey[] LINE_COMMENT_KEYS = new TextAttributesKey[]{createTextAttributesKey(COMMENT_ID)};
//  public static TextAttributesKey[] BLOCK_COMMENT_KEYS = new TextAttributesKey[]{createTextAttributesKey(BLOCK_COMMENT_ID)};
//  public static TextAttributesKey[] DATUM_COMMENT_KEYS = new TextAttributesKey[]{createTextAttributesKey(DATUM_COMMENT_ID)};
  public static TextAttributesKey[] IDENTIFIER_KEYS = new TextAttributesKey[]{createTextAttributesKey(IDENTIFIER_ID)};
  public static TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{createTextAttributesKey(NUMBER_ID)};
  public static TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{createTextAttributesKey(STRING_ID)};
  public static TextAttributesKey[] BRACE_KEYS = new TextAttributesKey[]{createTextAttributesKey(BRACES_ID)};
  public static TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{createTextAttributesKey(PAREN_ID)};
  public static TextAttributesKey[] LITERAL_KEYS = new TextAttributesKey[]{createTextAttributesKey(LITERAL_ID)};
  public static TextAttributesKey[] CHAR_KEYS = new TextAttributesKey[]{createTextAttributesKey(CHAR_ID)};
  public static TextAttributesKey[] BAD_CHARACTER_KEYS = new TextAttributesKey[]{createTextAttributesKey(BAD_CHARACTER_ID)};
  public static TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{createTextAttributesKey(KEYWORD_ID)};
  public static TextAttributesKey[] PROCEDURE_KEYS = new TextAttributesKey[]{createTextAttributesKey(PROCEDURE_ID)};
  public static TextAttributesKey[] SPECIAL_KEYS = new TextAttributesKey[]{createTextAttributesKey(SPECIAL_ID)};
  public static TextAttributesKey[] QUOTED_TEXT_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_TEXT_ID)};
  public static TextAttributesKey[] QUOTED_STRING_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_STRING_ID)};
  public static TextAttributesKey[] QUOTED_NUMBER_KEYS = new TextAttributesKey[]{createTextAttributesKey(QUOTED_NUMBER_ID)};
  public static TextAttributesKey[] DOT_KEYS = new TextAttributesKey[]{createTextAttributesKey(DOT_ID)};
  public static TextAttributesKey[] ABBREVIATION_KEYS = new TextAttributesKey[]{createTextAttributesKey(ABBREVIATION_ID)};
  public static TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  static
  {
    newFillMap(ATTRIBUTES, LINE_COMMENT_KEYS,
            SchemeTokens.LINE_COMMENT, SchemeTokens.BLOCK_COMMENT, SchemeTokens.DATUM_COMMENT);
//    newFillMap(ATTRIBUTES, BLOCK_COMMENT_KEYS, SchemeTokens.BLOCK_COMMENT);
//    newFillMap(ATTRIBUTES, DATUM_COMMENT_KEYS, SchemeTokens.DATUM_COMMENT);
    newFillMap(ATTRIBUTES, NUMBER_KEYS, SchemeTokens.NUMBER_LITERAL);
    newFillMap(ATTRIBUTES, STRING_KEYS, SchemeTokens.STRING_LITERAL);
    newFillMap(ATTRIBUTES, BRACE_KEYS, SchemeTokens.LEFT_SQUARE, SchemeTokens.RIGHT_SQUARE, SchemeTokens.LEFT_CURLY, SchemeTokens.RIGHT_CURLY);
    newFillMap(ATTRIBUTES, PAREN_KEYS, SchemeTokens.LEFT_PAREN, SchemeTokens.RIGHT_PAREN);
//    newFillMap(ATTRIBUTES, LITERAL_KEYS, AST_PLAIN_LITERAL, Tokens.BOOLEAN_LITERAL);
    newFillMap(ATTRIBUTES, CHAR_KEYS, SchemeTokens.CHAR_LITERAL);
    newFillMap(ATTRIBUTES, SPECIAL_KEYS, SchemeTokens.SPECIAL);
//    newFillMap(ATTRIBUTES, IDENTIFIER_KEYS, SchemeTokens.IDENTIFIERS);
    newFillMap(ATTRIBUTES, KEYWORD_KEYS, SchemeTokens.KEYWORD, SchemeTokens.BOOLEAN_LITERAL);
    newFillMap(ATTRIBUTES, PROCEDURE_KEYS, SchemeTokens.PROCEDURE);
//    newFillMap(ATTRIBUTES, BAD_CHARACTER_KEYS, SchemeTokens.BAD_CHARACTER);
    newFillMap(ATTRIBUTES, DOT_KEYS, SchemeTokens.DOT);
    newFillMap(ATTRIBUTES, ABBREVIATION_KEYS,
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
