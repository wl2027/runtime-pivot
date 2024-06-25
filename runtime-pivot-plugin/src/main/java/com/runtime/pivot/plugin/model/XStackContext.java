package com.runtime.pivot.plugin.model;

import cn.hutool.core.collection.ListUtil;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.frame.XStackFrame;
import com.runtime.pivot.plugin.enums.XStackBreakpointType;
import com.runtime.pivot.plugin.utils.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.StackFrameUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class XStackContext {
    //栈帧列表
    private List<XStackFrame> myXStackFrameList;
    //当前选中栈帧
    private XStackFrame currentXStackFrame;
    //当前选中栈的断点列表
    private List<XStackBreakpoint> currentXStackBreakpointList;
    //当前项目
    private Project myProject;
    //当前调试会话
    private XDebugSession myXDebugSession;
    //栈帧方法
    private Map<XStackFrame, XStackFrameMethod> myXStackFrameMethodMap = new ConcurrentHashMap<>();
    
    public static XStackContext getInstance(XDebugSession xDebugSession){
        return XStackContext.getInstance(xDebugSession);
    }


    private XStackContext(XDebugSession xDebugSession) {
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(xDebugSession.getProject());
        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
        List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream()
                .filter(bean -> bean.isEnabled())
                .collect(Collectors.toList());
        List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(xDebugSession);
        this.myXStackFrameList = xStackFrames;
        this.currentXStackFrame = xDebugSession.getCurrentStackFrame();
        this.myProject = xDebugSession.getProject();
        this.myXDebugSession = xDebugSession;
        //获取调用栈断点列表
        this.currentXStackBreakpointList =  buildXStackBreakpointList(xBreakpointList,myXStackFrameList,myProject);
        //======================================
        this.currentXStackBreakpointList = buildBacktrackingXBreakpointList(xBreakpointList, xStackFrameList, project);
        buildMethodBacktrackingStack(xBreakpointList, xStackFrameList, project);
    }

    private List<XStackBreakpoint> buildXStackBreakpointList(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrames, Project project) {

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
                XStackFrameMethod XStackFrameMethod = StackFrameUtils.getXStackFrameMethod(xStackFrame, project);
                XStackFrameAnchoringMap.put(xStackFrame, XStackFrameMethod);
                XBreakpoint<?> xBreakpoint = getBacktrackingXBreakpoint(XStackFrameMethod, xBreakpointList);
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


    private XBreakpoint<?> getBacktrackingXBreakpoint(XStackFrameMethod XStackFrameMethod, List<XBreakpoint<?>> xBreakpointList) {
        XBreakpoint<?> backtrackingXBreakpoint = null;
        if (xBreakpointList == null || xBreakpointList.isEmpty()) {
            return backtrackingXBreakpoint;
        }
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().getUrl().equals(XStackFrameMethod.getVirtualFile().getUrl())
                    && sourcePosition.getLine() >= XStackFrameMethod.getStart()
                    && sourcePosition.getLine() <= XStackFrameMethod.getLine()) {
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


    private List<XStackBreakpoint> buildBacktrackingXBreakpointList(List<XBreakpoint<?>> xBreakpointList, List<XStackFrame> xStackFrameList, Project project) {
        List<XStackBreakpoint> result = new ArrayList<>();
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
            XStackFrameMethod XStackFrameMethod = StackFrameUtils.getXStackFrameMethod(xStackFrame, project);
            XStackFrameAnchoringMap.put(xStackFrame, XStackFrameMethod);
            List<XStackBreakpoint> XStackBreakpoints = getBacktrackingXBreakpointWithMethodAnchoring(xStackFrame, bottomXStackFrame, XStackFrameMethod, xBreakpointList);
            //
            result.addAll(XStackBreakpoints);
        }
        //本身就是倒序,所以不需要反转
        //return CollUtil.reverse(result);
        return result;
    }

    private List<XStackBreakpoint> getBacktrackingXBreakpointWithMethodAnchoring(XStackFrame xStackFrame, XStackFrame bottomXStackFrame, XStackFrameMethod XStackFrameMethod, List<XBreakpoint<?>> xBreakpointList) {
        List<XStackBreakpoint> result = new ArrayList<>();
        if (xBreakpointList == null || xBreakpointList.isEmpty()) {
            return result;
        }
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().getUrl().equals(XStackFrameMethod.getVirtualFile().getUrl())
                    && sourcePosition.getLine() >= XStackFrameMethod.getStart()
                    && sourcePosition.getLine() <= XStackFrameMethod.getLine()) {
                //有回退断点~代码块含多个
                regressionXBreakpointList.add(xBreakpoint);
            }
        }
        // TODO 多个回退断点
        if (!regressionXBreakpointList.isEmpty()) {
            //降序
            regressionXBreakpointList.sort(new Comparator<XBreakpoint<?>>() {
                @Override
                public int compare(XBreakpoint<?> o1, XBreakpoint<?> o2) {
                    return o2.getSourcePosition().getLine() - o1.getSourcePosition().getLine();
                }
            });
            //降序构造
            for (int i = 0; i < regressionXBreakpointList.size(); i++) {
                XBreakpoint<?> breakpoint = regressionXBreakpointList.get(i);
                List<XBreakpoint<?>> sub = ListUtil.sub(regressionXBreakpointList, i + 1, regressionXBreakpointList.size());
                XStackBreakpoint bean = new XStackBreakpoint(
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

    private XStackBreakpointType accessibilityAnalysis(XStackFrame currentXStackFrame, XBreakpoint<?> breakpoint, XStackFrame endXStackFrame) {
        if (breakpoint.getSourcePosition().getFile().getUrl().equals(currentXStackFrame.getSourcePosition().getFile().getUrl())
                        && breakpoint.getSourcePosition().getLine()==(currentXStackFrame.getSourcePosition().getLine())) {
            return XStackBreakpointType.NOT_AVAILABLE;
        }
        if (endXStackFrame==null){
            return XStackBreakpointType.NOT_AVAILABLE;
        }
        return XStackBreakpointType.AVAILABLE;
    }

    public List<XStackFrame> getXStackFrameList() {
        return myXStackFrameList;
    }

    public XStackFrame getCurrentXStackFrame() {
        return currentXStackFrame;
    }

    public List<XStackBreakpoint> getCurrentXStackBreakpointList() {
        return currentXStackBreakpointList;
    }

    public Project getProject() {
        return myProject;
    }

    public XDebugSession getXDebugSession() {
        return myXDebugSession;
    }

    public Map<XStackFrame, XStackFrameMethod> getXStackFrameMethodMap() {
        return myXStackFrameMethodMap;
    }
}
