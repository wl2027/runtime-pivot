package com.runtime.pivot.plugin.i18n;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

public class RuntimePivotBundle extends DynamicBundle {

    private static final String BUNDLE = "messages.RuntimePivotBundle";
    private static final RuntimePivotBundle INSTANCE = new RuntimePivotBundle();

    public RuntimePivotBundle() {
        super(BUNDLE);
    }

    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        if (key == null) return "";
        if (params == null) return "";
        if (INSTANCE.getMessage(key, params) == null) return "";
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {
        if (key == null) return null;
        if (params == null) return null;
        if (INSTANCE.getLazyMessage(key, params) == null) return null;
        return INSTANCE.getLazyMessage(key, params);
    }
}
