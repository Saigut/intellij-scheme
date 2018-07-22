package schemely.completion;

import junit.framework.TestCase;

/**
 * @author Colin Fleming
 */
public class SchemeIdentifierMatcherTest extends TestCase
{
  public void testBasicMatching()
  {
    match("str", "string");
    match("STR", "string");
    match("string", "string");
    match("str-app", "string-append");
    match("c-w-c-c", "call-with-current-continuation");
    match("*-append", "string-append");
    doesntMatch("strings", "string");
    doesntMatch("strings", "string-append");
    doesntMatch("crud-w-c-c", "call-with-current-continuation");
  }

  public void match(String prefix, String test)
  {
    match(prefix, test, false);
  }

  public void match(String prefix, String test, boolean caseSensitive)
  {
    assertTrue(new SchemeIdentifierMatcher(prefix, caseSensitive).prefixMatches(test));
  }

  public void doesntMatch(String prefix, String test)
  {
    doesntMatch(prefix, test, false);
  }

  public void doesntMatch(String prefix, String test, boolean caseSensitive)
  {
    assertFalse(new SchemeIdentifierMatcher(prefix, caseSensitive).prefixMatches(test));
  }
}
