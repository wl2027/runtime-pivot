package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ObjectAction;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @see com.intellij.ide.actions.SynchronizeCurrentFileAction
 */
public class ObjectStoreAction extends ObjectAction {

    @Override
    public void action(@NotNull AnActionEvent e) {
        String basePath = e.getProject().getBasePath();
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();
        String code = ActionExecutorUtil.buildCode(ActionType.Object.objectStore,null,name,ActionExecutorUtil.buildStringObject(basePath));
        //回调后同步刷新./.runtime文件夹
        getRuntimeContext().executeAttachCode(code, new XEvaluationCallbackBase() {
            @Override
            public void evaluated(@NotNull XValue result) {
                VirtualFile baseDir = ProjectUtil.guessProjectDir(e.getProject());
                VirtualFile child = baseDir.findChild(".runtime");
                child.getFileSystem().refresh(false);
            }

            @Override
            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
                //TODO 弹窗提示
            }
        });

    }
}
