package com.runtime.pivot.plugin.model;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ReflectUtil;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.enums.RuntimeBreakpointType;
import com.runtime.pivot.plugin.utils.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.StackFrameUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


//TODO 执行器+当前栈信息

public class RuntimeContext {
    public static final String popFrame = "Debugger.PopFrame";
    public static final String resume = "Resume";
    //断点列表
    private final List<XBreakpoint<?>> xBreakpointList;
    private List<BacktrackingBreakpoint> backtrackingBreakpointList;
    //栈帧列表
    private final List<XStackFrame> xStackFrameList;
    //当前选中栈帧
    private final XStackFrame currentXStackFrame;

    //TODO 防止断点直接放过 or 回溯点处加判断是否可执行
    //回溯前执行到的临时断点
    private final XBreakpoint<?> currentXBreakpoint = null;
    private final Project project;
    private final XDebugSession xDebugSession;
    private final DebuggerSession debugSession;
    private final DebugProcessImpl debugProcess;
    //要回溯到的断点
    private XBreakpoint<?> backtrackingXBreakpoint;
    //要回溯到的栈帧
    private XStackFrame backtrackingXStackFrame;
    //最底栈帧
    private XStackFrame endXStackFrame;
    //栈帧=>方法锚点{文件,psiMethod,开始,结束,当前位置}
    private Map<XStackFrame, MethodAnchoring> xStackFrameMethodAnchoringMap = new ConcurrentHashMap<>();
    
    public static RuntimeContext getInstance(XDebugSession xDebugSession){
        return RuntimeContext.getInstance(xDebugSession);
    }

    public void buildMethodBacktrackingStack(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrameList, Project project) {
        if (xStackFrameList != null && xStackFrameList.size() > 1) {
            //TODO 当前栈是否可pop
            //栈底到栈顶遍历
            /**
             * 因为当前栈帧指的是选中的栈帧而不是走到的栈帧,所以做一个过滤
             * - 换成断点列表应该是不需要的,除非有当前断点变化监听器
             * - stackFrameChanged
             */
            boolean mark = false;
            Stack<XStackFrame> stack = new Stack<>();
            for (int i = xStackFrameList.size() - 1; i >= 0; i--) {
                XStackFrame xStackFrame = xStackFrameList.get(i);
                if (xStackFrame.equals(currentXStackFrame)) {
                    stack.push(xStackFrame);
                    mark = true;
                    break;
                } else {
                    //d1->d2->d3
                    stack.push(xStackFrame);
                }
            }
            if (!mark) {
                //当前栈帧列表没有选中栈帧=>TODO 多线程调试?
                return;
            }
            //栈顶到栈底遍历
            while (!stack.empty()) {
                XStackFrame xStackFrame = stack.pop();
                MethodAnchoring methodAnchoring = StackFrameUtils.getMethodAnchoring(xStackFrame, project);
                xStackFrameMethodAnchoringMap.put(xStackFrame, methodAnchoring);
                XBreakpoint<?> xBreakpoint = getBacktrackingXBreakpoint(methodAnchoring, xBreakpointList);
                if (xBreakpoint != null) {
                    //xBreakpoint.isEnabled() 改写其他的断点状态,监听结束需要改写回来 传入一个List => 先set:false,恢复,set:true
                    this.backtrackingXBreakpoint = xBreakpoint;
                    this.backtrackingXStackFrame = xStackFrame;
                    if (!stack.empty()) {
                        this.endXStackFrame = stack.pop();
                    }
                    break;
                }
            }
        }
    }

    public boolean isBacktracking() {
        return backtrackingXStackFrame != null
                && backtrackingXBreakpoint != null
                && endXStackFrame != null;
    }

