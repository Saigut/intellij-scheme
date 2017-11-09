package schemely.scheme.sisc;

import junit.framework.TestCase;

import static schemely.scheme.sisc.SISCOutputProcessor.looksLikeError;

/**
 * @author Colin Fleming
 */
public class SISCOutputProcessorTest extends TestCase
{
  public void testErrors()
  {
    assertTrue(looksLikeError("Error.\n"));
    assertTrue(looksLikeError("Error. \n"));
    assertTrue(looksLikeError("Error.\t\n"));
    assertTrue(looksLikeError("Error in some location.\t\n"));
    assertTrue(looksLikeError("Error in some.location.with.periods.\t\n"));
    assertTrue(looksLikeError("Error: some message\n"));
    assertTrue(looksLikeError("Error in some location: some message\n"));
    assertTrue(looksLikeError("Error in nested call.\n"));
    assertTrue(looksLikeError("Error in nested call: some message\n"));
    assertTrue(looksLikeError("Error in nested call from some location.\n"));
    assertTrue(looksLikeError("Error in nested call from some.location.with.periods.\n"));
    assertTrue(looksLikeError("Error in nested call from some location: some message\n"));
    assertTrue(looksLikeError("Error in nested call from some.location.with.periods: some message\n"));
  }

}
