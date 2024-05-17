package com.runtime.pivot.plugin.listeners;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManagerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//多线程并没有currentSessionChanged
public class MethodListener implements XDebuggerManagerListener {
    @Override
    public void processStarted(@NotNull XDebugProcess debugProcess) {
        XDebugSession session = debugProcess.getSession();
        debugProcess.getProcessHandler().addProcessListener(new ProcessListener() {
            @Override
            public void startNotified(@NotNull ProcessEvent event) {

            }

            @Override
            public void processTerminated(@NotNull ProcessEvent event) {

            }

            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {

            }
        });
        System.out.println("processStarted");
        //启动时调用

    }

    @Override
    public void processStopped(@NotNull XDebugProcess debugProcess) {
        System.out.println("processStopped");
        //关闭时调用
    }

    @Override
    public void currentSessionChanged(@Nullable XDebugSession previousSession, @Nullable XDebugSession currentSession) {
        System.out.println("currentSessionChanged");
        //启动时调用
        //关闭时调用
    }
}
