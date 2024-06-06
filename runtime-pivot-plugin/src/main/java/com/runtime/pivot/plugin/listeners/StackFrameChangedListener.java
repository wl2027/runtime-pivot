package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.frame.XStackFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class StackFrameChangedListener implements XDebugSessionListener {

    private final Map<XStackFrame,Runnable> xStackFrameRunnableMap;
    private final XDebugSession xDebugSession;
    private final XStackFrame endXStackFrame;

    public StackFrameChangedListener(Map<XStackFrame, Runnable> xStackFrameRunnableMap, XDebugSession xDebugSession,XStackFrame endXStackFrame) {
        this.xDebugSession = xDebugSession;
        this.endXStackFrame = endXStackFrame;
        this.xStackFrameRunnableMap = new ConcurrentHashMap<>();
        //java.lang.ClassCastException: class com.intellij.debugger.engine.JavaStackFrame cannot be cast to class java.lang.Comparable
        //this.xStackFrameRunnableMap = new ConcurrentSkipListMap<>();
        this.xStackFrameRunnableMap.putAll(xStackFrameRunnableMap);
    }

    public void put(XStackFrame key,Runnable value){
        xStackFrameRunnableMap.put(key,value);
    }

    @Override
    public void stackFrameChanged() {
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

    @Override
    public void sessionResumed() {
        XDebugSessionListener.super.sessionResumed();
    }

    @Override
    public void sessionStopped() {
        XDebugSessionListener.super.sessionStopped();
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
