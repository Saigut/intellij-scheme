package schemely.lexer;

import com.intellij.psi.tree.IElementType;
//import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Colin Fleming
 */
public class LexerTestBase
{
  protected static LexerTestCase testCase(String testData, IElementType... types)
  {
    return new LexerTestCase(testData, types);
  }

//  @Test(dataProvider = "lexerTests", groups = "Schemely")
  public void testLexer(LexerTestCase testCase)
  {
    SchemeLexer lexer = getLexer();
    lexer.start(testCase.testData);

    for (int i = 0; i < testCase.types.length; i++)
    {
      IElementType expected = testCase.types[i];

      assert expected.equals(lexer.getTokenType()) : "Expected " + expected + ", got " + lexer.getTokenType();
      lexer.advance();
    }

    assert lexer.getTokenType() == null : "Expected final null, got " + lexer.getTokenType();
  }

  protected SchemeLexer getLexer()
  {
    return new SchemeLexer();
  }

  protected static class LexerTestCase
  {
    protected final String testData;
    protected final IElementType[] types;

    private LexerTestCase(String testData, IElementType... types)
    {
      this.testData = testData;
      this.types = types;
    }

    @Override
    public String toString()
    {
      return "LexerTestCase{" + testData + " -> " + (types == null ? null : Arrays.asList(types)) + '}';
    }
  }
}
