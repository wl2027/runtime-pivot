//package com.runtime.pivot.plugin.actions;
//
//import com.intellij.debugger.DebuggerManager;
//import com.intellij.debugger.engine.DebugProcess;
//import com.intellij.debugger.engine.DebugProcessImpl;
//import com.intellij.debugger.engine.evaluation.EvaluateException;
//import com.intellij.debugger.engine.evaluation.EvaluationContextImpl;
//import com.intellij.debugger.engine.evaluation.TextWithImportsImpl;
//import com.intellij.debugger.impl.DebuggerContextImpl;
//import com.intellij.execution.ExecutionManager;
//import com.intellij.execution.process.ProcessHandler;
//import com.intellij.ide.lightEdit.LightEdit;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.intellij.xdebugger.XDebugSession;
//import com.intellij.xdebugger.XExpression;
//import com.intellij.xdebugger.impl.DebuggerSupport;
//import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
//import com.sun.jdi.Value;
//import org.jetbrains.annotations.NotNull;
//
//public class ExecuteXExpressionAction extends AnAction {
//
//    public boolean isEnabled(@NotNull final Project project, final AnActionEvent event) {
//        if (LightEdit.owns(project)) return false;
//        XDebugSession session = DebuggerUIUtil.getSession(event);
//        return session != null && isEnabled(session, event.getDataContext());
//    }
//    @Override
//    public void actionPerformed(AnActionEvent e) {
//        for (DebuggerSupport support : DebuggerSupport.getDebuggerSupports()) {
//            support.
//            if (isEnabled(project, e, support)) {
//                perform(project, e, support);
//                return true;
//            }
//        }
//        // 获取调试器管理器实例
//        DebuggerManager debuggerManager = DebuggerManager.getInstance(e.getProject());
//        // 获取ExecutionManager实例
//        ExecutionManager executionManager = ExecutionManager.getInstance(e.getProject());
//        for (ProcessHandler runningProcess : executionManager.getRunningProcesses()) {
//
//        }
//        DebugProcess debugProcess1 = debuggerManager.getDebugProcess();
//        // 获取当前调试上下文
//        DebuggerContextImpl debuggerContext = debuggerManager.getContext();
//
//        // 获取调试进程
//        DebugProcess debugProcess = debuggerContext.getDebugProcess();
//
//        // 创建XExpression对象
//        String expressionText = "your_expression_here";
//        TextWithImportsImpl textWithImports = TextWithImportsImpl.fromText(expressionText);
//        EvaluationContextImpl evaluationContext = new EvaluationContextImpl(debuggerContext, debugProcess, debuggerContext.getSuspendContext());
//        Value value = evaluationContext.computeThisObject();
//
//        XExpression expression = new XExpression(textWithImports, evaluationContext);
//
//        try {
//            // 执行XExpression并获取结果
//            Object result = expression.evaluate();
//
//            // 处理执行结果
//            if (result != null) {
//                System.out.println("Execution result: " + result.toString());
//            } else {
//                System.out.println("Execution result is null");
//            }
//        } catch (EvaluateException ex) {
//            // 处理表达式执行时的异常
//            ex.printStackTrace();
//        }
//    }
//}
//
