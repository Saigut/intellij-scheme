package schemely.psi.util;

import org.jetbrains.annotations.NotNull;


public abstract class SchemeTextUtil
{
  public static String getLastSymbolAtom(@NotNull String sym, @NotNull String sep)
  {
    int index = sym.lastIndexOf(sep);
    return index > 0 && index < sym.length() - 1 ? sym.substring(index + 1) : sym;
  }

  public static String getSymbolPrefix(@NotNull String sym, @NotNull String sep)
  {
    int index = sym.lastIndexOf(sep);
    return index > 0 && index < sym.length() - 1 ? sym.substring(0, index) : "";
  }

  public static String getSymbolPrefix(@NotNull String sym)
  {
    return getSymbolPrefix(sym, ".");
  }
}
