package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;

public abstract class XStackFrameListener implements XDebugSessionListener {

    private final XDebugSession xDebugSession;
    private final XStackFrame xStackFrame;

    protected XStackFrameListener(XDebugSession xDebugSession, XStackFrame xStackFrame) {
        this.xDebugSession = xDebugSession;
        this.xStackFrame = xStackFrame;
    }

    public abstract void stackFrameExecutionMethod();

    @Override
    public void sessionPaused() {
        try {
            XStackFrame currentStackFrame = xDebugSession.getCurrentStackFrame();
            if (currentStackFrame.equals(xStackFrame)) {
                stackFrameExecutionMethod();
            }
        }catch (Exception exception){

        }finally {
            xDebugSession.removeSessionListener(this);
        }
    }
}
