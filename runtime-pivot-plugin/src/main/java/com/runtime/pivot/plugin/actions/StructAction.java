package com.runtime.pivot.plugin.actions;

import com.intellij.ide.util.TreeJavaClassChooserDialog;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;


/**
 * @see com.intellij.xdebugger.impl.breakpoints.ui.BreakpointsDialog$AddXBreakpointAction
 */
public abstract class StructAction extends RuntimeBaseAction {
    @Override
    final protected boolean isEnable(AnActionEvent e) {
        return true;
    }

    /**
     * 作用域
     *  - 项目 : 精确匹配+搜索框
     *  - 类文件 : 精确匹配
     *  - 对象 : 精确匹配
     * @param e
     */
    @Override
    final protected void action(AnActionEvent e) throws Exception {
        Project project = e.getProject();
        //是否作用在类文件上
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        PsiClass psiClass = null;
        if (psiElement!=null && psiElement instanceof PsiClass){
            psiClass = (PsiClass) psiElement;
        }
        if (psiClass != null) {
            executeAttachCode(null,psiClass.getQualifiedName(),project);
            return;
        }
        //是否作用在对象上
        XValueNodeImpl node = ObjectAction.getSelectedNode(e.getDataContext());
        String name = node==null?null:node.getName();
        if (name != null) {
            executeAttachCode(name,null,project);
            return;
        }
        //作用在项目上
        TreeJavaClassChooserDialog treeJavaClassChooserDialog = new TreeJavaClassChooserDialog("class file dump",project);
        treeJavaClassChooserDialog.show();
        PsiClass selected = treeJavaClassChooserDialog.getSelected();
        if (selected != null) {
            executeAttachCode(null,selected.getQualifiedName(),project);
        }
    }

    abstract protected void executeAttachCode(String object, String className, Project project);
}
