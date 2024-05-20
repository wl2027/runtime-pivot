package com.runtime.pivot.plugin.test;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.DebuggerSupport;
import com.intellij.xdebugger.impl.actions.DebuggerActionHandler;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import org.jetbrains.annotations.NotNull;

public class InstrumentationAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        //sun.instrument.TransformerManager.transform 拦截这个方法,比较前后byte[]
        //Arrays.equals()

        //控制台打印封装-Console
        //树结构工具-TreeUtil
        //获取方法入口
        //StackTraceElement ele = ExceptionUtil.getRootStackElement();
        //ele.getMethodName();
        //记录运行=>耗时&链路?
        //com.intellij.xdebugger.impl.actions.ResumeAction
        //ThreadUtil.getStackTrace
        //getStackTrace 获得堆栈列表
        //getStackTraceElement 获得堆栈项
        //ConcurrencyTester tester = ThreadUtil.concurrencyTest


        //ScriptUtil.eval 执行Javascript脚本

        //系统属性调用-SystemUtil

//        SystemUtil.dumpSystemInfo();
        Project project = e.getProject();
        for (DebuggerSupport debuggerSupport : DebuggerSupport.getDebuggerSupports()) {
            DebuggerActionHandler evaluateHandler = debuggerSupport.getEvaluateHandler();
            //evaluateHandler.perform();
        }
        //MY XDebuggerActionBase
//        for (DebuggerSupport support : DebuggerSupport.getDebuggerSupports()) {
//            if (isEnabled(project, e, support)) {
//                perform(project, e, support);
//            }
//        }
        //MY XDebuggerActionHandler
        XDebugSession session = DebuggerUIUtil.getSession(e);

        //MY XDebuggerEvaluateActionHandler.perform/showDialog  / AppUIUtil
        //XDebuggerUtil.getInstance().createExpression(expression, null, null, EvaluationMode.EXPRESSION)

        //MY XDebuggerTestUtil
        XStackFrame frame = session.getCurrentStackFrame();

//        assertNotNull(frame);
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        //XDebuggerEvaluator => PromiseDebuggerEvaluator/JavaDebuggerEvaluator
//        assertNotNull(evaluator);
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        @NotNull String exp = "USER.toString()";
//        @NotNull String exp = "com.runtime.pivot.agent.PreAgent.print();";
//        @NotNull String exp = "System.out.println(USER)";
//        @NotNull String exp = "System.out.println(cn.hutool.system.SystemUtil.dumpSystemInfo());";
        XExpressionImpl xExpression = XExpressionImpl.fromText(exp);
        //MY 异步执行,需要信号量做前驱判断
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
        Pair<XValue, String> xValueStringPair = callback.waitFor(5000L);
        System.out.println(xValueStringPair);
        //异常信息和获取值
        //MY ((JavaValue) xValueStringPair.getFirst()).getDescriptor().getValue()
//        new DebuggerContextCommandImpl(session.getContextManager().context) {
//
//            @Override
//            public void threadAction(@NotNull SuspendContextImpl suspendContext) {
//
//            }
//        };
//        session.process.managerThread.schedule(command)


        /** MY
         * evaluator.evaluate(expression,
         *                          new XDebuggerEvaluator.XEvaluationCallback() {
         *                            @Override
         *                            public void evaluated(@NotNull XValue result) {
         *                              if (result instanceof JavaValue) {
         *                                forceEarlyReturnWithFinally(((JavaValue)result).getDescriptor().getValue(),
         *                                                            stackFrame,
         *                                                            debugProcess,
         *                                                            dialog);
         *                              }
         *                            }
         *
         *                            @Override
         *                            public void errorOccurred(@NotNull final @NlsContexts.DialogMessage String errorMessage) {
         *                              showError(project, JavaDebuggerBundle.message("error.unable.to.evaluate.expression") + ": " + errorMessage);
         *                            }
         *                          }, stackFrame.getSourcePosition());
         */


    }
}
