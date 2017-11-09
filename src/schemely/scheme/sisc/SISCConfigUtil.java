package schemely.scheme.sisc;

import com.intellij.openapi.application.PathManager;
import com.intellij.util.PathUtil;
import org.jetbrains.annotations.NonNls;
import sisc.REPL;
import sisc.modules.Types;

import java.io.File;

/**
 * @author Colin Fleming
 */
public class SISCConfigUtil
{
  @NonNls
  public static final String SISC_JAR_NAME_PREFIX = "sisc";
  @NonNls
  public static final String SISC_MAIN_CLASS_FILE = "sisc/REPL.class";
  @NonNls
  public static final String SISC_SDK = PathUtil.getJarPathForClass(REPL.class);
  @NonNls
  public static final String SISC_LIB = getJarPathForResource(REPL.class, "sisc/libs/srfi.scc");
  @NonNls
  public static final String SISC_SRC = getJarPathForResource(REPL.class, "sisc/boot/repl.scm");
  @NonNls
  public static final String SISC_OPT = PathUtil.getJarPathForClass(Types.class);

  public static String getJarPathForResource(Class<?> aClass, String resourceName) {
    String resourceRoot = PathManager.getResourceRoot(aClass, '/' + resourceName);
    return new File(resourceRoot).getAbsoluteFile().getAbsolutePath();
  }
}
