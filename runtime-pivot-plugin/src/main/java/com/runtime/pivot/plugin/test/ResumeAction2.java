package com.runtime.pivot.plugin.test;


import cn.hutool.core.date.StopWatch;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import org.jetbrains.annotations.NotNull;

public class ResumeAction2 extends ResumeAction implements DumbAware {

//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        XDebugSession session = DebuggerUIUtil.getSession(e);
//        XStackFrame frame = session.getCurrentStackFrame();
//        XDebuggerEvaluator evaluator = frame.getEvaluator();
//        XTestEvaluationCallback callback1 = new XTestEvaluationCallback();
//        XTestEvaluationCallback callback2 = new XTestEvaluationCallback();
//        XTestEvaluationCallback callback3 = new XTestEvaluationCallback();
//
//        @NotNull String exp = "com.runtime.pivot.agent.InstrumentationUtils.start();";
//        @NotNull String exp2 = "com.runtime.pivot.agent.InstrumentationUtils.stop();";
//        XExpressionImpl xExpression = XExpressionImpl.fromText(exp);
//        //MY 异步执行,需要信号量做前驱判断
//        evaluator.evaluate(xExpression, callback1, session.getCurrentPosition());
//        Pair<XValue, String> xValueStringPair = callback1.waitFor(XDebuggerTestUtil.TIMEOUT_MS);
////        Value value = ((JavaValue) xValueStringPair.getFirst()).getDescriptor().getValue();
////        Long l1 = new Long(value.toString());
//        super.actionPerformed(e);
//        session = DebuggerUIUtil.getSession(e);
////        evaluator = session.getCurrentStackFrame().getEvaluator();
//        evaluator.evaluate(XExpressionImpl.fromText(exp2), callback2, session.getCurrentPosition());
//        Pair<XValue, String> xValueStringPair2 = callback2.waitFor(XDebuggerTestUtil.TIMEOUT_MS);
////        Value value2 = ((JavaValue) xValueStringPair2.getFirst()).getDescriptor().getValue();
////        Long l2 = new Long(value2.toString());
////        long l = l2 - l1;
////        @NotNull String exp3 = "System.out.printf(\"执行时长：%d 毫秒.\", "+l+");";
////        evaluator.evaluate(XExpressionImpl.fromText(exp3), callback3, session.getCurrentPosition());
////        callback3.waitFor(XDebuggerTestUtil.TIMEOUT_MS);
//    }
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        XDebugSession session = DebuggerUIUtil.getSession(e);
//        XStackFrame frame = session.getCurrentStackFrame();
//        XDebuggerEvaluator evaluator = frame.getEvaluator();
//        XTestEvaluationCallback callback = new XTestEvaluationCallback();
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        //异步触发无法这样记录时间
//        super.actionPerformed(e);
//        stopWatch.stop();
//        double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
//        System.out.println(totalTimeSeconds);
//        @NotNull String exp = "System.out.println(\"执行时间:\"+"+totalTimeSeconds+");";
//        XExpressionImpl xExpression = XExpressionImpl.fromText(exp);
//        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
////        Pair<XValue, String> xValueStringPair = callback.waitFor(1000);
//    }

//    private static final Key<Long> LAST_RESUME_TIME = Key.create("LAST_RESUME_TIME");
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        Project project = e.getProject();
//        if (project == null) {
//            return;
//        }
//
//        DebuggerSession debuggerSession = getDebuggerSession(e);
//        if (debuggerSession == null) {
//            return;
//        }
//
//        SuspendContextImpl suspendContext = debuggerSession.getContextManager().getContext().getSuspendContext();
//        if (suspendContext != null) {
//            long currentTime = System.currentTimeMillis();
//            Long lastResumeTime = suspendContext.getUserData(LAST_RESUME_TIME);
//
//            if (lastResumeTime != null) {
//                long elapsedTime = currentTime - lastResumeTime;
//                System.out.println("Elapsed time between breakpoints: " + elapsedTime + "ms");
//            }
//
//            // Update the last resume time
//            suspendContext.putUserData(LAST_RESUME_TIME, currentTime);
//        }
//
//        // Call the super method to resume the execution
//        super.actionPerformed(e);
//    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession session = DebuggerUIUtil.getSession(e);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XDebugSessionListener xDebugSessionListener = new XDebugSessionListener(){
            @Override
            public void sessionResumed() {
                System.out.println("a");
            }

            @Override
            public void sessionStopped() {
                System.out.println("b");
            }

            @Override
            public void stackFrameChanged() {
                System.out.println("c");
            }

            @Override
            public void beforeSessionResume() {
                System.out.println("d");
            }

            @Override
            public void settingsChanged() {
                System.out.println("e");
            }

            @Override
            public void breakpointsMuted(boolean muted) {
                System.out.println("f");
            }

            @Override
            public void sessionPaused() {
                System.out.println("g");
                stopWatch.stop();
                double totalTimeSeconds = stopWatch.getTotalTimeSeconds();
                System.out.println(totalTimeSeconds);
                @NotNull String exp = "System.out.println(\"执行时间:\"+"+totalTimeSeconds+");";
                XExpressionImpl xExpression = XExpressionImpl.fromText(exp);
                evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
            }
        };
        session.addSessionListener(xDebugSessionListener);
        super.actionPerformed(e);
//        session.removeSessionListener(xDebugSessionListener);
    }

}

