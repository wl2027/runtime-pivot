package com.runtime.pivot.plugin.actions.method;

import com.intellij.debugger.JavaDebuggerBundle;
import com.intellij.debugger.engine.JVMNameUtil;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.lang.jvm.util.JvmClassUtil;
import com.intellij.lang.jvm.util.JvmUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.plugin.model.XDebugMethodWatchListener;
import com.runtime.pivot.plugin.service.XDebugMethodContext;
import org.jetbrains.annotations.NotNull;

public class MethodStopWatchAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        XDebugSession session = DebuggerUIUtil.getSession(e);
        XDebugMethodWatchListener xDebugMethodWatchListener = XDebugMethodContext.getInstance(e.getProject()).getSessionListenerMap().get(session);
        session.removeSessionListener(xDebugMethodWatchListener);
    }
}
