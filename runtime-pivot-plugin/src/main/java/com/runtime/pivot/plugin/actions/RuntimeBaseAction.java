package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.RuntimeContext;
import org.jetbrains.annotations.NotNull;

public abstract class RuntimeBaseAction extends AnAction {

    private RuntimeContext myRuntimeContext;

    @Override
    final public void actionPerformed(@NotNull AnActionEvent e) {
        //TODO 通用校验
        //TODO 构造上下文
        myRuntimeContext = loadRuntimeContext(e);
        action(e);
    }

    private RuntimeContext loadRuntimeContext(AnActionEvent e) {
        XDebugSession xDebugSession = DebuggerUIUtil.getSession(e);
        return RuntimeContext.getInstance(xDebugSession);
    }

    @Override
    final public void update(@NotNull AnActionEvent e) {
        //TODO 断点条件下
        isEnable(e);
    }

    protected RuntimeContext getRuntimeContext() {
        return myRuntimeContext;
    }

    protected abstract boolean isEnable(AnActionEvent e);
    protected abstract void action(AnActionEvent e);
}
