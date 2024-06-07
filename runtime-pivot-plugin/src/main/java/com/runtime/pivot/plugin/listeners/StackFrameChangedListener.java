package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StackFrameChangedListener implements XDebugSessionListener {

    private final Map<XStackFrame,Runnable> xStackFrameRunnableMap;
    private final XDebugSession xDebugSession;
    private final XStackFrame endXStackFrame;

    public StackFrameChangedListener(Map<XStackFrame, Runnable> xStackFrameRunnableMap, XDebugSession xDebugSession,XStackFrame endXStackFrame) {
        this.xDebugSession = xDebugSession;
        this.endXStackFrame = endXStackFrame;
        this.xStackFrameRunnableMap = new ConcurrentHashMap<>();
        this.xStackFrameRunnableMap.putAll(xStackFrameRunnableMap);
    }

    public void put(XStackFrame key,Runnable value){
        xStackFrameRunnableMap.put(key,value);
    }


    @Override
    public void sessionPaused() {
        XDebugSessionListener.super.sessionPaused();
        XStackFrame currentStackFrame = xDebugSession.getCurrentStackFrame();
        Runnable runnable = xStackFrameRunnableMap.get(currentStackFrame);
        if (runnable != null){
            try {
                runnable.run();
            } catch (Exception e) {
                xDebugSession.removeSessionListener(this);
                throw new RuntimeException(e);
            }
        }
        if (currentStackFrame.equals(endXStackFrame)){
            xDebugSession.removeSessionListener(this);
        }
    }
}
