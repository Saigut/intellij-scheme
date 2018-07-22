package schemely;

/**
 * @author Colin Fleming
 */
public class TestUtil
{
  public static <T> Object[][] testArray(T... items)
  {
    Object[][] ret = new Object[items.length][];
    for (int i = 0; i < items.length; i++)
    {
      ret[i] = new Object[] { items[i] };
    }
    return ret;
  }
}
