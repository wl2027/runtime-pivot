package com.runtime.pivot.plugin.enums;

public enum XStackBreakpointType {
    AVAILABLE("可用的"),
    USED("已用的"),
    UNAVAILABLE("不可用的"),
    DISABLE("禁用的"),
    CURRENT("当前的"),
    ;

    private final String description;

    XStackBreakpointType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
