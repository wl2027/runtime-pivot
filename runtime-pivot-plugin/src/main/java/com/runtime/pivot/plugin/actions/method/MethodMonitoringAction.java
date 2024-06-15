package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.MonitoringTableDialog;
import org.jetbrains.annotations.NotNull;

public class MethodMonitoringAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession session = DebuggerUIUtil.getSession(e);
        RuntimePivotMethodService runtimePivotMethodService = RuntimePivotMethodService.getInstance(e.getProject());
        MonitoringTableDialog monitoringTableDialog = runtimePivotMethodService.getSessionMonitoringTableMap().get(session);
        Project project = e.getProject();
        if (monitoringTableDialog == null || !monitoringTableDialog.isVisible()) {
            monitoringTableDialog = MonitoringTableDialog.getInstance(project, DebuggerUIUtil.getSession(e));
            runtimePivotMethodService.getSessionMonitoringTableMap().put(session,monitoringTableDialog);
            monitoringTableDialog.setVisible(true);
        } else {
            //TODO 提示已存在监听器
        }
    }
}
