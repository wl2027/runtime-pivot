package com.runtime.pivot.plugin.actions;

import cn.hutool.core.date.StopWatch;
import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.domain.MethodWatchContext;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import org.jetbrains.annotations.NotNull;

public class MethodStartWatchAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        /**
         * 开启后:
         * 当前断点->下一断点进入 下一断点执行->下一断点进入 ...
         * 任务1->任务2->
         *
         * 跨线程->传递?
         *
         * 复原上一个断点
         */
        //e.getData(XDebugSession.DATA_KEY)==null
        StopWatch stopWatch = new StopWatch("");
        XDebugSession session = DebuggerUIUtil.getSession(e);
        String sessionName = session.getSessionName();
        //Method threw 'java.lang.IllegalStateException' exception. Cannot evaluate com.intellij.debugger.engine.JavaExecutionStack.toString()
        MethodWatchContext.xDebugSession= session;
        //挂起上下文,这个会被清空,很重要要回到这个上下文
        XSuspendContext suspendContext = session.getSuspendContext();
        SuspendContextImpl suspendContextImpl = (SuspendContextImpl) suspendContext;
        MethodWatchContext.xStackFrame = session.getCurrentStackFrame();
        MethodWatchContext.xExecutionStack = ((XDebugSessionImpl) MethodWatchContext.xDebugSession).getCurrentExecutionStack();
        MethodWatchContext.debugProcess = session.getDebugProcess();
        MethodWatchContext.xDebugSession2 = MethodWatchContext.debugProcess.getSession();
        MethodWatchContext.executionEnvironment = ((XDebugSessionImpl) session).getExecutionEnvironment();
        MethodWatchContext.runContentDescriptor = ((XDebugSessionImpl) session).getRunContentDescriptor();
        XDebugProcess xdebugProcess = session.getDebugProcess();
        DebuggerSession debuggerSession = ((JavaDebugProcess) XDebuggerManager.getInstance(ProjectUtils.getCurrProject()).getCurrentSession().getDebugProcess()).getDebuggerSession();
        //((JvmDropFrameActionHandler)session.getDebugProcess().getDropFrameHandler())//拿myDebuggerSession
        MethodWatchContext.suspendContext = (SuspendContextImpl) session.getSuspendContext();
        //new DebuggerSession().getContextManager().addListener();
        //com.intellij.debugger.engine.DebugProcessListener
        //DebuggerManagerEx.getInstance(e.getProject()).addDebugProcessListener();
        XDebugSessionListener xDebugSessionListener = new XDebugSessionListener(){
            @Override
            public void sessionResumed() {
                System.out.println("a");
                XDebugSession session = DebuggerUIUtil.getSession(e);
                if (session.getSuspendContext()!=null) {
                    MethodWatchContext.suspendContext = (SuspendContextImpl) session.getSuspendContext();
                }
                session.getCurrentStackFrame();
                //null
                //session.getCurrentPosition().getLine();
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
            }
        };
        session.addSessionListener(xDebugSessionListener);

        //DebuggerManagerEx.getInstanceEx(e.getProject()).getSession(debugProcess);

//        XExecutionStack activeExecutionStack = session.getSuspendContext().getActiveExecutionStack();
//        XExecutionStack[] executionStacks = session.getSuspendContext().getExecutionStacks();
//        for (XExecutionStack executionStack : executionStacks) {
//            executionStack.getTopFrame();
//        }

        /**
         * 任务名
         * session.getCurrentStackFrame().getSourcePosition().getFile()
         * session.getCurrentStackFrame().getSourcePosition().getLine()
         *
         *
         *
         *
         */
    }
}
