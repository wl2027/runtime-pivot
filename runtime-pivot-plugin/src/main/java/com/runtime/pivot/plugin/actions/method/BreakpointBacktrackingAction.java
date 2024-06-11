package com.runtime.pivot.plugin.actions.method;

import cn.hutool.core.collection.ListUtil;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.StackFrameUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;


public class BreakpointBacktrackingAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession xDebugSession = DebuggerUIUtil.getSession(e);
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(e.getProject());
        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
        XStackFrame currentStackFrame = xDebugSession.getCurrentStackFrame();
        List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream()
                .filter(bean -> bean.isEnabled())
                .filter(bean->!(
                        bean.getSourcePosition().getFile().getUrl().equals(currentStackFrame.getSourcePosition().getFile().getUrl())
                        && bean.getSourcePosition().getLine()==(currentStackFrame.getSourcePosition().getLine())
                        ))
                .collect(Collectors.toList());

        List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(DebuggerUIUtil.getSession(e));
        //TODO currentStackFrame 当前栈帧指的是选中的,而不是定点栈帧,先要到达选中栈帧再开始执行命令
        MethodBacktrackingContext methodBacktrackingContext = new MethodBacktrackingContext(
                xBreakpointList,
                xStackFrames,
                xDebugSession
        );
        if (!methodBacktrackingContext.isBacktracking()) {
            //TODO无法回溯
        }
        XSourcePosition sourcePosition = methodBacktrackingContext.getBacktrackingXBreakpoint().getSourcePosition();
        sourcePosition.createNavigatable(e.getProject()).navigate(true);
        //弹出确认回溯点
        if (MessageUtil.showYesNoDialog("确认回溯点",
                sourcePosition.getFile().getName()+":"+sourcePosition.getLine(),
                e.getProject(),
                "确认",
                "取消",
                null)
        ){
            //断点列表-》{断点+断点类型+pop方法栈+pop方法栈的其他断点List+end方法栈}
            StackFrameUtils.invokeBacktracking(methodBacktrackingContext);
        }
//        if (methodBacktrackingContext.isBacktracking()){
//            StackFrameUtils.invokeBacktracking(methodBacktrackingContext);
//        }else {
//            //TODO 不可回溯+给原因
//        }

    }

}
