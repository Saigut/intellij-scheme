package schemely.scheme.kawa.impl;

import gnu.expr.Language;
import kawa.Shell;
import kawa.standard.Scheme;

/**
 * @author Colin Fleming
 */
public class KawaREPLMain
{
  public static void main(String[] args)
  {
    Scheme scheme = new Scheme();
    Language.setCurrentLanguage(scheme);
    Language.setDefaults(scheme);
    boolean ok = Shell.run(scheme, scheme.getEnvironment());
    if (!ok)
    {
      System.exit(-1);
    }
  }
}
