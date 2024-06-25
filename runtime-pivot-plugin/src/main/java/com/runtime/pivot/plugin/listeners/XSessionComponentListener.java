package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XSessionComponentListener implements XDebuggerManagerListener {
    @Override
    public void processStarted(@NotNull XDebugProcess debugProcess) {
        XDebuggerManagerListener.super.processStarted(debugProcess);
    }

    @Override
    public void processStopped(@NotNull XDebugProcess debugProcess) {
        XDebuggerManagerListener.super.processStopped(debugProcess);
        XDebugSession session = debugProcess.getSession();
        RuntimePivotMethodService
                .getInstance(debugProcess.getSession().getProject())
                .closeXSessionComponent(session);
    }

    @Override
    public void currentSessionChanged(@Nullable XDebugSession previousSession, @Nullable XDebugSession currentSession) {
        XDebuggerManagerListener.super.currentSessionChanged(previousSession, currentSession);
    }
}
