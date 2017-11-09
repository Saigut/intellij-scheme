package schemely;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.*;

public interface SchemeIcons
{
  @NonNls
  final Icon SCHEME_ICON = IconLoader.findIcon("/schemely/icons/lambda.png");

  final Icon FUNCTION = IconLoader.findIcon("/schemely/icons/function.png");
  final Icon METHOD = IconLoader.findIcon("/schemely/icons/method.png");
  final Icon JAVA_METHOD = IconLoader.findIcon("/schemely/icons/java_method.png");
  final Icon JAVA_FIELD = IconLoader.findIcon("/schemely/icons/java_field.png");
  final Icon SYMBOL = IconLoader.findIcon("/schemely/icons/symbol.png");
  final Icon NAMESPACE = IconLoader.findIcon("/schemely/icons/namespace.png");
}
