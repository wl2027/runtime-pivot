package com.runtime.pivot.plugin.domain;

import com.intellij.icons.AllIcons;
import com.runtime.pivot.plugin.enums.BreakpointType;

import javax.swing.*;

public class BreakpointListItem {
    private final String text;
    private final Icon icon;
    private final BreakpointType breakpointType;

    public BreakpointListItem(String text, BreakpointType breakpointType) {
        this.text = text;
        this.breakpointType = breakpointType;
        switch (breakpointType) {
            case AVAILABLE:
                this.icon = AllIcons.Debugger.Db_verified_breakpoint;
                break;
            case NOT_AVAILABLE:
                this.icon = AllIcons.Debugger.Db_muted_breakpoint;
                break;
            default:this.icon = AllIcons.Debugger.Db_muted_breakpoint;
        }

    }

    public String getText() {
        return text;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return text;
    }
}
