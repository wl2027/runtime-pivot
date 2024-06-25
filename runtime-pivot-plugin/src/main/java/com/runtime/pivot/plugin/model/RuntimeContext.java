package com.runtime.pivot.plugin.model;

import com.intellij.xdebugger.XDebugSession;

public class RuntimeContext {

    public RuntimeContext(XDebugSession xDebugSession) {
    }

    public static RuntimeContext getInstance(XDebugSession xDebugSession) {
        return new RuntimeContext(xDebugSession);
    }
}
