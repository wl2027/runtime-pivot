package com.runtime.pivot.plugin.utils;

import com.intellij.xdebugger.XSourcePosition;
import org.jetbrains.annotations.NotNull;

public class RuntimePivotUtil {
    public static String getPositionName(@NotNull XSourcePosition currentPosition) {
        return currentPosition==null?"":currentPosition.getFile().getName() + ":" + currentPosition.getLine();
    }
    public static String getNextPositionName(@NotNull XSourcePosition currentPosition) {
        return currentPosition==null?"":currentPosition.getFile().getName() + ":" + (currentPosition.getLine()+1);
    }
}
