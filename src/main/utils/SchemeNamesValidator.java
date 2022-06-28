package main.utils;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SchemeNamesValidator implements NamesValidator {
    @Override
    public boolean isKeyword(@NotNull final String name, final Project project) {
        return false;
    }

    @Override
    public boolean isIdentifier(@NotNull final String name, final Project project) {
        return true;
    }
}
