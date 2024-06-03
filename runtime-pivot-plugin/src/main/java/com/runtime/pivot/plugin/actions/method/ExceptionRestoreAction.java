package com.runtime.pivot.plugin.actions.method;

import com.intellij.debugger.engine.JavaDebugProcess;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.engine.events.SuspendContextCommandImpl;
import com.intellij.debugger.impl.DebuggerContextImpl;
import com.intellij.debugger.impl.DebuggerSession;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.UIUtil;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.impl.XDebugSessionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.domain.MethodWatchContext;
import com.runtime.pivot.plugin.test.XDebuggerTestUtil;
import com.runtime.pivot.plugin.utils.ProjectUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

/**
 * 并不需要回滚变量值
 * com.intellij.xdebugger.impl.actions.ResetFrameAction
 * JvmDropFrameActionHandler
 */
public class ExceptionRestoreAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }

}
