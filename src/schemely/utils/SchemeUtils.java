package schemely.utils;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;


public class SchemeUtils
{
  public static final String SCHEME_NOTIFICATION_GROUP = "Scheme";

  public static boolean isSuitableModule(Module module)
  {
    if (module == null)
    {
      return false;
    }
//    ModuleType type = module.getModuleType();
//    return type instanceof JavaModuleType || "PLUGIN_MODULE".equals(type.getId());
      return true;
  }
}
