package com.runtime.pivot.plugin.utils;

import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;


public class IconsUtil {
    private static @NotNull Icon load(@NotNull String path, int cacheKey, int flags) {
        return IconManager.getInstance().loadRasterizedIcon(path, IconsUtil.class.getClassLoader(), cacheKey, flags);
    }
    /** 16x16 */ public static final @NotNull Icon CLASS_LIST = load("/icons/class-list.svg", -1608250002, 0);

}
