package com.runtime.pivot.plugin.domain;

import cn.hutool.core.util.ReflectUtil;
import com.intellij.debugger.actions.DebuggerAction;
import com.intellij.debugger.actions.JvmDropFrameActionHandler;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.events.DebuggerContextCommandImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.ui.impl.watch.DebuggerTreeNodeImpl;
import com.intellij.debugger.ui.impl.watch.NodeDescriptorImpl;
import com.intellij.debugger.ui.impl.watch.StackFrameDescriptorImpl;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.actions.method.test2.StackFrameUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class MethodBacktrackingContext {
    private final List<XBreakpoint<?>> xBreakpointList;
    private final List<XStackFrame> xStackFrameList;
    private final XStackFrame currentXStackFrame;

    //TODO 防止断点直接放过 or 回溯点处加判断是否可执行
    private final XBreakpoint<?> currentXBreakpoint = null;//回溯前执行到的临时断点
    private final Project project;
    private final XDebugSession xDebugSession;
    private final DebuggerSession debugSession;
    private final DebugProcessImpl debugProcess;
    private XStackFrame currentBacktrackingXStackFrame;//当前回溯到的栈帧

    public static final String popFrame = "popFrame";
    public static final String resume = "resume";

    private Map<XStackFrame, Runnable> xStackFrameRunnableMap;
    private Queue<String> commandQueue = new LinkedList<>();
    private XBreakpoint<?> backtrackingXBreakpoint;//要回溯到的断点
    private XStackFrame backtrackingXStackFrame;//要回溯到的栈帧
    private XStackFrame endXStackFrame;//要回溯到的栈帧
    private Map<XStackFrame, MethodAnchoring> xStackFrameMethodAnchoringMap;//栈帧=>方法{文件,psiMethod,开始,结束,当前位置}

    public void buildMethodBacktrackingStack(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrameList, Project project) {
        this.xStackFrameMethodAnchoringMap = new ConcurrentHashMap<>();
        this.xStackFrameRunnableMap = new ConcurrentHashMap<>();
        if (xStackFrameList != null && xStackFrameList.size() > 1) {
            //栈顶到栈底遍历
            for (int i = 0; i < xStackFrameList.size() - 1; i++) {
                XStackFrame xStackFrame = xStackFrameList.get(i);
                MethodAnchoring methodAnchoring = StackFrameUtils.getMethodAnchoring(xStackFrame, project);
                xStackFrameMethodAnchoringMap.put(xStackFrame, methodAnchoring);
                XBreakpoint<?> xBreakpoint = getBacktrackingXBreakpoint(methodAnchoring, xBreakpointList);
                if (xBreakpoint != null) {
                    this.backtrackingXBreakpoint = xBreakpoint;
                    this.backtrackingXStackFrame = xStackFrame;
                    commandQueue.add(popFrame);
                    commandQueue.add(resume);
                    xStackFrameRunnableMap.put(xStackFrame, this::popFrameCommonRunnable);
                    endXStackFrame = xStackFrameList.get(i + 1);
                    xStackFrameRunnableMap.put(endXStackFrame, this::resumeCommonRunnable);
                    break;
                } else {
                    commandQueue.add(popFrame);
                    xStackFrameRunnableMap.put(xStackFrame, this::popFrameCommonRunnable);
                }
            }
        }
    }

    public void popFrameCommonRunnable() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                JavaStackFrame stackFrame = getStackFrame(xDebugSession);
                if (stackFrame==null||stackFrame.getStackFrameProxy().isBottom()) {
                    throw new RuntimeException();
                }
                DebuggerContextCommandImpl popFrameCommand = (DebuggerContextCommandImpl)debugProcess.createPopFrameCommand(debugProcess.getDebuggerContext(),stackFrame.getStackFrameProxy());
                popFrameCommand.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void resumeCommonRunnable() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            try {
                DebugProcessImpl.ResumeCommand resumeCommand = debugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext());
                resumeCommand.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    static JavaStackFrame getStackFrame(XDebugSession xDebugSession) {
        XStackFrame frame = xDebugSession.getCurrentStackFrame();
        if (frame instanceof JavaStackFrame) {
            return ((JavaStackFrame) frame);
        }
        return null;
    }


    public XStackFrame getEndXStackFrame() {
        return endXStackFrame;
    }

    public @Nullable String getCommand() {
        String command = commandQueue.poll();
        return command;
    }

    public Queue<String> getCommandQueue() {
        return commandQueue;
    }

    public XBreakpoint<?> getBacktrackingXBreakpoint() {
        return backtrackingXBreakpoint;
    }

    public XStackFrame getBacktrackingXStackFrame() {
        return backtrackingXStackFrame;
    }

    public Map<XStackFrame, MethodAnchoring> getxStackFrameMethodAnchoringMap() {
        return xStackFrameMethodAnchoringMap;
    }

    private XBreakpoint<?> getBacktrackingXBreakpoint(MethodAnchoring methodAnchoring, List<XBreakpoint<?>> xBreakpointList) {
        XBreakpoint<?> backtrackingXBreakpoint = null;
        if (xBreakpointList == null || xBreakpointList.isEmpty()) {
            return backtrackingXBreakpoint;
        }
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().equals(methodAnchoring.getVirtualFile()) && sourcePosition.getLine() >= methodAnchoring.getStart() && sourcePosition.getLine() <= methodAnchoring.getLine()) {
                //有回退断点~代码块含多个
                regressionXBreakpointList.add(xBreakpoint);
            }
        }
        //多个回退断点取最近
        // TODO 并且是可执行区断点
        if (!regressionXBreakpointList.isEmpty()) {
            backtrackingXBreakpoint = regressionXBreakpointList.get(0);
            for (XBreakpoint<?> xBreakpoint : regressionXBreakpointList) {
                if (backtrackingXBreakpoint.getSourcePosition().getLine() < xBreakpoint.getSourcePosition().getLine()) {
                    backtrackingXBreakpoint = xBreakpoint;
                }
            }
        }
        return backtrackingXBreakpoint;
    }


    public MethodBacktrackingContext(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrameList, XDebugSession xDebugSession) {
        this.xBreakpointList = xBreakpointList;
        this.xStackFrameList = xStackFrameList;
        this.currentXStackFrame = xDebugSession.getCurrentStackFrame();
        this.project = xDebugSession.getProject();
        this.xDebugSession = xDebugSession;
        this.debugSession = buildDebugSession(xDebugSession);
        this.debugProcess = debugSession.getProcess();
        buildMethodBacktrackingStack(xBreakpointList, xStackFrameList, project);
    }

    private DebuggerSession buildDebugSession(XDebugSession xDebugSession) {
        XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
        JvmDropFrameActionHandler jvmDropFrameActionHandler = (JvmDropFrameActionHandler) dropFrameHandler;
        DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(jvmDropFrameActionHandler, "myDebugSession");
        return myDebugSession;
    }

    public List<XBreakpoint<?>> getxBreakpointList() {
        return xBreakpointList;
    }

    public List<XStackFrame> getxStackFrameList() {
        return xStackFrameList;
    }

    public XStackFrame getCurrentXStackFrame() {
        return currentXStackFrame;
    }

    public XBreakpoint<?> getCurrentXBreakpoint() {
        return currentXBreakpoint;
    }

    public Project getProject() {
        return project;
    }

    public XDebugSession getxDebugSession() {
        return xDebugSession;
    }

    public DebuggerSession getDebugSession() {
        return debugSession;
    }

    public DebugProcessImpl getDebugProcess() {
        return debugProcess;
    }

    public XStackFrame getCurrentBacktrackingXStackFrame() {
        return currentBacktrackingXStackFrame;
    }

    public Map<XStackFrame, Runnable> getxStackFrameRunnableMap() {
        return xStackFrameRunnableMap;
    }
}
