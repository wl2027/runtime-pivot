//package com.runtime.pivot.plugin.actions;
//
//
//import com.intellij.debugger.DebuggerManagerEx;
//import com.intellij.debugger.impl.DebuggerContextListener;
//import com.intellij.debugger.impl.DebuggerManagerListener;
//import com.intellij.debugger.impl.DebuggerSession;
//import com.intellij.openapi.actionSystem.AnActionEvent;
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.xdebugger.impl.actions.ResumeAction;
//import com.runtime.pivot.plugin.utils.ProjectUtils;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class ResumeActionTest extends ResumeAction {
//    private static final ConcurrentHashMap<DebuggerSession, Long> sessionPauseTimeMap = new ConcurrentHashMap<>();
//
//    public ResumeActionTest() {
//        // Initialize the debugger manager and add a session listener
//        DebuggerManagerEx debuggerManager = DebuggerManagerEx.getInstanceEx(ProjectUtils.getCurrProject());
//        debuggerManager.addDebuggerManagerListener(new DebuggerManagerListener() {
//            @Override
//            public void sessionCreated(DebuggerSession session) {
//                session.getContextManager().addListener(new DebuggerContextListener() {
//                    @Override
//                    public void sessionPaused() {
//                        long pauseTime = System.currentTimeMillis();
//                        sessionPauseTimeMap.put(session, pauseTime);
//                    }
//
//                    @Override
//                    public void sessionResumed() {
//                        Long pauseTime = sessionPauseTimeMap.remove(session);
//                        if (pauseTime != null) {
//                            long resumeTime = System.currentTimeMillis();
//                            long elapsedTime = resumeTime - pauseTime;
//                            System.out.println("Elapsed time between breakpoints: " + elapsedTime + "ms");
//                        }
//                    }
//
//                    @Override
//                    public void sessionDetached() {
//                        sessionPauseTimeMap.remove(session);
//                    }
//
//                    @Override
//                    public void sessionAttached() {
//                        // No action needed
//                    }
//
//                    @Override
//                    public void sessionResumed(boolean b) {
//                        sessionResumed();
//                    }
//                });
//            }
//
//            @Override
//            public void sessionRemoved(DebuggerSession session) {
//                sessionPauseTimeMap.remove(session);
//            }
//        });
//    }
//
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        // Call the super method to resume the execution
//        super.actionPerformed(e);
//    }
//
//    @Override
//    public void update(@NotNull AnActionEvent e) {
//        super.update(e);
//        // Additional update logic if needed
//    }
//}
