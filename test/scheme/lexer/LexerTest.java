package schemely.lexer;

//import org.testng.annotations.DataProvider;
import schemely.TestUtil;

import static schemely.lexer.Tokens.*;

/**
 * @author Colin Fleming
 */
public class LexerTest extends LexerTestBase
{
//  @DataProvider(name = "lexerTests")
  private static Object[][] lexerTestCases()
  {
    return TestUtil.testArray(testCase("(", LEFT_PAREN),
                              testCase("[", LEFT_SQUARE),
                              testCase("{", LEFT_CURLY),
                              testCase(")", RIGHT_PAREN),
                              testCase("]", RIGHT_SQUARE),
                              testCase("}", RIGHT_CURLY),
                              testCase("#(", OPEN_VECTOR),

                              testCase("#t", BOOLEAN_LITERAL),
                              testCase("#f", BOOLEAN_LITERAL),
                              testCase("#T", BOOLEAN_LITERAL),
                              testCase("#F", BOOLEAN_LITERAL),

                              testCase("'", QUOTE_MARK),
                              testCase("`", BACKQUOTE),
                              testCase(",", COMMA),
                              testCase(",@", COMMA_AT),

                              testCase(";", COMMENT),
                              testCase("; comment", COMMENT),
                              testCase("; comment\n", COMMENT, WHITESPACE),
                              testCase("; comment\r\n", COMMENT, WHITESPACE),
                              testCase("; comment\r", COMMENT, WHITESPACE),

                              testCase("#||#", BLOCK_COMMENT),
                              testCase("#| |#", BLOCK_COMMENT),
                              testCase("#| \n |#", BLOCK_COMMENT),
                              testCase("#| comment |#", BLOCK_COMMENT),
                              testCase("#| #| comment |# |#", BLOCK_COMMENT),

                              testCase("\"string\"", STRING_LITERAL),
                              testCase("\"\"", STRING_LITERAL),
                              testCase("\"string", STRING_LITERAL),
                              testCase("\"str\\\"ng\"", STRING_LITERAL),
                              testCase("\"str\\$ng\"", STRING_LITERAL),

                              testCase("#\\newline", CHAR_LITERAL),
                              testCase("#\\space", CHAR_LITERAL),
                              testCase("#\\a", CHAR_LITERAL),

                              testCase("a", IDENTIFIER),
                              testCase("-", IDENTIFIER),
                              testCase("+", IDENTIFIER),
                              testCase("...", IDENTIFIER),
                              testCase("a-b", IDENTIFIER),
                              testCase("a.b", IDENTIFIER),

                              // Numbers
                              testCase("1", NUMBER_LITERAL),
                              testCase(".5", NUMBER_LITERAL),
                              testCase("1.5", NUMBER_LITERAL),
                              testCase("-17", NUMBER_LITERAL),
                              testCase("1/2", NUMBER_LITERAL),
                              testCase("-3/4", NUMBER_LITERAL),
                              testCase("8+0.6i", NUMBER_LITERAL),
                              testCase("8+0.6I", NUMBER_LITERAL),
                              testCase("1+2i", NUMBER_LITERAL),
                              testCase("3/4+1/2i", NUMBER_LITERAL),
                              testCase("2.0+0.3i", NUMBER_LITERAL),
                              testCase("#e0.5", NUMBER_LITERAL),
                              testCase("#E0.5", NUMBER_LITERAL),
                              testCase("#x03bb", NUMBER_LITERAL),
                              testCase("#X03bb", NUMBER_LITERAL),
                              testCase("#X03BB", NUMBER_LITERAL),
                              testCase("#e1e10", NUMBER_LITERAL),
                              testCase("8.0@6.0", NUMBER_LITERAL),
                              testCase("8@6", NUMBER_LITERAL),
                              testCase("-0i", NUMBER_LITERAL),
                              testCase("-0.i", NUMBER_LITERAL),
                              testCase("+1i", NUMBER_LITERAL),
                              testCase("8+6e20i", NUMBER_LITERAL),
                              testCase("8e10+6i", NUMBER_LITERAL),
                              testCase("#d-0e-10-0e-0i", NUMBER_LITERAL),
                              testCase("#D-0E-10-0E-0I", NUMBER_LITERAL),
                              testCase("#d#e-0.0f-0-.0s-0i", NUMBER_LITERAL),
                              testCase("1f2", NUMBER_LITERAL),
                              testCase("0@-.0", NUMBER_LITERAL),
                              testCase("999999999999999999999", NUMBER_LITERAL),

                              testCase("(define x 3)",
                                       LEFT_PAREN,
                                       IDENTIFIER,
                                       WHITESPACE,
                                       IDENTIFIER,
                                       WHITESPACE,
                                       NUMBER_LITERAL,
                                       RIGHT_PAREN),

                              testCase("(gen-java0 #\\; #\\newline)",
                                       LEFT_PAREN,
                                       IDENTIFIER,
                                       WHITESPACE,
                                       CHAR_LITERAL,
                                       WHITESPACE,
                                       CHAR_LITERAL,
                                       RIGHT_PAREN));
  }
}
