package schemely.scheme.sisc;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Colin Fleming
 */
public class StreamTest extends TestCase
{
  public static final Charset charset = Charset.defaultCharset();
  public static final String[] lines = new String[]{"`Twas brillig, and the slithy toves\n",
                                                    "  Did gyre and gimble in the wabe:\n",
                                                    "All mimsy were the borogoves,\n",
                                                    "  And the mome raths outgrabe."};
  public static final String text = lines[0] + lines[1] + lines[2] + lines[3];
  public static final byte[] bytes = text.getBytes(charset);


  // TODO test block methods
  public void testInputStream() throws IOException
  {
    SISCInProcessREPL.ToReplInputStream inputStream = new SISCInProcessREPL.ToReplInputStream(charset);

    for (String line : lines)
    {
      inputStream.enqueue(line);
    }
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    while (output.size() < bytes.length)
    {
      output.write(inputStream.read());
    }

    byte[] outputBytes = output.toByteArray();
    assertTrue(Arrays.equals(bytes, outputBytes));
  }

  // TODO test block methods
  public void testOutputStream() throws IOException
  {
    final AtomicInteger cursor = new AtomicInteger(0);

    SISCInProcessREPL.FromREPLOutputStream outputStream = new SISCInProcessREPL.FromREPLOutputStream(charset)
    {
      @Override
      protected void textAvailable(String text)
      {
        assertEquals(lines[cursor.getAndIncrement()], text);
      }

      public void check()
      {
      }
    };

    for (byte b : bytes)
    {
      outputStream.write(b);
    }
    outputStream.flush();

    assertEquals(lines.length, cursor.get());
  }
}
