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
import org.jetbrains.annotations.NotNull;

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
    
    public static XStackContext getInstance(@NotNull XDebugSession xDebugSession){
        XStackContext xStackContext = null;
        try {
            xStackContext = new XStackContext(xDebugSession);
        }catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
        return xStackContext;
    }

    private XStackContext(@NotNull XDebugSession xDebugSession) {
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
        this.currentXStackBreakpointList =  getXStackBreakpointList(xBreakpointList);
    }

    private List<XStackBreakpoint> getXStackBreakpointList(List<XBreakpoint<?>> xBreakpointList) {
        if (xBreakpointList==null || xBreakpointList.isEmpty() || myXStackFrameList==null || myXStackFrameList.isEmpty() || currentXStackFrame==null) {
            return new ArrayList<>();
        }
        List<XStackBreakpoint> result = new ArrayList<>();
        //是当前栈帧底部
        boolean isBottomCurrentXStackFrame = false;
        //栈顶到栈底
        for (int i = 0; i < myXStackFrameList.size(); i++) {
            XStackFrame xStackFrame = myXStackFrameList.get(i);
            if (xStackFrame.equals(currentXStackFrame)) {
                isBottomCurrentXStackFrame = true;
            }
            int j = i + 1;
            int xStackFramePosition = myXStackFrameList.size()-i;
            XStackFrame bottomXStackFrame = null;
            if (j < myXStackFrameList.size()) {
                //当前遍历栈帧下方栈帧
                bottomXStackFrame = myXStackFrameList.get(j);
            }
            //获取当前栈帧方法区
            XStackFrameMethod xStackFrameMethod = StackFrameUtils.getXStackFrameMethod(xStackFrame, myProject);
            if (xStackFrameMethod == null) {
                continue;
            }
            myXStackFrameMethodMap.put(xStackFrame, xStackFrameMethod);
            List<XStackBreakpoint> XStackBreakpoints = getMethodXStackBreakpointList(
                    isBottomCurrentXStackFrame,
                    xStackFramePosition,
                    xStackFrame,
                    bottomXStackFrame,
                    xStackFrameMethod,
                    xBreakpointList
            );
            result.addAll(XStackBreakpoints);

        }
        //本身就是倒序,所以不需要反转
        //return CollUtil.reverse(result);
        return result;
    }

    private List<XStackBreakpoint> getMethodXStackBreakpointList(
            boolean isBottomCurrentXStackFrame,
            int xStackFramePosition,
            XStackFrame xStackFrame,
            XStackFrame bottomXStackFrame,
            XStackFrameMethod xStackFrameMethod,
            List<XBreakpoint<?>> xBreakpointList) {
        //按方法范围取断点
        List<XBreakpoint<?>> methodXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().getUrl().equals(xStackFrameMethod.getVirtualFile().getUrl())
                    && sourcePosition.getLine() >= xStackFrameMethod.getStart()
                    && sourcePosition.getLine() <= xStackFrameMethod.getLine()) {
                //有回退断点~代码块含多个
                methodXBreakpointList.add(xBreakpoint);
            }
        }
        //降序
        methodXBreakpointList.sort((o1,o2)->o2.getSourcePosition().getLine() - o1.getSourcePosition().getLine());
        //降序构造
        List<XStackBreakpoint> xStackBreakpointList = buildMethodXStackBreakpointList(
                methodXBreakpointList,
                isBottomCurrentXStackFrame,
                xStackFramePosition,
                xStackFrame,
                bottomXStackFrame,
                xStackFrameMethod
                );
        return xStackBreakpointList;
    }

    private List<XStackBreakpoint> buildMethodXStackBreakpointList(
            List<XBreakpoint<?>> methodXBreakpointList,
            boolean isBottomCurrentXStackFrame,
            int xStackFramePosition,
            XStackFrame xStackFrame,
            XStackFrame bottomXStackFrame,
            XStackFrameMethod xStackFrameMethod) {
        List<XStackBreakpoint> result = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : methodXBreakpointList) {
            XStackBreakpoint xStackBreakpoint = new XStackBreakpoint(
                    isBottomCurrentXStackFrame,
                    xStackFramePosition,
                    xStackFrame,
                    currentXStackFrame,
                    bottomXStackFrame,
                    xStackFrameMethod,
                    xBreakpoint
            );
            result.add(xStackBreakpoint);
        }
        return result;
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
