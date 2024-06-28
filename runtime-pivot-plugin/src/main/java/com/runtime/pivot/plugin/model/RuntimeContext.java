package com.runtime.pivot.plugin.model;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.EvaluationMode;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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
        RuntimeEvaluationCallback callback = new RuntimeEvaluationCallback();
        return executeAttachCode(code,callback);
    }

    public XEvaluationCallbackBase executeAttachCode(String code, Consumer<JavaValue> javaValueConsumer) {
        RuntimeEvaluationCallback callback = new RuntimeEvaluationCallback(javaValueConsumer);
        return executeAttachCode(code,callback);
    }

    public XEvaluationCallbackBase executeAttachCode(String code, Consumer<JavaValue> javaValueConsumer,Consumer<String> errorOccurredConsumer) {
        RuntimeEvaluationCallback callback = new RuntimeEvaluationCallback(javaValueConsumer,errorOccurredConsumer);
        return executeAttachCode(code,callback);
    }

    public XEvaluationCallbackBase executeAttachCode(String code, XEvaluationCallbackBase callback) {
        XExpressionImpl xExpression = XExpressionImpl.fromText(code, EvaluationMode.CODE_FRAGMENT);
        myXDebuggerEvaluator.evaluate(xExpression,callback,myXDebugSession.getCurrentPosition());
        return callback;
    }

    public XDebugSession getXDebugSession() {
        return myXDebugSession;
    }
}
