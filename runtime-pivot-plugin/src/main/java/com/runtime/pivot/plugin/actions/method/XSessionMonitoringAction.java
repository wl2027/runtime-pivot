package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.MethodAction;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.XSessionMonitoringDialog;
import org.jetbrains.annotations.NotNull;

public class XSessionMonitoringAction extends MethodAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        XSessionMonitoringDialog xSessionMonitoringDialog = RuntimePivotMethodService
                .getInstance(e.getProject())
                .buildXSessionMonitoringDialog(getRuntimeContext().getXDebugSession());
        xSessionMonitoringDialog.setVisible(true);
    }
}
