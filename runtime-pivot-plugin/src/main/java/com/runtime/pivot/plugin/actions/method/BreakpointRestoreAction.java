package com.runtime.pivot.plugin.actions.method;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReflectUtil;
import com.intellij.debugger.DebuggerInvocationUtil;
import com.intellij.debugger.JavaDebuggerBundle;
import com.intellij.debugger.actions.DebuggerAction;
import com.intellij.debugger.actions.JvmDropFrameActionHandler;
import com.intellij.debugger.actions.PopFrameAction;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.DebugProcessListener;
import com.intellij.debugger.engine.DebuggerManagerThreadImpl;
import com.intellij.debugger.engine.JavaStackFrame;
import com.intellij.debugger.engine.SuspendContext;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.SuspendManager;
import com.intellij.debugger.engine.evaluation.EvaluateException;
import com.intellij.debugger.engine.events.DebuggerCommandImpl;
import com.intellij.debugger.engine.events.DebuggerContextCommandImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.debugger.impl.DebuggerUtilsEx;
import com.intellij.debugger.jdi.StackFrameProxyImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.intellij.debugger.ui.breakpoints.Breakpoint;
import com.intellij.debugger.ui.impl.watch.DebuggerTreeNodeImpl;
import com.intellij.debugger.ui.impl.watch.NodeDescriptorImpl;
import com.intellij.debugger.ui.impl.watch.StackFrameDescriptorImpl;
import com.intellij.idea.ActionsBundle;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XDropFrameHandler;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.actions.ResumeAction;
import com.runtime.pivot.plugin.actions.method.test.BreakpointUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.actions.method.test2.StackFrameUtils;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import com.sun.jdi.InvalidStackFrameException;
import com.sun.jdi.NativeMethodException;
import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.VMDisconnectedException;
import jdk.jshell.EvalException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.intellij.debugger.actions.PopFrameAction.ACTION_NAME;
import static com.runtime.pivot.agent.providers.MethodActionProvider.breakpointRestore;
import static com.runtime.pivot.plugin.domain.MethodWatchContext.suspendContext;

/**
 * 并不需要回滚变量值
 * com.intellij.xdebugger.impl.actions.ResetFrameAction
 * JvmDropFrameActionHandler
 */
public class BreakpointRestoreAction extends AnAction {

