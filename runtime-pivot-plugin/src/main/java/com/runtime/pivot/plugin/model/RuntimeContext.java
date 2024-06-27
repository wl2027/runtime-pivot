package com.runtime.pivot.plugin.model;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RuntimeContext {

    private @NotNull XDebugSession myXDebugSession;
    private @NotNull XDebuggerEvaluator myXDebuggerEvaluator;

    public RuntimeContext(XDebugSession xDebugSession) {
        XStackFrame currentStackFrame = xDebugSession.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = currentStackFrame.getEvaluator();
        myXDebugSession = xDebugSession;
        myXDebuggerEvaluator = evaluator;
    }

    public static RuntimeContext getInstance(XDebugSession xDebugSession) {
        if (xDebugSession == null) {
            return null;
        }
        return new RuntimeContext(xDebugSession);
    }

    /**
     * 执行探针代码
     * @param code
     * @return
     * @throws Exception
     */
    public Object executeAttachCode(String code) {
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(code, EvaluationMode.CODE_FRAGMENT);
        myXDebuggerEvaluator.evaluate(xExpression,callback,myXDebugSession.getCurrentPosition());
        return callback;
    }

    public XDebugSession getXDebugSession() {
        return myXDebugSession;
    }
}
