package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.actions.RuntimeBaseAction;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.RuntimeMonitoringDialog;
import org.jetbrains.annotations.NotNull;

public class MethodMonitoringAction extends RuntimeBaseAction {
    @Override
    protected boolean isEnable(AnActionEvent e) {
        return true;
    }

    @Override
    public void action(@NotNull AnActionEvent e) {
        RuntimeContext runtimeContext = getRuntimeContext();
        RuntimePivotMethodService runtimePivotMethodService = RuntimePivotMethodService.getInstance(e.getProject());
        RuntimeMonitoringDialog runtimeMonitoringDialog = runtimePivotMethodService.getRuntimeMonitoringDialog(runtimeContext);
        runtimeMonitoringDialog.setVisible(true);
    }
}
