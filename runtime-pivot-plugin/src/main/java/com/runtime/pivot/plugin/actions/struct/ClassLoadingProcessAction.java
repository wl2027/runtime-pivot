package com.runtime.pivot.plugin.actions.struct;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.intellij.xdebugger.impl.ui.tree.actions.XDebuggerTreeActionBase;
import com.intellij.xdebugger.impl.ui.tree.nodes.XValueNodeImpl;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.actions.RuntimeBaseAction;
import com.runtime.pivot.plugin.utils.platfrom.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * 取对象 or 类?
 */
public class ClassLoadingProcessAction extends RuntimeBaseAction {
//    @Override
//    public void update(@NotNull AnActionEvent e) {
//        //psi&变量&
//        XValueNodeImpl node = getSelectedNode(e.getDataContext());
//        e.getPresentation().setEnabled(node != null && isEnabled(node, e));
//        /**
//         * //启用
//         * e.getPresentation().setEnabledAndVisible(true);
//         * e.getPresentation().setEnabledAndVisible(false);
//         */
//        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
//        e.getPresentation().setEnabled(psiElement != null && psiElement instanceof PsiClass);
//    }

    @Override
    protected boolean isEnable(AnActionEvent e) {
        return false;
    }

    @Override
    public void action(@NotNull AnActionEvent e) {
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
        String text = ActionExecutorUtil.buildCode(ActionType.Class.classLoadingProcess,null,name,ActionExecutorUtil.buildStringObject(qualifiedName));
        XDebugSession session = DebuggerUIUtil.getSession(e);
        XStackFrame frame = session.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, session.getCurrentPosition());
    }
}
