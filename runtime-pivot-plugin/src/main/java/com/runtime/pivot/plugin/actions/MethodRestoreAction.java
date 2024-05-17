package com.runtime.pivot.plugin.actions;

import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.runtime.pivot.plugin.domain.MethodWatchContext;
import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.events.DebuggerContextCommandImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.breakpoints.XBreakpointBase;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import com.sun.jdi.event.Event;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

/**
 * 并不需要回滚变量值
 * com.intellij.xdebugger.impl.actions.ResetFrameAction
 * JvmDropFrameActionHandler
 */
public class MethodRestoreAction extends AnAction {
//    //只是打上断点,当前断点没有回去
//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        XDebugSession session = DebuggerUIUtil.getSession(e);
//        XDebuggerManagerImpl instance = (XDebuggerManagerImpl) XDebuggerManager.getInstance(e.getProject());
//        VirtualFile file = session.getCurrentPosition().getFile();
//        int line = session.getCurrentPosition().getLine();
//        XDebuggerTestUtil.toggleBreakpoint(e.getProject(),file,line+3);
//    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DebuggerSession debuggerSessionX = ((JavaDebugProcess) XDebuggerManager.getInstance(e.getProject()).getCurrentSession().getDebugProcess()).getDebuggerSession();

//        XDebuggerManagerImpl instance = (XDebuggerManagerImpl) XDebuggerManager.getInstance(e.getProject());
//        XDebugSession xDebugSession = MethodWatchContext.xDebugSession;
//        xDebugSession.setCurrentStackFrame(MethodWatchContext.xExecutionStack,MethodWatchContext.xStackFrame);
//        xDebugSession = DebuggerUIUtil.getSession(e);
//        xDebugSession.breakpointReached()
//        xDebugSession.getDebugProcess().getBreakpointHandlers()[1].
        //DebuggerContextImpl
//        DebugProcessEvents debugProcess = new DebugProcessEvents(e.getProject());
//        DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(e.getProject()).getSession(debugProcess);
        SuspendContextImpl suspendContext = MethodWatchContext.suspendContext;
        Pair<Breakpoint, Event> firstItem = ContainerUtil.getFirstItem(DebuggerUtilsEx.getEventDescriptors(suspendContext));
        XBreakpoint xBreakpoint = firstItem.getFirst().getXBreakpoint();
        DebuggerContextCommandImpl debuggerContextCommand = new DebuggerContextCommandImpl(debuggerSessionX.getContextManager().getContext()) {
            @Override
            public void threadAction(@NotNull SuspendContextImpl suspendContext) {
                //com.intellij.debugger.engine.DebugProcessImpl.PopFrameCommand.threadAction 弹出栈逻辑
                System.out.println("");
                XDebugSessionImpl session = (XDebugSessionImpl) DebuggerUIUtil.getSession(e);
                session.breakpointReached(xBreakpoint,suspendContext);
            }
        };
        //java.lang.NullPointerException
        //	at com.runtime.pivot.plugin.actions.MethodStartWatchAction$1.sessionResumed(MethodStartWatchAction.java:63)


        //Should be invoked in manager thread, use DebuggerManagerThreadImpl.getInstance(..).invoke...

        debuggerSessionX.getProcess().getManagerThread().schedule(debuggerContextCommand);
//        val command = object : DebuggerContextCommandImpl(session.contextManager.context) {
//            override fun threadAction(suspendContext:SuspendContextImpl) {
//                val loggedFinish = loggedOnFinish(
//                        unLoggedFinish = onFinish,
//                        event = GoToLine
//                )
//
//                finishOnException(loggedFinish) {
//                    jumpByRunToLineImpl(
//                            session = session,
//                            suspendContext = suspendContext,
//                            line = line,
//                            onFinish = loggedFinish
//                    )
//                }
//            }
//        }
        //session.process.managerThread.schedule(command)





    }

//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        XDebuggerManagerImpl instance = (XDebuggerManagerImpl) XDebuggerManager.getInstance(e.getProject());
//        XDebugSession xDebugSession = MethodWatchContext.xDebugSession;
//        xDebugSession.setCurrentStackFrame(MethodWatchContext.xExecutionStack,MethodWatchContext.xStackFrame);
//        XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
//        //dropFrameHandler.drop(xDebugSession.getCurrentStackFrame());
//        //JavaDebugProcess javaDebugProcess = JavaDebugProcess.create();
//        //主要逻辑
////        DebugProcessEvents debugProcess = new DebugProcessEvents(e.getProject());
////        DebuggerSession debuggerSession = DebuggerManagerEx.getInstanceEx(e.getProject()).getSession(debugProcess);
////        try {
////            DebuggerSession debuggerSession1 = DebuggerManagerEx.getInstanceEx(e.getProject()).attachVirtualMachine(debuggerSession.getDebugEnvironment());
////        } catch (ExecutionException ex) {
////            throw new RuntimeException(ex);
////        }
//        //XDebugProcess debugProcess = MethodWatchContext.debugProcess;
//        ExecutionEnvironment executionEnvironment = MethodWatchContext.executionEnvironment;
//        try {
////            instance.startSessionAndShowTab("aaa",MethodWatchContext.runContentDescriptor,new XDebugProcessStarter() {
////                @Override
////                public @NotNull XDebugProcess start(@NotNull XDebugSession session) throws ExecutionException {
////                    return null;
////                }
////            });
//            instance.startSession(executionEnvironment,new XDebugProcessStarter() {
//                @Override
//                @NotNull
//                public XDebugProcess start(@NotNull XDebugSession session) {
//                    return xDebugSession.getDebugProcess();
////                    return session.getDebugProcess();
//                    //return JavaDebugProcess.create(session, debuggerSession);
//                }
//            });
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//
//
//    }









//    @Override
//    public void actionPerformed(@NotNull AnActionEvent e) {
//        XDebuggerManagerImpl instance = (XDebuggerManagerImpl) XDebuggerManager.getInstance(e.getProject());
//        ReflectUtil.invoke(instance,"setCurrentSession", MethodWatchContext.xDebugSession);
//    }

    protected void toggleBreakpointInEgg(final String file, final String innerPath, final int line) {
        UIUtil.invokeAndWaitIfNeeded((Runnable) () -> {
            VirtualFile f = LocalFileSystem.getInstance().findFileByPath(file);
            Assert.assertNotNull(f);
            final VirtualFile jarRoot = JarFileSystem.getInstance().getJarRootForLocalFile(f);
            Assert.assertNotNull(jarRoot);
            VirtualFile innerFile = jarRoot.findFileByRelativePath(innerPath);
            Assert.assertNotNull(innerFile);
            XDebuggerTestUtil.toggleBreakpoint(ProjectUtils.getCurrProject(), innerFile, line);
        });
    }

//    private void doToggleBreakpoint(String file, int line) {
//        Assert.assertTrue(canPutBreakpointAt(getProject(), file, line));
//        XDebuggerTestUtil.toggleBreakpoint(getProject(),getFileByPath(file), line);
//    }
//
//    protected void toggleBreakpoint(final String file, final int line) {
//        ApplicationManager.getApplication().invokeAndWait(() -> doToggleBreakpoint(file, line), ModalityState.defaultModalityState());
//        setBreakpointSuspendPolicy(getProject(), line, myDefaultSuspendPolicy);
//    }
//
//    protected String getFilePath(@NotNull final String path) {
//        final VirtualFile virtualFile = myFixture.getTempDirFixture().getFile(path);
//        assert virtualFile != null && virtualFile.exists() : String.format("No file '%s' in %s", path, myFixture.getTempDirPath());
//        return virtualFile.getPath();
//    }

}
