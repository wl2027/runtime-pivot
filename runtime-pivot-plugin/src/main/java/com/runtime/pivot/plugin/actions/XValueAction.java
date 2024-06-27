package com.runtime.pivot.plugin.actions;

import com.intellij.debugger.engine.JavaValue;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.xdebugger.frame.XReferrersProvider;
import com.intellij.xdebugger.frame.XValue;
import com.intellij.xdebugger.impl.ui.tree.nodes.XEvaluationCallbackBase;
import com.runtime.pivot.plugin.model.RuntimeBaseAction;
import com.sun.jdi.Value;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class XValueAction extends RuntimeBaseAction {
    @Override
    protected boolean isEnable(AnActionEvent e) {
        return true;
    }

    @Override
    protected void action(AnActionEvent e) {

        getRuntimeContext().executeAttachCode("java.lang.Object a = new java.util.HashMap(){{put(\"aaa\",\"111\");put(\"bbb\",\"222\");}};", new XEvaluationCallbackBase() {
            @Override
            public void evaluated(@NotNull XValue result) {
                XReferrersProvider referrersProvider = result.getReferrersProvider();
            }

            @Override
            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
                System.out.println(errorMessage);
            }
        });

//        getRuntimeContext().executeAttachCode("String aaa = null;", new XEvaluationCallbackBase() {
//            //((JavaValue) result).getDescriptor().getValue()
//            @Override
//            public void evaluated(@NotNull XValue result) {
//                Value value = ((JavaValue) result).getDescriptor().getValue();
//                XReferrersProvider referrersProvider = result.getReferrersProvider();
//            }
//
//            @Override
//            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
//
//            }
//        });
//        getRuntimeContext().executeAttachCode("int a = 123;", new XEvaluationCallbackBase() {
//            @Override
//            public void evaluated(@NotNull XValue result) {
//                XReferrersProvider referrersProvider = result.getReferrersProvider();
//            }
//
//            @Override
//            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
//
//            }
//        });
//
//        getRuntimeContext().executeAttachCode("if (true) throw new NullPointerException();", new XEvaluationCallbackBase() {
//            @Override
//            public void evaluated(@NotNull XValue result) {
//                XReferrersProvider referrersProvider = result.getReferrersProvider();
//            }
//
//            @Override
//            public void errorOccurred(@NotNull @NlsContexts.DialogMessage String errorMessage) {
//                System.out.println(errorMessage);
//            }
//        });
    }
}
