package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.RuntimeBaseAction;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import org.jetbrains.annotations.NotNull;


public class XSessionBreakpointAction extends RuntimeBaseAction {


    @Override
    protected boolean isEnable(AnActionEvent e) {
        return true;
    }

    @Override
    public void action(@NotNull AnActionEvent e) {
        XSessionBreakpointDialog xSessionBreakpointDialog =
                RuntimePivotMethodService.getInstance(e.getProject())
                        .buildXSessionBreakpointDialog(myXDebugSession);
        xSessionBreakpointDialog.setVisible(true);
    }

}
