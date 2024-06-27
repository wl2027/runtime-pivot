package com.runtime.pivot.plugin.model;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.MessageUtil;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.actions.XDebuggerActionBase;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.XDebuggerTree;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RuntimeBaseAction extends AnAction {

    private static final Logger log = Logger.getInstance(RuntimeBaseAction.class);
    private @Nullable RuntimeContext myRuntimeContext;

    @Override
    final public void actionPerformed(@NotNull AnActionEvent e) {
        //构造通用上下文
        XDebugSession session = DebuggerUIUtil.getSession(e);
        myRuntimeContext = RuntimeContext.getInstance(session);
        //通用校验
        if (session == null || myRuntimeContext == null) {
            MessageUtil.showOkCancelDialog("runtime-pivot",
                    "action context is null",
                    null,
                    null,
                    null,
                    null,
                    e.getProject());
            log.info("action context is null");
            return;
        }
        action(e);
    }

    /**
     * @see XDebuggerActionBase#update(AnActionEvent)
     * @param e
     */
    @Override
    final public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        //可执行栈帧条件下才可见可用
        XDebugSession session = DebuggerUIUtil.getSession(e);
        if (session == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        XStackFrame currentStackFrame = session.getCurrentStackFrame();
        if (currentStackFrame == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        XDebuggerEvaluator evaluator = currentStackFrame.getEvaluator();
        if (evaluator == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        presentation.setEnabled(isEnable(e));
        presentation.setVisible(isVisible(e));
    }

    /**
     * 是否可用
     * @param e
     * @return
     */
    protected abstract boolean isEnable(AnActionEvent e);

    /**
     * 是否可见
     * @param e
     * @return
     */
    protected boolean isVisible(AnActionEvent e){
        //默认都可见
        return true;
    }

    /**
     * 执行事件
     * @param e
     */
    protected abstract void action(AnActionEvent e);


    protected RuntimeContext getRuntimeContext() {
        return myRuntimeContext;
    }

}
