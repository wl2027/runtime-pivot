//package com.runtime.pivot.plugin.actions;
//
//import com.intellij.xdebugger.XDebuggerUtil;
//import com.intellij.xdebugger.XExpression;
//import com.intellij.xdebugger.impl.DebuggerSupport;
//import com.intellij.xdebugger.impl.actions.DebuggerActionHandler;
//import com.intellij.xdebugger.impl.actions.XDebuggerActionBase;
//import org.jetbrains.annotations.NotNull;
//
//public class GetSize extends XDebuggerActionBase {
//    GetSize(){
//        super(true);
//    }
//
//    @Override
//    protected @NotNull DebuggerActionHandler getHandler(@NotNull DebuggerSupport debuggerSupport) {
//        XExpression expression = XDebuggerUtil.getInstance().createExpression(document.getText(), language, null, mode);
//        myInputComponent.getInputEditor().
//        DebuggerActionHandler evaluateHandler = debuggerSupport.getEvaluateHandler();
//        return evaluateHandler;
//    }
//}
