package com.runtime.pivot.plugin.domain;

import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;

public class MethodWatchContext implements XDebugSessionListener {

    public static XDebugSession xDebugSession;
    public static XDebugSession xDebugSession2;
    public static XStackFrame xStackFrame;
    public static XExecutionStack xExecutionStack;
    public static XDebugProcess debugProcess;
    public static ExecutionEnvironment executionEnvironment;
    public static RunContentDescriptor runContentDescriptor;
    public static SuspendContextImpl suspendContext;

    @Override
    public void sessionPaused() {
        XDebugSessionListener.super.sessionPaused();
    }

    @Override
    public void sessionResumed() {
        XDebugSessionListener.super.sessionResumed();
    }

    @Override
    public void sessionStopped() {
        XDebugSessionListener.super.sessionStopped();
    }

    @Override
    public void stackFrameChanged() {
        XDebugSessionListener.super.stackFrameChanged();
    }

    @Override
    public void beforeSessionResume() {
        XDebugSessionListener.super.beforeSessionResume();
    }

    @Override
    public void settingsChanged() {
        XDebugSessionListener.super.settingsChanged();
    }

    @Override
    public void breakpointsMuted(boolean muted) {
        XDebugSessionListener.super.breakpointsMuted(muted);
    }
}
