package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
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
        //
        String basePath = e.getProject().getBasePath();
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();
        String text = ActionExecutorUtil.buildCode(ActionType.Object.objectStore,null,name,ActionExecutorUtil.buildStringObject(basePath));
        XDebugSession session = DebuggerUIUtil.getSession(e);
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
        //刷新./.runtime文件夹
        VirtualFile baseDir = ProjectUtil.guessProjectDir(e.getProject());
        VirtualFile child = baseDir.findChild(".runtime");
        child.getFileSystem().refresh(false);
    }
}
