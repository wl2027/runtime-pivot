package com.runtime.pivot.plugin.actions.method;

import cn.hutool.core.date.StopWatch;
import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.domain.MethodWatchContext;
import com.runtime.pivot.plugin.model.XDebugMethodWatchListener;
import com.runtime.pivot.plugin.service.XDebugMethodContext;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.sun.jdi.event.Event;
import org.jetbrains.annotations.NotNull;

public class MethodStartWatchAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        XDebugSession session = DebuggerUIUtil.getSession(e);
        XDebugMethodWatchListener xDebugMethodWatchListener = new XDebugMethodWatchListener("testTask", session);
        session.addSessionListener(xDebugMethodWatchListener);
        XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().put(session,xDebugMethodWatchListener);
    }
}
