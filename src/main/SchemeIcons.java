package main;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;

import javax.swing.Icon;

public interface SchemeIcons
{
  @NonNls
  Icon SCHEME_ICON = IconLoader.findIcon("/icons/lambda.png");

  Icon SYMBOL = IconLoader.findIcon("/icons/symbol.png");

  Icon VARIABLE = AllIcons.Nodes.Variable;
  Icon FUNCTION = AllIcons.Nodes.Function;
  Icon PARAMETER = AllIcons.Nodes.Parameter;
  Icon LAMBDA = AllIcons.Nodes.Lambda;
}
