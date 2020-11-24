package com.plugins.mygetset.selecttext;

import com.intellij.codeInsight.editorActions.SelectWordUtil;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public final class SelectWordUtilCompat {

    public static final int BUILD_NUMBER = ApplicationInfo.getInstance().getBuild().getBaselineVersion();

    public static void addWordOrLexemeSelection(boolean camel,
                                                @NotNull Editor editor,
                                                int cursorOffset,
                                                @NotNull List<TextRange> ranges,
                                                @NotNull SelectWordUtil.CharCondition isWordPartCondition) {
        final int IDEA2016_2 = 162;
        if (BUILD_NUMBER >= IDEA2016_2) {
            SelectWordUtil.addWordOrLexemeSelection(camel, editor, cursorOffset, ranges, isWordPartCondition);
        } else {
            CharSequence editorText = editor.getDocument().getImmutableCharSequence();
            SelectWordUtil.addWordSelection(camel, editorText, cursorOffset, ranges, isWordPartCondition);
        }
    }

}
