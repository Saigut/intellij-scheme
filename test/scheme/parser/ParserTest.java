package schemely.parser;

//import org.testng.annotations.DataProvider;
import schemely.TestUtil;
import schemely.psi.impl.SchemeQuoted;
import schemely.psi.impl.SchemeVector;


public class ParserTest extends ParseTestCaseBase
{
//  @DataProvider(name = "parseFiles")
  private Object[][] getParseFiles()
  {
    setUp();
    return TestUtil.testArray(identifier("foo"),
                              identifier("foo*"),
                              literal("123"),
                              literal("123.123"),
                              literal("\"123.456\""),
                              literal("\"this is\n" + "            a multiline\n" + "            string\""),
                              list("(a b)"),
                              list("(a b (c d))"),
                              element("'(a b (c d))", SchemeQuoted.class),
                              element("'a", SchemeQuoted.class),
                              element("#(a b (c d))", SchemeVector.class),
                              list("()"),
                              element("#()", SchemeVector.class),
                              improperList("(a b . c)"));
  }
}
