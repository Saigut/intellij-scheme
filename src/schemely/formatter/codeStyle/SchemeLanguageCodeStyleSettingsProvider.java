package schemely.formatter.codeStyle;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.psi.codeStyle.*;
import org.jetbrains.annotations.NotNull;
import schemely.SchemeLanguage;


public class SchemeLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

    @NotNull
    @Override
    public Language getLanguage() {
        return SchemeLanguage.INSTANCE;
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_AROUND_ASSIGNMENT_OPERATORS");
            consumer.renameStandardOption("SPACE_AROUND_ASSIGNMENT_OPERATORS", "Separator");
        } else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("KEEP_BLANK_LINES_IN_CODE");
        }
    }

    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings defaultSettings = new CommonCodeStyleSettings(SchemeLanguage.INSTANCE);
        defaultSettings.initIndentOptions();
        CommonCodeStyleSettings.IndentOptions indentOptions = defaultSettings.getIndentOptions();
        if (null != indentOptions) {
            indentOptions.INDENT_SIZE = 2;
            indentOptions.TAB_SIZE = 2;
            indentOptions.CONTINUATION_INDENT_SIZE = 4;
        }
        return defaultSettings;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return "(f f)\n" +
                "\n" +
                "((lambda (f) x) (lambda (f) x))\n" +
                "\n" +
                "(define Y (lambda (f) (f f)))\n" +
                "\n" +
                "((lambda (f) (lambda (x) ((f f) (g x))))\n" +
                " (lambda (f) (lambda (x) ((f f) (g x)))))\n" +
                " \n" +
                "((lambda (g)\n" +
                " ((lambda (f) (lambda (x) ((f f) (g x))))\n" +
                "  (lambda (f) (lambda (x) ((f f) (g x))))))\n" +
                "cdr)\n" +
                "\n" +
                "((lambda (g)\n" +
                " ((lambda (f) (f f))\n" +
                "  (lambda (f) (lambda (x) ((f f) (g x))))))\n" +
                "g)";
    }

    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }
}
