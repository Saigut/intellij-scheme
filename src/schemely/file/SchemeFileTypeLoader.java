package schemely.file;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class SchemeFileTypeLoader extends FileTypeFactory
{
  public void createFileTypes(@NotNull FileTypeConsumer consumer)
  {
    consumer.consume(SchemeFileType.SCHEME_FILE_TYPE, SchemeFileType.SCHEME_EXTENSIONS);
  }
}
