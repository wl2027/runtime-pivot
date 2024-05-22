package com.runtime.pivot.plugin.listeners;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.util.Key;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XDebuggerManagerListener;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.runtime.pivot.agent.config.ActionType;
import com.runtime.pivot.agent.providers.ClassEnhanceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//多线程并没有currentSessionChanged
public class MethodListener implements XDebuggerManagerListener {

    public static final String a = "";
    public static void aaa(String a,String b){

    }

    public static XDebugSessionImpl xDebugSessionImpl = new XDebugSessionImpl(null,null);

    @Override //XDebugProcess 调试过程
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
        session.addSessionListener(new XDebugSessionListener() {
            @Override
            public void sessionPaused() {
                XDebugSessionListener.super.sessionPaused();
                //比较 session;
                //MY DebugProcessImpl=>Command
                //MY XDebuggerSessionImpl.clear()

                //JumpToStatementHandler.perform - done
                Map<String,Object> rs0 = cacheBreakpointsWithJum(session);
                //JumpToStatementHandler.perform - done
                Map<String,Object> rs1 = cacheBreakpointsWithPaused(session);
                //com.intellij.xdebugger.impl.actions.ResumeAction -done
                Map<String,Object> rs2 = cacheBreakpointsWithResume(session);
                //com.intellij.xdebugger.impl.actions.StepOverAction
                Map<String,Object> rs3 = cacheBreakpointsWithStepOver(session);
                //com.intellij.debugger.actions.PopFrameAction & PopFrameCommand & JvmDropFrameActionHandler 是调试启动时创建出来的
                Map<String,Object> rs4 = cacheBreakpointsWithResetFrame(session);
                //com.intellij.debugger.actions.ForceEarlyReturnAction
                Map<String,Object> rs5 =  cacheBreakpointsWithForceRetuen(session);
            }

            @Override
            public void sessionResumed() {
                XDebugSessionListener.super.sessionResumed();
            }

            @Override
            public void sessionStopped() {
                XDebugSessionListener.super.sessionStopped();
            }

            @Override
            public void stackFrameChanged() {
                XDebugSessionListener.super.stackFrameChanged();
            }

            @Override
            public void beforeSessionResume() {
                XDebugSessionListener.super.beforeSessionResume();
            }

            @Override
            public void settingsChanged() {
                XDebugSessionListener.super.settingsChanged();
            }

            @Override
            public void breakpointsMuted(boolean muted) {
                XDebugSessionListener.super.breakpointsMuted(muted);
            }
        });
        System.out.println("processStarted");
        //启动时调用

    }

    private Map<String, Object> cacheBreakpointsWithJum(XDebugSession session) {

        HashMap<String, Object> rs = new HashMap<>();
        //1.记录 position = XDebuggerUtilImpl.getCaretPosition(session.project, dataContext)
        //2.记录 currentLine = session.currentPosition
        //3.跳转 jumpService.tryJumpToLine(sourceLine, debugProcess.debuggerSession)
        //1.获取jumInfo
        //2.tryJumpToSelectedLine
        /**
         * DebuggerContextCommandImpl(debuggerSession.contextManager.context) {
         *         override fun threadAction(suspendContext: SuspendContextImpl) {
         *             finishOnException(loggedFinish) {
         *                 tryJumpToSelectedLineImpl(
         *                         session = session,
         *                         classFile = classFile,
         *                         jumpAnalyzeTarget = jumpAnalyzeTarget,
         *                         jumpAnalyzeAdditionalInfo = jumpAnalyzeAdditionalInfo,
         *                         commonTypeResolver = commonTypeResolver,
         *                         suspendContext = suspendContext,
         *                         onFinish = loggedFinish
         *                 )
         *             }
         *         }
         *     }
         *     session.process.managerThread.schedule(command)
         */

        //1. 记录 process = session.process
        //2. 记录 context = process.debuggerContext
        //3. 记录 threadProxy = context.threadProxy

        //1. 记录 currentFrame() = threadProxy.frame(0)
        //2. 记录 method = currentFrame().location().method()
        //3. 记录 machine = threadProxy.virtualMachineProxy
        //4. 记录 jbJDIStack = currentFrame().stackFrame
        //5. 记录 slotValues = jbJDIStack.getSlotsValues(localsToSafeAndRestore)
        //6. 记录 popFrameCommand = process.createPopFrameCommand(process.debuggerContext, suspendContext.frameProxy)
        //7. 执行 popFrameCommand.threadAction(suspendContext)
        //8. 错误补丁 jreSteppingBugPatch(machine.eventRequestManager(), threadProxy.threadReference)

        /**
         * private fun jreSteppingBugPatch(eventRequestManager: EventRequestManager, threadReference: ThreadReference) {
         *     val lineTablePatchStepRequest = eventRequestManager.createStepRequest(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OVER)
         *     lineTablePatchStepRequest.enable()
         *     lineTablePatchStepRequest.disable()
         * }
         */

        //9. 重定义 machine.redefineClasses(mapOf(declaredType to prefixUpdateResult.klass))
        //10.热插拔 process.onHotSwapFinished()

        //11. threadProxy.forceFramesAndGetFirst()
        //12. jbJDITargetStack/stackTargetFrame.trySetValue(it.first, it.second)

        //    process.suspendBreakpoints()
        //    process.suspendManager.resume(process.suspendManager.pausedContext)

        //我们不应该通过命令恢复它，因为命令将重新创建我们必须避免的用户断点
        // We should not resume it by command because Command will recreate user breakpoints that we have to avoid
        //    val resumeThread = process.createResumeCommand(process.suspendManager.pausedContext)
        //    resumeThread.run()

        return rs;


    }

    private Map<String, Object> cacheBreakpointsWithForceRetuen(XDebugSession session) {
        //有栈帧情况下:回到前一个栈帧
        //第一次: debugProcess.getManagerThread().schedule 区分有没有返回
        //JvmDropFrameActionHandler.evaluateFinallyBlocks
        //forceEarlyReturn
        //第二次: debugProcess.getManagerThread().schedule
        /**
         * try {
         *           debugProcess.startWatchingMethodReturn(thread);
         *           thread.forceEarlyReturn(value);
         *         }
         *         catch (Exception e) {
         *           showError(debugProcess.getProject(), JavaDebuggerBundle.message("error.early.return", e.getLocalizedMessage()));
         *           return;
         *         }
         *         SwingUtilities.invokeLater(() -> {
         *           if (dialog != null) {
         *             dialog.close(DialogWrapper.OK_EXIT_CODE);
         *           }
         *           debugProcess.getSession().stepInto(true, null);
         *         });
         */

        HashMap<String, Object> rs = new HashMap<>();
        return rs;
    }

    private Map<String, Object> cacheBreakpointsWithResetFrame(XDebugSession session) {
        //有栈帧情况下:回到前一个栈帧
        HashMap<String, Object> rs = new HashMap<>();
        return rs;
    }

    private Map<String, Object> cacheBreakpointsWithStepOver(XDebugSession session) {
        //释放+进入断点
        HashMap<String, Object> rs = new HashMap<>();
        //MY StepOverAction==> XDebuggerActionBase.actionPerformed ==> DebuggerSupport.getDebuggerSupports().for(perform())
        return rs;
    }

    private Map<String, Object> cacheBreakpointsWithResume(XDebugSession session) {
        /**
         * //调试配置弹出操作
         * if (project != null && !DumbService.isDumb(project)) {
         *   new ChooseDebugConfigurationPopupAction().actionPerformed(e);
         * }
         */

        /**
         * clearPausedData:615, XDebugSessionImpl (com.intellij.xdebugger.impl)
         * doResume:604, XDebugSessionImpl (com.intellij.xdebugger.impl)
         * resume:593, XDebugSessionImpl (com.intellij.xdebugger.impl)
         * perform:82, XDebuggerSupport$5 (com.intellij.xdebugger.impl)
         * perform:18, XDebuggerActionHandler (com.intellij.xdebugger.impl.actions.handlers)
         * perform:79, XDebuggerActionBase (com.intellij.xdebugger.impl.actions)
         * performWithHandler:71, XDebuggerActionBase (com.intellij.xdebugger.impl.actions)
         * actionPerformed:40, ResumeAction (com.intellij.xdebugger.impl.actions)
         */

        /**
         * XDebuggerSessionImpl
         * private void clearPausedData() {
         *     //挂起上下文
         *     mySuspendContext = null;
         *     //当前执行堆栈
         *     myCurrentExecutionStack = null;
         *     //当前栈帧
         *     myCurrentStackFrame = null;
         *     //堆顶栈帧
         *     myTopStackFrame = null;
         *     //清除非活动断点
         *     clearActiveNonLineBreakpoint();
         *     //更新执行位置
         *     updateExecutionPosition();
         *   }
         *
         *   private void clearActiveNonLineBreakpoint() {
         *     //myActiveNonLineBreakpoint.get()
         *     myActiveNonLineBreakpoint.set(null);
         *   }
         *
         */
        HashMap<String, Object> rs = new HashMap<>();
        return rs;
    }

    private Map<String, Object> cacheBreakpointsWithPaused(XDebugSession session) {

        //JavaDebugProcess.create
        //myJavaSession.getContextManager().addListener(new DebuggerContextListener())

        //核心: 到达断点不处理
        //((XDebugSessionImpl)getSession()).breakpointReachedNoProcessing(xBreakpoint, newSuspendContext);
        //getSession().positionReached(newSuspendContext);
        //unsetPausedIfNeeded(newContext);

        //1.记录 XSourcePosition position = breakpoint.getSourcePosition();
        //2. 执行 myDebugProcess.logStack(suspendContext, this); 日志堆栈
        //3.到达内部位置 positionReachedInternal(suspendContext, true);
        //4.记录 myCurrentExecutionStack = suspendContext.getActiveExecutionStack();


        //5. 处理临时命中断点 handleTemporaryBreakpointHit(breakpoint);
        //判断是否为临时断点  处理&&是行断点&&是临时的
        //if (doProcessing && breakpoint instanceof XLineBreakpoint<?> && ((XLineBreakpoint<?>)breakpoint).isTemporary())
        /**
         * addSessionListener(new XDebugSessionListener() {
         *       private void removeBreakpoint() {
         *         XDebuggerUtil.getInstance().removeBreakpoint(myProject, breakpoint);
         *         removeSessionListener(this);
         *       }
         *
         *       @Override
         *       public void sessionResumed() {
         *         removeBreakpoint();
         *       }
         *
         *       @Override
         *       public void sessionStopped() {
         *         removeBreakpoint();
         *       }
         *     });
         */

        //IDEA确实到了这个位置,但是JVM呢?
        //功能缩减为中断时返现?=>需要看中断时idea清空了什么

        /**
         * process.getManagerThread().schedule(new SuspendContextCommandImpl(newSuspendContext) {
         *     @Override
         *     public void contextAction(@NotNull SuspendContextImpl suspendContext) {
         *       ThreadReferenceProxyImpl threadProxy = newContext.getThreadProxy();
         *       newSuspendContext.initExecutionStacks(threadProxy);
         *
         *       Pair<Breakpoint, Event> item = ContainerUtil.getFirstItem(DebuggerUtilsEx.getEventDescriptors(newSuspendContext));
         *       if (item != null) {
         *         XBreakpoint xBreakpoint = item.getFirst().getXBreakpoint();
         *         Event second = item.getSecond();
         *         if (xBreakpoint != null && second instanceof LocatableEvent &&
         *             threadProxy != null && ((LocatableEvent)second).thread() == threadProxy.getThreadReference()) {
         *           ((XDebugSessionImpl)getSession()).breakpointReachedNoProcessing(xBreakpoint, newSuspendContext);
         *           unsetPausedIfNeeded(newContext);
         *           SourceCodeChecker.checkSource(newContext);
         *           return;
         *         }
         *       }
         *       //到达位置
         *       getSession().positionReached(newSuspendContext);
         *       //如果需要则暂停
         *       unsetPausedIfNeeded(newContext);
         *       //检查源代码
         *       SourceCodeChecker.checkSource(newContext);
         *     }
         *  });
         */




        HashMap<String, Object> rs = new HashMap<>();



        return rs;
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
