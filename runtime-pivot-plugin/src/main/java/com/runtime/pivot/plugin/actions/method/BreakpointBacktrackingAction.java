package com.runtime.pivot.plugin.actions.method;

import cn.hutool.core.collection.ListUtil;
import com.intellij.debugger.actions.DebuggerAction;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.ui.impl.watch.DebuggerTreeNodeImpl;
import com.intellij.debugger.ui.impl.watch.NodeDescriptorImpl;
import com.intellij.debugger.ui.impl.watch.StackFrameDescriptorImpl;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.actions.method.test2.StackFrameUtils;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
public class BreakpointBacktrackingAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession xDebugSession = DebuggerUIUtil.getSession(e);
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(e.getProject());
        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
        List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream().filter(bean -> bean.isEnabled()).collect(Collectors.toList());
        List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(DebuggerUIUtil.getSession(e));
        MethodBacktrackingContext methodBacktrackingContext = new MethodBacktrackingContext(
                xBreakpointList,
                xStackFrames,
                xDebugSession
        );
        StackFrameUtils.invokeBacktracking(methodBacktrackingContext);


//        if (methodBacktrackingContext.isBacktracking()){
//            StackFrameUtils.invokeBacktracking(methodBacktrackingContext);
//        }else {
//            //TODO 不可回溯+给原因
//        }

    }

//    private XBreakpoint<?> breakpointRestore(AnActionEvent e,
//                                             XDebugSession xDebugSession,
//                                             List<XStackFrame> xStackFrames,
//                                             List<XBreakpoint<?>> xBreakpointList) {
//        //TODO 改成命令队列,防止resetFrame错误
//        if (xStackFrames.size() <= 1) {
//            return null;
//        }
//        //xStackFrames 每个栈帧执行到的位置,不是断点位置
//        for (XStackFrame xStackFrame : xStackFrames) {
//            //methodRange [方法前一行,方法语句最后一行]
//            StackFrameUtils.Range<Integer> methodRange = StackFrameUtils.getMethodRange(e.getProject(), xStackFrame);
//            System.out.println(xStackFrame + ":" + methodRange);
//            XBreakpoint<?> regressionXBreakpoint = getRegressionXBreakpoint(xStackFrame, methodRange, xBreakpointList);
//            if (regressionXBreakpoint != null) {
//                XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
//                JvmDropFrameActionHandler jvmDropFrameActionHandler = (JvmDropFrameActionHandler) dropFrameHandler;
//                DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(jvmDropFrameActionHandler, "myDebugSession");
//                DebugProcessImpl myDebugProcess = myDebugSession.getProcess();
//                try {
//                    DebuggerContextCommandImpl popFrameCommand = (DebuggerContextCommandImpl) myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(), getStackFrame(e).getStackFrameProxy());
//                    popFrameCommand.threadAction(myDebugProcess.getDebuggerContext().getSuspendContext());
//                    //popFrameCommand.run();
//
//                } catch (Exception ex) {
//                    throw new RuntimeException(ex);
//                }
//                ApplicationManager.getApplication().executeOnPooledThread(()->{
//                    while (isResetFrame(xStackFrame,xDebugSession)){
//                        ThreadUtil.sleep(10);
//                    }
//                    try {
//                        myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();
//                    } catch (Exception ex) {
//                        throw new RuntimeException(ex);
//                    }
//                    //myDebugProcess.getManagerThread().invoke(myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),getStackFrame(e).getStackFrameProxy()));
//                    //myDebugProcess.getManagerThread().invoke(myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()));
//
//                    //myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),getStackFrame(e).getStackFrameProxy()).run();
//                    //myDebugProcess.onHotSwapFinished();
//                    //myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();
//                    //resumeAction.actionPerformed(e);
//                });
//                return regressionXBreakpoint;
//            } else {
//                //resetFrameAction.actionPerformed(e);
//            }
//        }
//        return null;
//    }

    static JavaStackFrame getStackFrame(AnActionEvent e) {
        StackFrameDescriptorImpl descriptor = getSelectedStackFrameDescriptor(e);
        if (descriptor != null) {
            return new JavaStackFrame(descriptor, false);
        }
        return getSelectedStackFrame(e);
    }
    @Nullable
    private static XDropFrameHandler getDropFrameHandler(@NotNull AnActionEvent e) {
        var xSession = DebuggerUIUtil.getSession(e);
        return Optional.ofNullable(xSession)
                .map(XDebugSession::getDebugProcess)
                .map(XDebugProcess::getDropFrameHandler)
                .orElse(null);
    }

    @Nullable
    private static JavaStackFrame getSelectedStackFrame(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            XDebugSession session = DebuggerUIUtil.getSession(e);
            if (session != null) {
                XStackFrame frame = session.getCurrentStackFrame();
                if (frame instanceof JavaStackFrame) {
                    return ((JavaStackFrame)frame);
                }
            }
        }
        return null;
    }
    @Nullable
    private static StackFrameDescriptorImpl getSelectedStackFrameDescriptor(AnActionEvent e) {
        DebuggerTreeNodeImpl selectedNode = DebuggerAction.getSelectedNode(e.getDataContext());
        if(selectedNode != null) {
            NodeDescriptorImpl descriptor = selectedNode.getDescriptor();
            if(descriptor instanceof StackFrameDescriptorImpl) {
                return (StackFrameDescriptorImpl)descriptor;
            }
        }
        return null;
    }

    private boolean isResetFrame(XStackFrame xStackFrame, XDebugSession xDebugSession) {
        boolean b = xDebugSession.getCurrentStackFrame().equals(xStackFrame);
        System.out.println(b);
        return b;
    }

//    private XBreakpoint<?> getRegressionXBreakpoint(XStackFrame xStackFrame, StackFrameUtils.Range<Integer> methodRange, List<XBreakpoint<?>> xBreakpointList) {
//        XBreakpoint<?> regressionXBreakpoint = null;
//        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
//        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
//            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
//            if (sourcePosition.getFile().equals(methodRange.getVirtualFile()) && sourcePosition.getLine() >= methodRange.getStart() && sourcePosition.getLine() <= xStackFrame.getSourcePosition().getLine()) {
//                //有回退断点~代码块含多个
//                regressionXBreakpointList.add(xBreakpoint);
//            }
//        }
//        if (!regressionXBreakpointList.isEmpty()) {
//            regressionXBreakpoint = regressionXBreakpointList.get(0);
//            for (XBreakpoint<?> xBreakpoint : regressionXBreakpointList) {
//                if (regressionXBreakpoint.getSourcePosition().getLine() < xBreakpoint.getSourcePosition().getLine()) {
//                    regressionXBreakpoint = xBreakpoint;
//                }
//            }
//        }
//        return regressionXBreakpoint;
//    }
}
