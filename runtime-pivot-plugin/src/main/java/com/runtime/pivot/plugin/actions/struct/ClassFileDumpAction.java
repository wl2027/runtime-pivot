package com.runtime.pivot.plugin.actions.struct;

import cn.hutool.core.collection.ListUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.ObjectAction;
import com.runtime.pivot.plugin.actions.StructAction;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class ClassFileDumpAction extends StructAction {

    /**
     * 作用域
     *  - 项目 : 模糊匹配+搜索框
     *  - 类文件 : 模糊匹配
     *  - 对象 : 模糊匹配
     * @param e
     */
    @Override
    public void action(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        //e.getData(CommonDataKeys.PSI_FILE)
        PsiClass psiClass = null;
        if (psiElement instanceof PsiClass){
            psiClass = (PsiClass) psiElement;
        }




        String qualifiedName = psiClass==null?null:psiClass.getQualifiedName();
        XValueNodeImpl node = ObjectAction.getSelectedNode(e.getDataContext());
        String name = node==null?null:node.getName();
        String code = ActionExecutorUtil.buildCode(ActionType.Class.classFileDump,null,name,ActionExecutorUtil.buildStringObject(qualifiedName),ActionExecutorUtil.buildStringObject(e.getProject().getBasePath()));
        getRuntimeContext().executeAttachCode(code, new XEvaluationCallbackBase() {
            @Override
            public void evaluated(@NotNull XValue result) {
                VirtualFile baseDir = ProjectUtil.guessProjectDir(e.getProject());
                VirtualFile child = baseDir.findChild(".runtime");
                com.intellij.ide.actions.SynchronizeCurrentFileAction.synchronizeFiles(ListUtil.of(child),e.getProject(),false);
                child.getFileSystem().refresh(false);
            }

            @Override
            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {

            }
        });

    }
}