    public void popFrameCommonRunnable() {
        xDebugSession.getDebugProcess().getDropFrameHandler().drop(backtrackingXStackFrame);
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

    private XBreakpoint<?> getBacktrackingXBreakpoint(MethodAnchoring methodAnchoring, List<XBreakpoint<?>> xBreakpointList) {
        XBreakpoint<?> backtrackingXBreakpoint = null;
        if (xBreakpointList == null || xBreakpointList.isEmpty()) {
            return backtrackingXBreakpoint;
        }
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().getUrl().equals(methodAnchoring.getVirtualFile().getUrl())
                    && sourcePosition.getLine() >= methodAnchoring.getStart()
                    && sourcePosition.getLine() <= methodAnchoring.getLine()) {
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

    private RuntimeContext(XDebugSession xDebugSession) {
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(xDebugSession.getProject());
        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
        List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream()
                .filter(bean -> bean.isEnabled())
                .collect(Collectors.toList());
        List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(xDebugSession);

        this.xBreakpointList = xBreakpointList;
        this.xStackFrameList = xStackFrames;
        this.currentXStackFrame = xDebugSession.getCurrentStackFrame();
        this.project = xDebugSession.getProject();
        this.xDebugSession = xDebugSession;
        this.debugSession = buildDebugSession(xDebugSession);
        this.debugProcess = debugSession.getProcess();
        //取所有的可回溯断点
        this.backtrackingBreakpointList = buildBacktrackingXBreakpointList(xBreakpointList, xStackFrameList, project);
        //取最近的可回溯断点
        buildMethodBacktrackingStack(xBreakpointList, xStackFrameList, project);
    }

    private List<BacktrackingBreakpoint> buildBacktrackingXBreakpointList(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrameList, Project project) {
        List<BacktrackingBreakpoint> result = new ArrayList<>();
        boolean mark = false;
        Stack<XStackFrame> stack = new Stack<>();
        for (int i = xStackFrameList.size() - 1; i >= 0; i--) {
            XStackFrame xStackFrame = xStackFrameList.get(i);
            if (xStackFrame.equals(currentXStackFrame)) {
                stack.push(xStackFrame);
                mark = true;
                break;
            } else {
                //d1->d2->d3
                stack.push(xStackFrame);
            }
        }
        if (!mark) {
            //当前栈帧列表没有选中栈帧=>TODO 多线程调试?
            return result;
        }
        //栈顶到栈底遍历
        while (!stack.empty()) {
            XStackFrame xStackFrame = stack.pop();
            XStackFrame bottomXStackFrame = null;
            if (!stack.empty()) {
                bottomXStackFrame = stack.peek();
            }
            MethodAnchoring methodAnchoring = StackFrameUtils.getMethodAnchoring(xStackFrame, project);
            xStackFrameMethodAnchoringMap.put(xStackFrame, methodAnchoring);
            //获取当前栈帧的全部断点
            List<BacktrackingBreakpoint> backtrackingBreakpoints = getBacktrackingXBreakpointWithMethodAnchoring(xStackFrame, bottomXStackFrame, methodAnchoring, xBreakpointList);
            //往后添加
            result.addAll(backtrackingBreakpoints);
        }
        //本身就是倒序,所以不需要反转
        //return CollUtil.reverse(result);
        return result;
    }

    private List<BacktrackingBreakpoint> getBacktrackingXBreakpointWithMethodAnchoring(XStackFrame xStackFrame, XStackFrame bottomXStackFrame, MethodAnchoring methodAnchoring, List<XBreakpoint<?>> xBreakpointList) {
        List<BacktrackingBreakpoint> result = new ArrayList<>();
        if (xBreakpointList == null || xBreakpointList.isEmpty()) {
            return result;
        }
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        //当前断点位置在栈帧方法的区域内
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().getUrl().equals(methodAnchoring.getVirtualFile().getUrl())
                    && sourcePosition.getLine() >= methodAnchoring.getStart()
                    && sourcePosition.getLine() <= methodAnchoring.getLine()) {
                //有回退断点~代码块含多个
                regressionXBreakpointList.add(xBreakpoint);
            }
        }
        // TODO 多个回退断点
        if (!regressionXBreakpointList.isEmpty()) {
            //降序
            regressionXBreakpointList.sort(new Comparator<XBreakpoint<?>>() {
                //从大到小   654 321 ...
                @Override
                public int compare(XBreakpoint<?> o1, XBreakpoint<?> o2) {
                    return o2.getSourcePosition().getLine() - o1.getSourcePosition().getLine();
                }
            });
            //降序构造前缀断点~哪些是这个断点的前缀,前缀范围可以是当前栈,也可以是上一个栈
            for (int i = 0; i < regressionXBreakpointList.size(); i++) {
                XBreakpoint<?> breakpoint = regressionXBreakpointList.get(i);
                List<XBreakpoint<?>> sub = ListUtil.sub(regressionXBreakpointList, i + 1, regressionXBreakpointList.size());
                BacktrackingBreakpoint bean = new BacktrackingBreakpoint(
                        debugProcess,
                        xDebugSession,
                        breakpoint,
                        //TODO 做可达性分析~可执行区断点
                        accessibilityAnalysis(currentXStackFrame,breakpoint,bottomXStackFrame),
                        xStackFrame,
                        bottomXStackFrame,
                        sub,
                        breakpoint.getSourcePosition()
                );
                result.add(bean);
            }
        }
        return result;
    }

    private RuntimeBreakpointType accessibilityAnalysis(XStackFrame currentXStackFrame, XBreakpoint<?> breakpoint, XStackFrame endXStackFrame) {
        if (breakpoint.getSourcePosition().getFile().getUrl().equals(currentXStackFrame.getSourcePosition().getFile().getUrl())
                        && breakpoint.getSourcePosition().getLine()==(currentXStackFrame.getSourcePosition().getLine())) {
            return RuntimeBreakpointType.NOT_AVAILABLE;
        }
        if (endXStackFrame==null){
            return RuntimeBreakpointType.NOT_AVAILABLE;
        }
        return RuntimeBreakpointType.AVAILABLE;
    }

    private DebuggerSession buildDebugSession(XDebugSession xDebugSession) {
        XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
        DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(dropFrameHandler, "myDebugSession");
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

    public XBreakpoint<?> getBacktrackingXBreakpoint() {
        return backtrackingXBreakpoint;
    }

    public XStackFrame getBacktrackingXStackFrame() {
        return backtrackingXStackFrame;
    }

    public XStackFrame getEndXStackFrame() {
        return endXStackFrame;
    }

    public Map<XStackFrame, MethodAnchoring> getxStackFrameMethodAnchoringMap() {
        return xStackFrameMethodAnchoringMap;
    }

    public List<BacktrackingBreakpoint> getBacktrackingXBreakpointList() {
        return backtrackingBreakpointList;
    }
}
