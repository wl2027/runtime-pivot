//package com.runtime.pivot.plugin.actions.method;
//
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.xdebugger.XDebugSession;
//import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
//import com.runtime.pivot.plugin.listeners.XDebugMethodWatchListener;
//import com.runtime.pivot.plugin.service.XDebugMethodContext;
//import org.jetbrains.annotations.NotNull;
//
//public class MethodStopMonitoringAction extends AnAction {
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//
//        XDebugSession session = DebuggerUIUtil.getSession(e);
//        XDebugMethodWatchListener xDebugMethodWatchListener = XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().get(session);
//        session.removeSessionListener(xDebugMethodWatchListener);
//    }
//}
