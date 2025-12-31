package main.utils;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for reading resource files.
 */
public class SchemeResourceUtil {

    /**
     * Reads a resource file as a UTF-8 string.
     *
     * @param resourcePath the path to the resource file
     * @return the content of the resource file, or null if not found or error occurred
     */
    @Nullable
    public static String readResourceAsString(String resourcePath) {
        try (InputStream stream = SchemeResourceUtil.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream != null) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] contentBytes = buffer.toByteArray();
                return new String(contentBytes, StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}
