package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.model.XStackContext;
import org.jetbrains.annotations.NotNull;

public abstract class RuntimeBaseAction extends AnAction {
    protected XDebugSession myXDebugSession;
    protected RuntimeContext runtimeContext;

    @Override
    final public void actionPerformed(@NotNull AnActionEvent e) {
        //TODO 通用校验
        //TODO 构造上下文
        myXDebugSession = DebuggerUIUtil.getSession(e);
        runtimeContext = RuntimeContext.getInstance(myXDebugSession);
        action(e);
    }

    @Override
    final public void update(@NotNull AnActionEvent e) {
        //TODO 断点条件下
        isEnable(e);
    }

    protected RuntimeContext getRuntimeContext() {
        return runtimeContext;
    }

    protected abstract boolean isEnable(AnActionEvent e);
    protected abstract void action(AnActionEvent e);
}
