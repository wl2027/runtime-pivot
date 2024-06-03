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

/**
 * 参考 {@link com.intellij.xdebugger.impl.ui.tree.actions.XCopyNameAction}
 */
public class ObjectInternalsAction extends XDebuggerTreeActionBase {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node.getName();

        String text = ActionExecutorUtil.buildCode(ActionType.Object.objectInternals,null,name);
        XDebugSession session = DebuggerUIUtil.getSession(e);
        /**
         * pause用在已经停止的事情再次开始前，短时间停止在说或者做的。
         * suspension指的是使某件正在进行中的事暂停，尤其强调是短时间的停止。
         */
        //session.isSuspended()//已暂停
        //session.isPaused()//已暂停
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
