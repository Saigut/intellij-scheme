package icons;

import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface SchemeIcons {
	@NotNull Icon SCHEME_ICON = IconLoader.getIcon("/icons/lambda.png");
	@NotNull Icon FUNCTION = IconLoader.getIcon("/icons/function.png");
	@NotNull Icon METHOD = IconLoader.getIcon("/icons/method.png");
	@NotNull Icon JAVA_METHOD = IconLoader.getIcon("/icons/java_method.png");
	@NotNull Icon JAVA_FIELD = IconLoader.getIcon("/icons/java_field.png");
	@NotNull Icon SYMBOL = IconLoader.getIcon("/icons/symbol.png");
	@NotNull Icon NAMESPACE = IconLoader.getIcon("/icons/namespace.png");
}