    /**
     * (1) 当前栈帧 当前行 :判断当前栈帧的方法内有没有断点? 有则判断断点行<当前行回退+恢复 : 没有回退一层
     * (2) 当前栈帧 当前行 :判断当前栈帧的方法内有没有断点? 有则判断断点行<当前行回退+恢复 : 没有回退一层
     * (3)...
     * 当前执行处所在方法的[start,end] 查找断点(局限于行断点,其他类型断点先不考虑)
     * <p>
     * <p>
     * Reset Frame：重新执行当前方法，局部变量和方法状态被重置，适用于需要重新运行当前方法的情况。
     * Force Return：强制从当前方法返回指定值，而不执行方法的其余部分，适用于测试调用者接收不同返回值时的行为或跳过方法剩余部分的情况。
     * 不考虑回溯的断点是执行过的,这条断点链路是新取出来的
     *
     * @param e
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        XDebugSession xDebugSession = DebuggerUIUtil.getSession(e);
        //线程数
        XExecutionStack[] executionStacks = xDebugSession.getSuspendContext().getExecutionStacks();
        XDebuggerManager debuggerManager = XDebuggerManager.getInstance(e.getProject());
        XBreakpointManager breakpointManager = debuggerManager.getBreakpointManager();
        XBreakpoint<?>[] allBreakpoints = breakpointManager.getAllBreakpoints();
        List<XBreakpoint<?>> xBreakpointList = ListUtil.of(allBreakpoints).stream().filter(bean -> bean.isEnabled()).collect(Collectors.toList());
        //Breakpoint first = ContainerUtil.getFirstItem(DebuggerUtilsEx.getEventDescriptors((SuspendContextImpl) (xDebugSession.getSuspendContext()))).getFirst();
        List<XStackFrame> xStackFrames = XDebuggerTestUtil.collectFrames(DebuggerUIUtil.getSession(e));
        String resumeActionId = "Resume"; //下一步,不是下一行
        //Reset Frame是IDEA 2022.1版本之后才有的。之前叫Drop Frame
        String resetFrameActionId = "Debugger.PopFrame"; //上一步
        ResumeAction resumeAction = (ResumeAction) ActionManager.getInstance().getAction(resumeActionId);
        PopFrameAction resetFrameAction = (PopFrameAction) ActionManager.getInstance().getAction(resetFrameActionId);

        breakpointRestore(e, resumeAction, resetFrameAction, xDebugSession, xStackFrames, executionStacks, xBreakpointList);


        //XBreakpoint<?> previousBreakpoint = BreakpointUtil.getPreviousBreakpoint(e.getProject());
        //String message = (previousBreakpoint == null) ? "No previous breakpoint found." : "Previous Breakpoint: " + previousBreakpoint;
        //Messages.showMessageDialog(project, message, "Previous Breakpoint", Messages.getInformationIcon());

        //xDebugSession.positionReached();//当到达位置时调用此方法（例如，在“运行到光标”或“单步执行”命令之后）

    }

    private XBreakpoint<?> breakpointRestore(AnActionEvent e,
                                             ResumeAction resumeAction,
                                             PopFrameAction resetFrameAction,
                                             XDebugSession xDebugSession,
                                             List<XStackFrame> xStackFrames,
                                             XExecutionStack[] executionStacks,
                                             List<XBreakpoint<?>> xBreakpointList) {
        //TODO 改成命令队列,防止resetFrame错误
        if (xStackFrames.size() <= 1) {
            return null;
        }
        //xStackFrames 每个栈帧执行到的位置,不是断点位置
        for (XStackFrame xStackFrame : xStackFrames) {
            //methodRange [方法前一行,方法语句最后一行]
            StackFrameUtils.Range<Integer> methodRange = StackFrameUtils.getMethodRange(e.getProject(), xStackFrame);
            System.out.println(xStackFrame + ":" + methodRange);
            XBreakpoint<?> regressionXBreakpoint = getRegressionXBreakpoint(xStackFrame, methodRange, xBreakpointList);
            //这里查找是异步的,只能加同步机制
            if (regressionXBreakpoint != null) {
//                resetFrameAction.actionPerformed(e);
                XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
                JvmDropFrameActionHandler jvmDropFrameActionHandler = (JvmDropFrameActionHandler) dropFrameHandler;
                DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(jvmDropFrameActionHandler, "myDebugSession");
                DebugProcessImpl myDebugProcess = myDebugSession.getProcess();
                try {
                    DebuggerContextCommandImpl popFrameCommand = (DebuggerContextCommandImpl) myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(), getStackFrame(e).getStackFrameProxy());
                    popFrameCommand.threadAction(myDebugProcess.getDebuggerContext().getSuspendContext());
                    //popFrameCommand.run();
                    //myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                //invoke 一直自旋
                //schedule 一直自旋
                //pushBack 一直自旋
                //processEvent 一直自旋
                //invokeAndWait 一直自旋
                //invokeCommand 一直自旋
//                myDebugProcess.getManagerThread().invoke(new DebuggerCommandImpl(){
//                    @Override
//                    protected void action() throws Exception {
//                        while (isResetFrame(xStackFrame,xDebugSession)){
//                            ThreadUtil.sleep(10);
//                        }
//                        resumeAction.actionPerformed(e);
//                    }
//                });
                //========================20240603 第一次用自旋锁成功=================================
                ApplicationManager.getApplication().executeOnPooledThread(()->{
                    while (isResetFrame(xStackFrame,xDebugSession)){
                        ThreadUtil.sleep(10);
                    }
                    try {
                        myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    //resumeAction.actionPerformed(e);
                });


                //resumeAction.actionPerformed(e);

                //resetFrameTest(e,resumeAction);
                //==========================================
                testCommon();
                //resumeTest(e);
                test();
                return regressionXBreakpoint;
            } else {
                resetFrameAction.actionPerformed(e);
                //自旋判断当前线程是不是目标线程
            }
        }
        return null;
    }

    private void testCommon() {
//        XDropFrameHandler dropFrameHandler = xDebugSession.getDebugProcess().getDropFrameHandler();
//        JvmDropFrameActionHandler jvmDropFrameActionHandler = (JvmDropFrameActionHandler) dropFrameHandler;
//        DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(jvmDropFrameActionHandler, "myDebugSession");
//        DebugProcessImpl myDebugProcess = myDebugSession.getProcess();
//        try {
//            try {
//                myDebugProcess.getManagerThread().invoke(new DebuggerCommandImpl() {
//                    @Override
//                    protected void action() throws Exception {
//                        // 添加PopFrame完成监听器
//                        myDebugProcess.addDebugProcessListener(new DebugProcessListener() {
//
//                            @Override
//                            public void paused(@NotNull SuspendContext suspendContext) {
//                                System.out.println("paused");
//                                // 确保监听器只执行一次
//                                myDebugProcess.removeDebugProcessListener(this);
//
//                                // 在PopFrame完成后执行resume命令
//                                myDebugProcess.getManagerThread().invoke(new DebuggerCommandImpl() {
//                                    @Override
//                                    protected void action() throws Exception {
//                                        // 执行resume命令
//                                        myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();
//                                    }
//                                });
//                            }
//
//                        });
//                        // 执行popFrame命令
//                        myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),getStackFrame(e).getStackFrameProxy()).run();
//                    }
//                });
//            } catch (Exception exception) {
//                exception.printStackTrace();
//            }
//
//            //myDebugProcess.getManagerThread().invoke(myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),getStackFrame(e).getStackFrameProxy()));
//            //myDebugProcess.getManagerThread().invoke(myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()));
//
//            //myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),getStackFrame(e).getStackFrameProxy()).run();
//            //myDebugProcess.onHotSwapFinished();
//            //myDebugProcess.createResumeCommand((SuspendContextImpl) xDebugSession.getSuspendContext()).run();
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
    }

    private void resumeTest(AnActionEvent e) {
    }

    private void resetFrameTest(AnActionEvent e,ResumeAction resumeAction) {
        final JavaStackFrame stackFrame = getStackFrame(e);
        if (stackFrame == null || stackFrame.getStackFrameProxy().isBottom()) {
            return;
        }
        final XDropFrameHandler handler = getDropFrameHandler(e);
        final JavaStackFrame frame = getSelectedStackFrame(e);
        //====================================
        //if (frame != null && handler != null) {
        //    handler.drop(frame);
        //}

        //=================================
        XDropFrameHandler dropFrameHandler = handler;
        JvmDropFrameActionHandler jvmDropFrameActionHandler = (JvmDropFrameActionHandler) dropFrameHandler;
        DebuggerSession myDebugSession = (DebuggerSession) ReflectUtil.getFieldValue(jvmDropFrameActionHandler, "myDebugSession");

        if (frame instanceof JavaStackFrame) {
            var stackFrame0 = (JavaStackFrame)frame;
            var project = myDebugSession.getProject();
            DebugProcessImpl debugProcess = myDebugSession.getProcess();
            var debuggerContext = myDebugSession.getContextManager().getContext();
            try {
                myDebugSession.setSteppingThrough(stackFrame0.getStackFrameProxy().threadProxy());
                if (ReflectUtil.invoke(jvmDropFrameActionHandler,"evaluateFinallyBlocks",project,
                        UIUtil.removeMnemonic(ActionsBundle.actionText(ACTION_NAME)),
                        stackFrame0,
                        new XDebuggerEvaluator.XEvaluationCallback() {
                            @Override
                            public void evaluated(@NotNull XValue result) {
                                //ReflectUtil.invoke(jvmDropFrameActionHandler,"popFrame",debugProcess, debuggerContext, stackFrame0);
                                //resumeAction.actionPerformed(e);
                                //debugProcess.getManagerThread().schedule(debugProcess.createPopFrameCommand(debuggerContext, stackFrame.getStackFrameProxy()));
                                debugProcess.getManagerThread().schedule(new TestFrameCommand(debugProcess,e,resumeAction,debugProcess.getSuspendManager(),debuggerContext,stackFrame.getStackFrameProxy()));
                            }

                            @Override
                            public void errorOccurred(@NotNull final String errorMessage) {
//                                showError(project, JavaDebuggerBundle.message("error.executing.finally", errorMessage),
//                                        UIUtil.removeMnemonic(ActionsBundle.actionText(ACTION_NAME)));
                            }
                        })) return;
                //ReflectUtil.invoke(jvmDropFrameActionHandler,"popFrame",debugProcess, debuggerContext, stackFrame0);
                //debugProcess.getManagerThread().schedule(debugProcess.createPopFrameCommand(debuggerContext, stackFrame.getStackFrameProxy()));
                debugProcess.getManagerThread().schedule(new TestFrameCommand(debugProcess,e,resumeAction,debugProcess.getSuspendManager(),debuggerContext,stackFrame.getStackFrameProxy()));
            }
            catch (NativeMethodException e2) {
                Messages.showMessageDialog(project, JavaDebuggerBundle.message("error.native.method.exception"),
                        UIUtil.removeMnemonic(ActionsBundle.actionText(ACTION_NAME)), Messages.getErrorIcon());
            }
            catch (InvalidStackFrameException | VMDisconnectedException ignored) {
            }
        }



    }


    private class TestFrameCommand extends DebuggerContextCommandImpl {
        private final StackFrameProxyImpl myStackFrame;
        private final SuspendManager mySuspendManager;
        private final AnActionEvent myAnActionEvent;
        private final ResumeAction myResumeAction;
        private final DebugProcessImpl myDebugProcess;

        TestFrameCommand(DebugProcessImpl debugProcess,AnActionEvent e,ResumeAction resumeAction,SuspendManager suspendManager, DebuggerContextImpl context, StackFrameProxyImpl frameProxy) {
            super(context, frameProxy.threadProxy());
            myStackFrame = frameProxy;
            mySuspendManager = suspendManager;
            myAnActionEvent = e;
            myResumeAction = resumeAction;
            myDebugProcess = debugProcess;
        }

        @Override
        public void threadAction(@NotNull SuspendContextImpl suspendContext) {
            final ThreadReferenceProxyImpl thread = myStackFrame.threadProxy();
            try {
                //thread.popFrames(myStackFrame);
                //mySuspendManager.popFrame(suspendContext);
                //ActionManager.getInstance().getAction("Debugger.ResumeThread").actionPerformed(myAnActionEvent);
                myDebugProcess.createPopFrameCommand(myDebugProcess.getDebuggerContext(),myStackFrame).run();
                myDebugProcess.createResumeCommand(suspendContext).run();
//                myDebugProcess.getManagerThread().schedule(new DebuggerContextCommandImpl(suspendContext.getDebugProcess().getDebuggerContext()) {
//
//                    @Override
//                    public void threadAction(@NotNull SuspendContextImpl suspendContext) {
//                        try {
//                            myDebugProcess.createResumeCommand(suspendContext).run();
//                        } catch (Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                });
            }
            catch (final Exception e) {
               e.printStackTrace();
            }
        }
    }

    static JavaStackFrame getStackFrame(AnActionEvent e) {
        StackFrameDescriptorImpl descriptor = getSelectedStackFrameDescriptor(e);
        if (descriptor != null) {
            return new JavaStackFrame(descriptor, false);
        }
        return getSelectedStackFrame(e);
    }
    @Nullable
    private static XDropFrameHandler getDropFrameHandler(@NotNull AnActionEvent e) {
        var xSession = DebuggerUIUtil.getSession(e);
        return Optional.ofNullable(xSession)
                .map(XDebugSession::getDebugProcess)
                .map(XDebugProcess::getDropFrameHandler)
                .orElse(null);
    }

    @Nullable
    private static JavaStackFrame getSelectedStackFrame(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            XDebugSession session = DebuggerUIUtil.getSession(e);
            if (session != null) {
                XStackFrame frame = session.getCurrentStackFrame();
                if (frame instanceof JavaStackFrame) {
                    return ((JavaStackFrame)frame);
                }
            }
        }
        return null;
    }
    @Nullable
    private static StackFrameDescriptorImpl getSelectedStackFrameDescriptor(AnActionEvent e) {
        DebuggerTreeNodeImpl selectedNode = DebuggerAction.getSelectedNode(e.getDataContext());
        if(selectedNode != null) {
            NodeDescriptorImpl descriptor = selectedNode.getDescriptor();
            if(descriptor instanceof StackFrameDescriptorImpl) {
                return (StackFrameDescriptorImpl)descriptor;
            }
        }
        return null;
    }




    private void test() {
//        ActionUpdateThread actionUpdateThread = resetFrameAction.getActionUpdateThread();
//                while (!resetFrameAction.getActionUpdateThread().equals(ActionUpdateThread.EDT)){
//                    ThreadUtil.sleep(10);
//                    System.out.println(resetFrameAction.getActionUpdateThread().equals(ActionUpdateThread.EDT));
//                }
//                System.out.println(resetFrameAction.getActionUpdateThread());
        //resumeAction.actionPerformed(e);
//                while (isResetFrame2(xStackFrame,xDebugSession)){
//                    ThreadUtil.sleep(10);
//                }
//                xDebugSession.resume();
//                DebugProcessImpl process = myDebugSession.getProcess();
//                process.getSuspendManager().resume(process.getSuspendManager().getPausedContext());
        //自旋判断当前线程是不是目标线程
        //invokeAndWait 不停,一直是true
        //invokeLater 不停,一直是true
        //invokeLaterOnWriteThread 不停,一直是true
//                ApplicationManager.getApplication().invokeLaterOnWriteThread(
//                        ()->{
//                            while (isResetFrame(xStackFrame,xDebugSession)){
//                                ThreadUtil.sleep(10);
//                            }
//                            resumeAction.actionPerformed(e);
//                        },
//                        ModalityState.NON_MODAL
//                );
        //自己new 线程,不是调度线程
//                new Thread(()->{
//                    while (isResetFrame(xStackFrame,xDebugSession)){
//                        ThreadUtil.sleep(10);
//                    }
        //避免命令
//                    resumeAction.actionPerformed(e);
//                }).start();
    }

    private boolean isResetFrame(XStackFrame xStackFrame, XDebugSession xDebugSession) {
        boolean b = xDebugSession.getCurrentStackFrame().equals(xStackFrame);
        System.out.println(b);
        return b;
    }
    private boolean isResetFrame2(XStackFrame xStackFrame, XDebugSession xDebugSession) {
        boolean b = XDebuggerTestUtil.collectFrames(xDebugSession).get(0).equals(xStackFrame);
        System.out.println(b);
        return b;
    }

    private XBreakpoint<?> getRegressionXBreakpoint(XStackFrame xStackFrame, StackFrameUtils.Range<Integer> methodRange, List<XBreakpoint<?>> xBreakpointList) {
        XBreakpoint<?> regressionXBreakpoint = null;
        List<XBreakpoint<?>> regressionXBreakpointList = new ArrayList<>();
        for (XBreakpoint<?> xBreakpoint : xBreakpointList) {
            XSourcePosition sourcePosition = xBreakpoint.getSourcePosition();
            if (sourcePosition.getFile().equals(methodRange.getVirtualFile()) && sourcePosition.getLine() >= methodRange.getStart() && sourcePosition.getLine() <= xStackFrame.getSourcePosition().getLine()) {
                //有回退断点~代码块含多个
                regressionXBreakpointList.add(xBreakpoint);
            }
        }
        if (!regressionXBreakpointList.isEmpty()) {
            regressionXBreakpoint = regressionXBreakpointList.get(0);
            for (XBreakpoint<?> xBreakpoint : regressionXBreakpointList) {
                if (regressionXBreakpoint.getSourcePosition().getLine() < xBreakpoint.getSourcePosition().getLine()) {
                    regressionXBreakpoint = xBreakpoint;
                }
            }
        }
        return regressionXBreakpoint;
    }

    private static boolean isBefore(XSourcePosition pos1, XSourcePosition pos2) {
        if (pos1.getFile().equals(pos2.getFile())) {
            return pos1.getLine() < pos2.getLine();
        }
        return false;
    }
}
