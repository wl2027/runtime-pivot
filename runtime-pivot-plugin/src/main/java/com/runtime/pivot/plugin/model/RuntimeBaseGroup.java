package com.runtime.pivot.plugin.model;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import org.jetbrains.annotations.NotNull;

public class RuntimeBaseGroup extends DefaultActionGroup {
    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        //可执行栈帧条件下才可见可用
        XDebugSession session = DebuggerUIUtil.getSession(e);
        if (session == null) {
            presentation.setVisible(false);
            presentation.setEnabled(false);
            return;
        }
        XStackFrame currentStackFrame = session.getCurrentStackFrame();
        if (currentStackFrame == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        XDebuggerEvaluator evaluator = currentStackFrame.getEvaluator();
        if (evaluator == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
    }
}
