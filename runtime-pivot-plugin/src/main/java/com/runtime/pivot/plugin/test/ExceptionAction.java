//package com.runtime.pivot.plugin.actions;
//
//import com.intellij.debugger.DebuggerManagerEx;
//import com.intellij.debugger.InstanceFilter;
//import com.intellij.debugger.engine.DebugProcessImpl;
//import com.intellij.debugger.engine.SuspendContextImpl;
//import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
//import com.intellij.debugger.engine.requests.RequestManagerImpl;
//import com.intellij.debugger.impl.DebuggerSession;
//import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
//import com.intellij.debugger.ui.breakpoints.FilteredRequestor;
//import com.intellij.openapi.actionSystem.AnAction;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.project.ProjectManagerListener;
//import com.intellij.openapi.diagnostic.Logger;
//import com.intellij.ui.classFilter.ClassFilter;
//import com.sun.jdi.*;
//import com.sun.jdi.event.*;
//import com.sun.jdi.request.*;
//import org.jetbrains.annotations.NotNull;
//
//public class ExceptionAction extends AnAction {
//
//    private static final Logger LOG = Logger.getInstance(ExceptionAction.class);
//
//    private FilteredRequestor filteredRequestor = new FilteredRequestor() {
//        @Override
//        public String getSuspendPolicy() {
//            return null;
//        }
//
//        @Override
//        public boolean isInstanceFiltersEnabled() {
//            return false;
//        }
//
//        @Override
//        public InstanceFilter[] getInstanceFilters() {
//            return new InstanceFilter[0];
//        }
//
//        @Override
//        public boolean isCountFilterEnabled() {
//            return false;
//        }
//
//        @Override
//        public int getCountFilter() {
//            return 0;
//        }
//
//        @Override
//        public boolean isClassFiltersEnabled() {
//            return false;
//        }
//
//        @Override
//        public ClassFilter[] getClassFilters() {
//            return new ClassFilter[0];
//        }
//
//        @Override
//        public ClassFilter[] getClassExclusionFilters() {
//            return new ClassFilter[0];
//        }
//
//        @Override
//        public boolean processLocatableEvent(SuspendContextCommandImpl suspendContextCommand, LocatableEvent locatableEvent) {
//            // 只在特定事件中设置断点，这里我们在异常事件中设置断点
//            return locatableEvent instanceof ExceptionEvent;
//        }
//    };
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        DebuggerSession session = DebuggerManagerEx.getInstanceEx(e.getProject()).getContext().getDebuggerSession();
//        if (session != null) {
//            DebugProcessImpl process = session.getProcess();
//            VirtualMachine vm = process.getVirtualMachineProxy().getVirtualMachine();
//            setExceptionRequest(vm, e.getProject());
//        }
//    }
//    private void setExceptionRequest(VirtualMachine vm, Project project) {
//        ExceptionRequest exceptionRequest = vm.eventRequestManager().createExceptionRequest(null, true, true);
//        exceptionRequest.setSuspendPolicy(EventRequest.SUSPEND_ALL);
//        exceptionRequest.enable();
//
//        new Thread(() -> {
//            try {
//                EventQueue eventQueue = vm.eventQueue();
//                while (true) {
//                    EventSet eventSet = eventQueue.remove();
//                    for (Event event : eventSet) {
//                        if (event instanceof ExceptionEvent) {
//                            handleExceptionEvent((ExceptionEvent) event, project);
//                        }
//                    }
//                    eventSet.resume();
//                }
//            } catch (InterruptedException e) {
//                LOG.error("Exception handling thread interrupted", e);
//            }
//        }).start();
//    }
//
//    private void handleExceptionEvent(ExceptionEvent event, Project project) {
//        ObjectReference exception = event.exception();
//        Location catchLocation = event.catchLocation();
//        if (catchLocation == null) {
//            DebuggerSession session = DebuggerManagerEx.getInstanceEx(project).getContext().getDebuggerSession();
//            if (session != null) {
//                DebugProcessImpl process = session.getProcess();
//                process.getManagerThread().invoke(new SuspendContextCommandImpl(process.getSuspendManager().getPausedContext()) {
//                    @Override
//                    public void contextAction(SuspendContextImpl suspendContext) {
//                        try {
//                            // Create and enable a breakpoint request at the exception location
//                            //在异常点创建并启用断点 FilteredRequestor 筛选请求程序
//                            RequestManagerImpl requestManager = process.getRequestsManager();
//                            BreakpointRequest breakpointRequest = requestManager.createBreakpointRequest(FilteredRequestor.DEFAULT, event.location());
//                            breakpointRequest.enable();
//
//                            // Suspend the thread where the exception occurred
//                            // 挂起发生异常的线程
//                            process.getSuspendManager().suspendThread(suspendContext, process.createResumeThreadCommand(new ThreadReferenceProxyImpl(suspendContext.getThread().getVirtualMachine(),event.thread())));
//
//                            LOG.info("Unhandled exception occurred: " + exception.toString());
//                        } catch (Exception e) {
//                            LOG.error("Error handling exception event", e);
//                        }
//                    }
//                });
//            }
//        } else {
//            LOG.info("Handled exception occurred: " + exception.toString());
//        }
//    }
//}
