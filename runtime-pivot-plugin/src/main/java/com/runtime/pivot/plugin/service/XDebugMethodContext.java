package com.runtime.pivot.plugin.service;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.runtime.pivot.plugin.view.method.BreakpointListDialog;
import com.runtime.pivot.plugin.view.method.MonitoringTableDialog;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class XDebugMethodContext {
    private final Project project;

//    private final Map<XDebugSession, XDebugMethodWatchListener> sessionListenerMap = new ConcurrentHashMap<>();
    private final Map<XDebugSession, BreakpointListDialog> sessionBreakpointListMap = new ConcurrentHashMap<>();
    private final Map<XDebugSession, MonitoringTableDialog> sessionMonitoringTableMap = new ConcurrentHashMap<>();

    public XDebugMethodContext(Project project) {
        this.project = project;
    }

    public static XDebugMethodContext getInstance(){
        return getInstance(ProjectUtils.getCurrProject());
    }

    public static XDebugMethodContext getInstance(Project project){
        return ServiceManager.getService(project, XDebugMethodContext.class);
    }

//    public Map<XDebugSession, XDebugMethodWatchListener> getSessionListenerMap() {
//        return sessionListenerMap;
//    }


    public Map<XDebugSession, BreakpointListDialog> getSessionBreakpointListMap() {
        return sessionBreakpointListMap;
    }

    public Map<XDebugSession, MonitoringTableDialog> getSessionMonitoringTableMap() {
        return sessionMonitoringTableMap;
    }
}
