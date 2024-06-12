package com.runtime.pivot.plugin.utils;

import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.runtime.pivot.plugin.enums.BreakpointType;
import org.jetbrains.annotations.NotNull;

public class RuntimePivotUtil {
    public static String getPositionName(@NotNull XSourcePosition currentPosition) {
        return currentPosition==null?"":currentPosition.getFile().getName() + ":" + currentPosition.getLine();
    }
    public static String getNextPositionName(@NotNull XSourcePosition currentPosition) {
        return currentPosition==null?"":currentPosition.getFile().getName() + ":" + (currentPosition.getLine()+1);
    }

    public static boolean compareBreakpoints(XBreakpoint o1,XBreakpoint o2){
        if (o1.getSourcePosition().getFile().getUrl().equals(o2.getSourcePosition().getFile().getUrl())
                && o1.getSourcePosition().getLine()==(o2.getSourcePosition().getLine())) {
            return true;
        }else {
            return false;
        }
    }
}
