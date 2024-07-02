package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.model.RuntimeAgentAction;

public abstract class ProgramAction extends RuntimeAgentAction {

    @Override
    final protected boolean isEnable(AnActionEvent e) {
        return super.isEnable(e);
    }
}
