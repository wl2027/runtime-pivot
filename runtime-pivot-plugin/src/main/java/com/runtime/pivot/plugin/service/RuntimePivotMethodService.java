package com.runtime.pivot.plugin.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointListener;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.BacktrackingXBreakpoint;
import com.runtime.pivot.plugin.model.RuntimeContext;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.RuntimeBreakpointDialog;
import com.runtime.pivot.plugin.view.method.RuntimeMonitoringDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RuntimePivotMethodService {
    private Project project;
    private Map<XDebugSession, RuntimeBreakpointDialog> sessionRuntimeBreakpointDialogMap = new ConcurrentHashMap<>();
    private Map<XDebugSession, RuntimeMonitoringDialog> sessionRuntimeMonitoringDialogMap = new ConcurrentHashMap<>();

    public RuntimePivotMethodService(Project project) {
        this.project = project;
    }

    public static RuntimePivotMethodService getInstance(){
        return getInstance(ProjectUtils.getCurrProject());
    }

    public static RuntimePivotMethodService getInstance(Project project){
        return ServiceManager.getService(project, RuntimePivotMethodService.class);
    }

    public Map<XDebugSession, RuntimeBreakpointDialog> getSessionRuntimeBreakpointDialogMap() {
        return sessionRuntimeBreakpointDialogMap;
    }

    public Map<XDebugSession, RuntimeMonitoringDialog> getSessionRuntimeMonitoringDialogMap() {
        return sessionRuntimeMonitoringDialogMap;
    }

    public RuntimeBreakpointDialog getRuntimeBreakpointDialog(RuntimeContext runtimeContext) {
        XDebugSession xDebugSession = runtimeContext.getxDebugSession();
        List<BacktrackingXBreakpoint> backtrackingXBreakpointList = runtimeContext.getBacktrackingXBreakpointList();
        RuntimeBreakpointDialog runtimeBreakpointDialog = sessionRuntimeBreakpointDialogMap.get(xDebugSession);
        if (runtimeBreakpointDialog == null) {
            runtimeBreakpointDialog = RuntimeBreakpointDialog.getInstance(project, xDebugSession,backtrackingXBreakpointList);
            sessionRuntimeBreakpointDialogMap.put(runtimeContext.getxDebugSession(), runtimeBreakpointDialog);
        } else {
            runtimeBreakpointDialog.updateListData(backtrackingXBreakpointList);
        }
        return runtimeBreakpointDialog;
    }

    public RuntimeMonitoringDialog getRuntimeMonitoringDialog(RuntimeContext runtimeContext) {
        XDebugSession xDebugSession = runtimeContext.getxDebugSession();
        RuntimeMonitoringDialog runtimeMonitoringDialog = sessionRuntimeMonitoringDialogMap.get(xDebugSession);

        if (runtimeMonitoringDialog == null) {
            runtimeMonitoringDialog = RuntimeMonitoringDialog.getInstance(project, xDebugSession);
            sessionRuntimeMonitoringDialogMap.put(xDebugSession, runtimeMonitoringDialog);
        } else {
            runtimeMonitoringDialog.onClearButtonClicked();
        }
        return runtimeMonitoringDialog;
    }
}
