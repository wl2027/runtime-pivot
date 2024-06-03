package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.XDebugMethodWatchListener;
import com.runtime.pivot.plugin.service.XDebugMethodContext;
import org.jetbrains.annotations.NotNull;

public class MethodStartMonitoringAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        XDebugSession session = DebuggerUIUtil.getSession(e);
        XDebugMethodWatchListener xDebugMethodWatchListener = new XDebugMethodWatchListener(e.getProject(),"testTask", session);
        session.addSessionListener(xDebugMethodWatchListener);
        XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().put(session,xDebugMethodWatchListener);
    }
}
