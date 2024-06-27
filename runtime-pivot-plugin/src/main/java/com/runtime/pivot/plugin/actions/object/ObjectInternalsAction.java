package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ObjectAction;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class ObjectInternalsAction extends ObjectAction {

    public void action(@NotNull AnActionEvent e) {
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();
        String code = ActionExecutorUtil.buildCode(ActionType.Object.objectInternals,null,name);
        getRuntimeContext().executeAttachCode(code);
    }
}
