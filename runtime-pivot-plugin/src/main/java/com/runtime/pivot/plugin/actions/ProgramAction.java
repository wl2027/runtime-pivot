package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;

public abstract class ProgramAction extends RuntimeBaseAction {

    @Override
    final protected boolean isEnable(AnActionEvent e) {
        return true;
    }
}
