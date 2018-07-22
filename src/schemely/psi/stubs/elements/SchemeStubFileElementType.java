package schemely.psi.stubs.elements;

import com.intellij.psi.StubBuilder;
import com.intellij.psi.stubs.*;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.io.StringRef;
import schemely.file.SchemeFileType;
import schemely.psi.stubs.SchemeFileStubBuilder;
import schemely.psi.stubs.api.SchemeFileStub;
import schemely.psi.stubs.impl.SchemeFileStubImpl;
import schemely.psi.stubs.index.SchemeClassNameIndex;
import schemely.psi.stubs.index.SchemeFullScriptNameIndex;

import java.io.IOException;


public class SchemeStubFileElementType extends IStubFileElementType<SchemeFileStub>
{
  private static final int CACHES_VERSION = 10;

  public SchemeStubFileElementType()
  {
    super(SchemeFileType.SCHEME_LANGUAGE);
  }

  public StubBuilder getBuilder()
  {
    return new SchemeFileStubBuilder();
  }

  @Override
  public int getStubVersion()
  {
    return super.getStubVersion() + CACHES_VERSION;
  }

  public String getExternalId()
  {
    return "scheme.FILE";
  }

  @Override
  public void indexStub(PsiFileStub stub, IndexSink sink)
  {
    super.indexStub(stub, sink);
  }

  @Override
  public void serialize(SchemeFileStub stub, StubOutputStream dataStream) throws IOException
  {
    dataStream.writeName(stub.getPackageName().toString());
    dataStream.writeName(stub.getName().toString());
    dataStream.writeBoolean(stub.isClassDefinition());
  }

  @Override
  public SchemeFileStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
  {
    StringRef packName = dataStream.readName();
    StringRef name = dataStream.readName();
    boolean isScript = dataStream.readBoolean();
    return new SchemeFileStubImpl(packName, name, isScript);
  }

  public void indexStub(SchemeFileStub stub, IndexSink sink)
  {
    String name = stub.getName().toString();
    if (stub.isClassDefinition() && name != null)
    {
      sink.occurrence(SchemeClassNameIndex.KEY, name);
      String pName = stub.getPackageName().toString();
      String fqn = pName == null || pName.length() == 0 ? name : pName + "." + name;
      sink.occurrence(SchemeFullScriptNameIndex.KEY, fqn.hashCode());
    }
  }
}
