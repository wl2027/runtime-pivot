package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.model.XStackContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    public static @NotNull List<XValueNodeImpl> getSelectedNodes(@NotNull DataContext dataContext) {
        return XDebuggerTree.getSelectedNodes(dataContext);
    }

    public static @Nullable XValueNodeImpl getSelectedNode(@NotNull DataContext dataContext) {
        return ContainerUtil.getFirstItem(getSelectedNodes(dataContext));
    }

    @Nullable
    public static XValue getSelectedValue(@NotNull DataContext dataContext) {
        XValueNodeImpl node = getSelectedNode(dataContext);
        return node != null ? node.getValueContainer() : null;
    }

}
