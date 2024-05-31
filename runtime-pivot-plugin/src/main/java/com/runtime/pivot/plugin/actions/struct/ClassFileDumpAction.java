package com.runtime.pivot.plugin.actions.struct;

import com.intellij.debugger.JavaDebuggerBundle;
import com.intellij.debugger.engine.JVMNameUtil;
import com.intellij.ide.util.TreeClassChooser;
import com.intellij.ide.util.TreeClassChooserFactory;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.test.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

public class ClassFileDumpAction extends XDebuggerTreeActionBase {
    @Override
    public void update(@NotNull AnActionEvent e) {
        //psi&变量&
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        e.getPresentation().setEnabled(node != null && isEnabled(node, e));
        /**
         * //启用
         * e.getPresentation().setEnabledAndVisible(true);
         * e.getPresentation().setEnabledAndVisible(false);
         */
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        e.getPresentation().setEnabled(psiElement != null && psiElement instanceof PsiClass);
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        //e.getData(CommonDataKeys.PSI_FILE)
        PsiClass psiClass = null;
        if (psiElement instanceof PsiClass){
            psiClass = (PsiClass) psiElement;
        }
        String qualifiedName = psiClass==null?null:psiClass.getQualifiedName();
        XValueNodeImpl node = getSelectedNode(e.getDataContext());
        String name = node==null?null:node.getName();

        Project project = e.getProject();
        String text = ActionExecutorUtil.buildCode(ActionType.Class.classFileDump,null,name,ActionExecutorUtil.buildStringObject(qualifiedName),ActionExecutorUtil.buildStringObject(e.getProject().getBasePath()));
        XDebugSession session = DebuggerUIUtil.getSession(e);
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
    }

    @Override
    protected void perform(XValueNodeImpl node, @NotNull String nodeName, AnActionEvent e) {

    }
}
