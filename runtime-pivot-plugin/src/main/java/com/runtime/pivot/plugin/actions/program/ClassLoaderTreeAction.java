package com.runtime.pivot.plugin.actions.program;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ProgramAction;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class ClassLoaderTreeAction extends ProgramAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        String code = ActionExecutorUtil.buildCode(ActionType.Program.classLoaderTree,null);
        getRuntimeContext().executeAttachCode(code);
    }
}
