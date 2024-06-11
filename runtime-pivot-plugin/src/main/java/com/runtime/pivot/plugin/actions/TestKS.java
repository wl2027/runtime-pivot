package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;

public class TestKS extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
        XDebugSession session = DebuggerUIUtil.getSession(e);
        session.addSessionListener(new XDebugSessionListener() {
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
                //选中不同栈帧时候会变,多线程切换也会触发
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
        });
    }
}
