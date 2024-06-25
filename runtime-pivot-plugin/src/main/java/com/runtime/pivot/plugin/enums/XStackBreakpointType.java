package com.runtime.pivot.plugin.enums;

public enum XStackBreakpointType {
    AVAILABLE("可用"),
    USED("已用"),
    UNAVAILABLE("不可用"),
    DISABLE("禁用"),
    ;

    private final String description;

    XStackBreakpointType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
