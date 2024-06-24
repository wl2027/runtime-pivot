package com.runtime.pivot.plugin.listeners;

import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.runtime.pivot.plugin.model.BacktrackingBreakpoint;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.RuntimeBreakpointDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

//关闭UI+更新UI
public class RuntimeBreakpointDialogListener implements XDebuggerManagerListener, XBreakpointListener {

    public static void updateDialogData( XDebugSession xDebugSession) {
        //if (true) return;
        //java.lang.Throwable: AWT events are not allowed inside write action: java.awt.event.FocusEvent[FOCUS_LOST,temporary,opposite=null,cause=ACTIVATION] on EditorComponent file=file
        RuntimeContext runtimeContext = RuntimeContext.getInstance(xDebugSession);
        RuntimeBreakpointDialog runtimeBreakpointDialog = RuntimePivotMethodService
                .getInstance(xDebugSession.getProject())
                .getSessionRuntimeBreakpointDialogMap()
                .get(xDebugSession);
        if (runtimeBreakpointDialog!=null) {
            runtimeBreakpointDialog.updateListData(runtimeContext.getBacktrackingXBreakpointList());
        }
    }

    @Override
    public void processStarted(@NotNull XDebugProcess debugProcess) {
        XDebuggerManagerListener.super.processStarted(debugProcess);
    }

    @Override
    public void processStopped(@NotNull XDebugProcess debugProcess) {
        XDebugSession session = debugProcess.getSession();
        RuntimeBreakpointDialog runtimeBreakpointDialog = RuntimePivotMethodService
                .getInstance(debugProcess.getSession().getProject())
                .getSessionRuntimeBreakpointDialogMap()
                .get(session);
        if (runtimeBreakpointDialog !=null) {
            runtimeBreakpointDialog.close();
        }
    }

    @Override
    public void currentSessionChanged(@Nullable XDebugSession previousSession, @Nullable XDebugSession currentSession) {
        XDebuggerManagerListener.super.currentSessionChanged(previousSession, currentSession);
    }

    @Override
    public void breakpointAdded(@NotNull XBreakpoint breakpoint) {
        XDebugSession currentSession = XDebuggerManager.getInstance(ProjectUtils.getCurrProject()).getCurrentSession();
        updateDialogData(currentSession);
    }

    @Override
    public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {
        //updateDialogData();//没必要重构,只需要排除
        XDebugSession currentSession = XDebuggerManager.getInstance(ProjectUtils.getCurrProject()).getCurrentSession();
        RuntimeBreakpointDialog runtimeBreakpointDialog = RuntimePivotMethodService.getInstance(ProjectUtils.getCurrProject()).getSessionRuntimeBreakpointDialogMap().get(currentSession);
        List<BacktrackingBreakpoint> collect = runtimeBreakpointDialog.getBacktrackingXBreakpointList().stream().filter(
                bean -> !bean.getxBreakpoint().equals(breakpoint)
//                        bean-> !RuntimePivotUtil.compareBreakpoints(bean.getxBreakpoint(),breakpoint)
        ).collect(Collectors.toList());
        runtimeBreakpointDialog.updateListData(collect);
        XBreakpointListener.super.breakpointRemoved(breakpoint);
    }

    @Override
    public void breakpointChanged(@NotNull XBreakpoint breakpoint) {
        //改变的是breakpoint.isEnabled()才去调用
        //updateData(); //断点增删没有调用,断点属性修改会调用 主要关注isEnable
        XDebugSession currentSession = XDebuggerManager.getInstance(ProjectUtils.getCurrProject()).getCurrentSession();
        RuntimeBreakpointDialog runtimeBreakpointDialog = RuntimePivotMethodService.getInstance(ProjectUtils.getCurrProject()).getSessionRuntimeBreakpointDialogMap().get(currentSession);
        runtimeBreakpointDialog.getBacktrackingXBreakpointList().forEach(BacktrackingBreakpoint::updateType);
        runtimeBreakpointDialog.updateListData(runtimeBreakpointDialog.getBacktrackingXBreakpointList());
        XBreakpointListener.super.breakpointChanged(breakpoint);
    }

    @Override
    public void breakpointPresentationUpdated(@NotNull XBreakpoint breakpoint, @Nullable XDebugSession session) {
        //断点视图已更新
        //updateData();//调试会话启动也会调用,因为也属于断点视图更新
        XBreakpointListener.super.breakpointPresentationUpdated(breakpoint, session);
    }
}
