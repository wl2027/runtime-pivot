package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.MethodAction;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import org.jetbrains.annotations.NotNull;


public class XSessionBreakpointAction extends MethodAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        XSessionBreakpointDialog xSessionBreakpointDialog =
                RuntimePivotMethodService.getInstance(e.getProject())
                        .buildXSessionBreakpointDialog(getRuntimeContext().getXDebugSession());
        xSessionBreakpointDialog.setVisible(true);
    }

}
