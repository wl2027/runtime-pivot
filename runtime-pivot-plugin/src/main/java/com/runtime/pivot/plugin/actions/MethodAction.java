package com.runtime.pivot.plugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;

public abstract class MethodAction extends RuntimeBaseAction {

    /**
     * isPaused：表示调试会话暂停，等待用户操作 (断点命中)。
     * isSuspended：表示调试会话被挂起，通常是调试器在执行某个操作时临时停止线程执行 (表达式求值)。
     * @param e
     * @return
     */
    @Override
    final protected boolean isEnable(AnActionEvent e) {
        return getRuntimeContext().getXDebugSession().isPaused();
    }
}
