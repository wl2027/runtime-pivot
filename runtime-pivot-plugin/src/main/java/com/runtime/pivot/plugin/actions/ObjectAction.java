package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.WatchesRootNode;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ObjectAction extends RuntimeBaseAction {
    /**
     * 不为空,且不能是计算的,要内存中真实存在的
     * @see XDebuggerTreeActionBase#update(AnActionEvent)
     * @param e
     * @return
     */
    @Override
    final protected boolean isEnable(AnActionEvent e) {
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        return node != null && node.getName() != null && node.getParent() instanceof WatchesRootNode;
    }

    /**
     * @see XDebuggerTreeActionBase
     * @param dataContext
     * @return
     */
    public static @NotNull List<XValueNodeImpl> getSelectedNodes(@NotNull DataContext dataContext) {
        return XDebuggerTree.getSelectedNodes(dataContext);
    }

    public static @Nullable XValueNodeImpl getSelectedNode(@NotNull DataContext dataContext) {
        return ContainerUtil.getFirstItem(getSelectedNodes(dataContext));
    }

    public static @Nullable XValue getSelectedValue(@NotNull DataContext dataContext) {
        XValueNodeImpl node = getSelectedNode(dataContext);
        return node != null ? node.getValueContainer() : null;
    }
}
