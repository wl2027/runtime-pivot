package com.runtime.pivot.plugin.actions.method;

import com.intellij.debugger.JavaDebuggerBundle;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.XDebugMethodWatchListener;
import com.runtime.pivot.plugin.service.XDebugMethodContext;
import org.jetbrains.annotations.NotNull;

import static com.intellij.debugger.actions.PopFrameAction.ACTION_NAME;

public class MethodStartMonitoringAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        XDebugSession session = DebuggerUIUtil.getSession(e);
        XDebugMethodWatchListener xDebugMethodWatchListener = XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().get(session);
        if (xDebugMethodWatchListener != null) {
            //TODO 提示已存在监听器
            //String message = (previousBreakpoint == null) ? "No previous breakpoint found." : "Previous Breakpoint: " + previousBreakpoint;
            //Messages.showMessageDialog(project, message, "Previous Breakpoint", Messages.getInformationIcon());
//            Messages.showMessageDialog(e.getProject(), JavaDebuggerBundle.message("error.native.method.exception"),
//                    UIUtil.removeMnemonic(ActionsBundle.actionText(ACTION_NAME)), Messages.getErrorIcon());
            return;
        }
        xDebugMethodWatchListener = new XDebugMethodWatchListener(e.getProject(),"testTask", session);
        session.addSessionListener(xDebugMethodWatchListener);
        XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().put(session,xDebugMethodWatchListener);
    }
}
