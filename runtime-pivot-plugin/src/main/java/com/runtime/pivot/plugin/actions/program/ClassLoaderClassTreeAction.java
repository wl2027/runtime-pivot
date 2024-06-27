package com.runtime.pivot.plugin.actions.program;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ProgramAction;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class ClassLoaderClassTreeAction extends ProgramAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        String code = ActionExecutorUtil.buildCode(ActionType.Program.classLoaderClassTree,null);
        getRuntimeContext().executeAttachCode(code);
    }
}
