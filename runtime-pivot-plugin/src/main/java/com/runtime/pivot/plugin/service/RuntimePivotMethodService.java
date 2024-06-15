package com.runtime.pivot.plugin.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.BreakpointListDialog;
import com.runtime.pivot.plugin.view.method.MonitoringTableDialog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimePivotMethodService {
    private Project project;
    private Map<XDebugSession, BreakpointListDialog> sessionBreakpointListMap = new ConcurrentHashMap<>();
    private Map<XDebugSession, MonitoringTableDialog> sessionMonitoringTableMap = new ConcurrentHashMap<>();

    public RuntimePivotMethodService(Project project) {
        this.project = project;
    }

    public static RuntimePivotMethodService getInstance(){
        return getInstance(ProjectUtils.getCurrProject());
    }

    public static RuntimePivotMethodService getInstance(Project project){
        return ServiceManager.getService(project, RuntimePivotMethodService.class);
    }

    public Map<XDebugSession, BreakpointListDialog> getSessionBreakpointListMap() {
        return sessionBreakpointListMap;
    }

    public Map<XDebugSession, MonitoringTableDialog> getSessionMonitoringTableMap() {
        return sessionMonitoringTableMap;
    }

    public void clear(){
        this.sessionBreakpointListMap.clear();
        this.sessionMonitoringTableMap.clear();
    }
}
