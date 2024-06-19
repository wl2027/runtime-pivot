package com.runtime.pivot.plugin.actions.program;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class TransformersAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        String text = ActionExecutorUtil.buildCode(ActionType.Program.transformers,null);
        XDebugSession session = DebuggerUIUtil.getSession(e);
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
    }
}
