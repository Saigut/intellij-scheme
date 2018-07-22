package schemely.lexer;

//import org.testng.annotations.DataProvider;
import schemely.TestUtil;
import schemely.scheme.sisc.lexer.SISCLexer;
import schemely.scheme.sisc.lexer.SISCTokens;

/**
 * @author Colin Fleming
 */
public class SISCLexerTest extends LexerTestBase
{
//  @DataProvider(name = "lexerTests")
  private static Object[][] lexerTestCases()
  {
    return TestUtil.testArray(testCase("#0=", SISCTokens.PTR_DEF),
                              testCase("#0#", SISCTokens.PTR_REF),
                              testCase("#%let", Tokens.IDENTIFIER),
                              testCase("#0(", Tokens.OPEN_VECTOR),
                              testCase("#'", SISCTokens.SYNTAX_QUOTE),
                              testCase(".token", Tokens.IDENTIFIER),
                              testCase("|token|", Tokens.IDENTIFIER),
                              testCase("->int", Tokens.IDENTIFIER));
  }

  @Override
  protected SchemeLexer getLexer()
  {
    return new SISCLexer();
  }
}
