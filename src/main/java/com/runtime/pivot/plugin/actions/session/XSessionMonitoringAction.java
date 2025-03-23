package com.runtime.pivot.plugin.actions.session;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.MethodAction;
import com.runtime.pivot.plugin.service.RuntimePivotXSessionService;
import com.runtime.pivot.plugin.view.method.XSessionMonitoringDialog;
import org.jetbrains.annotations.NotNull;

public class XSessionMonitoringAction extends MethodAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        XSessionMonitoringDialog xSessionMonitoringDialog = RuntimePivotXSessionService
                .getInstance(e.getProject())
                .buildXSessionMonitoringDialog(getRuntimeContext().getXDebugSession());
        xSessionMonitoringDialog.setVisible(true);
    }
}
