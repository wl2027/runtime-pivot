package com.runtime.pivot.plugin.actions.object;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.test.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoreAction extends XDebuggerTreeActionBase {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();

        String text = ActionExecutorUtil.buildCode(ActionType.Object.store,name);
        XDebugSession session = DebuggerUIUtil.getSession(e);
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
    }

    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {

    }

    public static @Nullable XValueNodeImpl getSelectedNode(@NotNull DataContext dataContext) {
        return ContainerUtil.getFirstItem(getSelectedNodes(dataContext));
    }
    public static @NotNull List<XValueNodeImpl> getSelectedNodes(@NotNull DataContext dataContext) {
        return XDebuggerTree.getSelectedNodes(dataContext);
    }
}
