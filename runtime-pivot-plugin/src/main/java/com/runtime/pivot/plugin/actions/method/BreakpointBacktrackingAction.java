package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.actions.RuntimeBaseAction;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.RuntimeBreakpointDialog;
import org.jetbrains.annotations.NotNull;


public class BreakpointBacktrackingAction extends RuntimeBaseAction {


    @Override
    protected boolean isEnable(AnActionEvent e) {
        return true;
    }

    @Override
    public void action(@NotNull AnActionEvent e) {
        RuntimeContext runtimeContext = getRuntimeContext();
        RuntimeBreakpointDialog runtimeBreakpointDialog =
                RuntimePivotMethodService.getInstance(e.getProject())
                        .getRuntimeBreakpointDialog(runtimeContext);
        runtimeBreakpointDialog.setVisible(true);
    }

}
