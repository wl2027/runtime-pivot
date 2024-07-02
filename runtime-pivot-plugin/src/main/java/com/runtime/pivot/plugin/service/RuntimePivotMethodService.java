package com.runtime.pivot.plugin.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.XSessionBreakpointDialog;
import com.runtime.pivot.plugin.view.method.XSessionMonitoringDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimePivotMethodService implements Disposable {
    private Project project;
    private Map<XDebugSession, XSessionBreakpointDialog> sessionBreakpointDialogMap = new ConcurrentHashMap<>();
    private Map<XDebugSession, XSessionMonitoringDialog> sessionMonitoringDialogMap = new ConcurrentHashMap<>();

    public RuntimePivotMethodService(Project project) {
        this.project = project;
    }

    public static RuntimePivotMethodService getInstance(){
        return getInstance(ProjectUtils.getCurrProject());
    }

    public static RuntimePivotMethodService getInstance(Project project){
        return ServiceManager.getService(project, RuntimePivotMethodService.class);
    }

    public Map<XDebugSession, XSessionBreakpointDialog> getSessionBreakpointDialogMap() {
        return sessionBreakpointDialogMap;
    }

    public Map<XDebugSession, XSessionMonitoringDialog> getSessionMonitoringDialogMap() {
        return sessionMonitoringDialogMap;
    }

    public @Nullable XSessionBreakpointDialog getXSessionBreakpointDialog(@NotNull XDebugSession xDebugSession) {
        return sessionBreakpointDialogMap.get(xDebugSession);
    }
    public @Nullable XSessionMonitoringDialog getXSessionMonitoringDialog(@NotNull XDebugSession xDebugSession) {
        return sessionMonitoringDialogMap.get(xDebugSession);
    }

    public @Nullable XSessionBreakpointDialog removeXSessionBreakpointDialog(@NotNull XDebugSession xDebugSession) {
        return sessionBreakpointDialogMap.remove(xDebugSession);
    }
    public @Nullable XSessionMonitoringDialog removeXSessionMonitoringDialog(@NotNull XDebugSession xDebugSession) {
        return sessionMonitoringDialogMap.remove(xDebugSession);
    }

    public @NotNull XSessionBreakpointDialog buildXSessionBreakpointDialog(@NotNull XDebugSession xDebugSession) {
        XSessionBreakpointDialog xSessionBreakpointDialog = getXSessionBreakpointDialog(xDebugSession);
        if (xSessionBreakpointDialog == null) {
            xSessionBreakpointDialog = XSessionBreakpointDialog.getInstance(xDebugSession);
            sessionBreakpointDialogMap.put(xDebugSession, xSessionBreakpointDialog);
        } else {
            xSessionBreakpointDialog.updateData(XStackContext.getInstance(xDebugSession));
        }
        return xSessionBreakpointDialog;
    }

    public @NotNull XSessionMonitoringDialog buildXSessionMonitoringDialog(@NotNull XDebugSession xDebugSession) {
        XSessionMonitoringDialog xSessionMonitoringDialog = getXSessionMonitoringDialog(xDebugSession);

        if (xSessionMonitoringDialog == null) {
            xSessionMonitoringDialog = XSessionMonitoringDialog.getInstance(xDebugSession);
            sessionMonitoringDialogMap.put(xDebugSession, xSessionMonitoringDialog);
        } else {
            //重新打开相当于清除数据
            xSessionMonitoringDialog.initData(null);
        }
        return xSessionMonitoringDialog;
    }

    @Override
    public void dispose() {
        this.sessionBreakpointDialogMap.clear();
        this.sessionMonitoringDialogMap.clear();
    }

    public void closeXSessionComponent(XDebugSession session) {
        sessionBreakpointDialogMap.forEach((k,v)->{
            if (k==session) {
                v.closeComponent();
            }
        });
        sessionMonitoringDialogMap.forEach((k,v)->{
            if (k==session) {
                v.closeComponent();
            }
        });
    }
}
