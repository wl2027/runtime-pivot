package com.runtime.pivot.plugin.actions.method;

import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.BacktrackingXBreakpoint;
import com.runtime.pivot.plugin.domain.MethodBacktrackingContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.view.method.BreakpointListDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;


public class BreakpointBacktrackingAction extends AnAction {


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession xDebugSession = DebuggerUIUtil.getSession(e);
        MethodBacktrackingContext methodBacktrackingContext = new MethodBacktrackingContext(xDebugSession);
        List<BacktrackingXBreakpoint> backtrackingXBreakpointList = methodBacktrackingContext.getBacktrackingXBreakpointList();
        BreakpointListDialog breakpointListDialog = RuntimePivotMethodService.getInstance(e.getProject()).getSessionBreakpointListMap().get(xDebugSession);
        Project project = e.getProject();
        if (breakpointListDialog == null || !breakpointListDialog.isVisible()) {
            breakpointListDialog = new BreakpointListDialog(project, DebuggerUIUtil.getSession(e),backtrackingXBreakpointList);
            RuntimePivotMethodService.getInstance(e.getProject()).getSessionBreakpointListMap().put(xDebugSession,breakpointListDialog);
            breakpointListDialog.setVisible(true);
        } else {
            breakpointListDialog.updateListData(backtrackingXBreakpointList);
            breakpointListDialog.setVisible(true);
            //TODO 提示已存在监听器
        }
        XDebuggerManagerListener xDebuggerManagerListener = new XDebuggerManagerListener(){
            @Override
            public void processStopped(@NotNull XDebugProcess debugProcess) {
                XDebugSession session = debugProcess.getSession();
                BreakpointListDialog breakpointListDialog1 = RuntimePivotMethodService.getInstance(project).getSessionBreakpointListMap().get(session);
                if (breakpointListDialog1!=null) {
                    breakpointListDialog1.close();
                }
            }
        };
        e.getProject().getMessageBus().connect().subscribe(XDebuggerManager.TOPIC,xDebuggerManagerListener);
        //一定要
        //TODO 所有操作都要判断是否存在会话和弹窗
        XBreakpointListener xBreakpointListener = new XBreakpointListener(){

            private void updateData() {
                //if (true) return;
                //java.lang.Throwable: AWT events are not allowed inside write action: java.awt.event.FocusEvent[FOCUS_LOST,temporary,opposite=null,cause=ACTIVATION] on EditorComponent file=file
                XDebugSession currentSession = XDebuggerManager.getInstance(project).getCurrentSession();
                MethodBacktrackingContext methodBacktrackingContext = new MethodBacktrackingContext(currentSession);
                BreakpointListDialog breakpointListDialog1 = RuntimePivotMethodService.getInstance(e.getProject()).getSessionBreakpointListMap().get(currentSession);
                breakpointListDialog1.updateListData(methodBacktrackingContext.getBacktrackingXBreakpointList());
            }

            @Override
            public void breakpointAdded(@NotNull XBreakpoint breakpoint) {
                //updateData();
                XBreakpointListener.super.breakpointAdded(breakpoint);
            }

            @Override
            public void breakpointRemoved(@NotNull XBreakpoint breakpoint) {
                //updateData();//没必要重构,只需要排除
                XDebugSession currentSession = XDebuggerManager.getInstance(project).getCurrentSession();
                BreakpointListDialog breakpointListDialog1 = RuntimePivotMethodService.getInstance(e.getProject()).getSessionBreakpointListMap().get(currentSession);
                List<BacktrackingXBreakpoint> collect = breakpointListDialog1.getBacktrackingXBreakpointList().stream().filter(
                        bean -> !bean.getxBreakpoint().equals(breakpoint)
//                        bean-> !RuntimePivotUtil.compareBreakpoints(bean.getxBreakpoint(),breakpoint)
                ).collect(Collectors.toList());
                breakpointListDialog1.updateListData(collect);
                XBreakpointListener.super.breakpointRemoved(breakpoint);
            }

            @Override
            public void breakpointChanged(@NotNull XBreakpoint breakpoint) {
                //改变的是breakpoint.isEnabled()才去调用
                //updateData(); //断点增删没有调用,断点属性修改会调用 主要关注isEnable
                XDebugSession currentSession = XDebuggerManager.getInstance(project).getCurrentSession();
                BreakpointListDialog breakpointListDialog1 = RuntimePivotMethodService.getInstance(e.getProject()).getSessionBreakpointListMap().get(currentSession);
                breakpointListDialog1.getBacktrackingXBreakpointList().forEach(BacktrackingXBreakpoint::updateType);
                breakpointListDialog1.updateListData(breakpointListDialog1.getBacktrackingXBreakpointList());
                XBreakpointListener.super.breakpointChanged(breakpoint);
            }
            //断点视图已更新
            @Override
            public void breakpointPresentationUpdated(@NotNull XBreakpoint breakpoint, @Nullable XDebugSession session) {
                //updateData();//调试会话启动也会调用,因为也属于断点视图更新
                XBreakpointListener.super.breakpointPresentationUpdated(breakpoint, session);
            }
        };
        e.getProject().getMessageBus().connect().subscribe(XBreakpointListener.TOPIC,xBreakpointListener);
    }

}
