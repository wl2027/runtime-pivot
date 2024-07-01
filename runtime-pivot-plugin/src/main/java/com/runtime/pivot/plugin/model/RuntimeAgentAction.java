package com.runtime.pivot.plugin.model;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.config.RuntimePivotSettings;

public abstract class RuntimeAgentAction extends RuntimeBaseAction {

    @Override
    protected boolean isEnable(AnActionEvent e) {
        return isAttachAgent(e);
    }

//    @Override
//    protected boolean isVisible(AnActionEvent e) {
//        return super.isVisible(e) && isAttachAgent(e);
//    }

    private boolean isAttachAgent(AnActionEvent e){
        return RuntimePivotSettings.getInstance(e.getProject()).isAttachAgent();
    }

}
