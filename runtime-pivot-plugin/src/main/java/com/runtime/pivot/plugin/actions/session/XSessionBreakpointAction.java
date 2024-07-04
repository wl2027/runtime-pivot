package com.runtime.pivot.plugin.actions.session;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.MethodAction;
import com.runtime.pivot.plugin.service.RuntimePivotXSessionService;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import org.jetbrains.annotations.NotNull;


public class XSessionBreakpointAction extends MethodAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        XSessionBreakpointDialog xSessionBreakpointDialog =
                RuntimePivotXSessionService.getInstance(e.getProject())
                        .buildXSessionBreakpointDialog(getRuntimeContext().getXDebugSession());
        xSessionBreakpointDialog.setVisible(true);
    }

}
