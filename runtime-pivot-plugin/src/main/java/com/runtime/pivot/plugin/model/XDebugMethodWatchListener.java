package com.runtime.pivot.plugin.model;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.test.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;

import java.util.Date;

/**
 * 维护一个map<ssion,xdebug> => 服务组件的概念=>颗粒度=>全局/项目/程序/会话
 */
public class XDebugMethodWatchListener implements XDebugSessionListener {
    private final StopWatch stopWatch ;
    private final XDebugSession xDebugSession ;

    public XDebugMethodWatchListener(String id, XDebugSession xDebugSession) {
        this.stopWatch = new StopWatch(id);
        this.xDebugSession = xDebugSession;
    }

    public StopWatch getStopWatch() {
        return stopWatch;
    }

    //进入暂停
    @Override
    public void sessionPaused() {
        System.out.println("XDebugSessionListener.super.sessionPaused();");
        //stop
        stopWatch.stop();
        String prettyPrint = stopWatch.prettyPrint();
        System.out.println(prettyPrint);
        String text = ActionExecutorUtil.buildCode(ActionType.Method.trackTime,null,ActionExecutorUtil.buildStringObject(prettyPrint));
        XStackFrame frame = xDebugSession.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, xDebugSession.getCurrentPosition());
    }

    //恢复执行
    @Override
    public void sessionResumed() {
        System.out.println("XDebugSessionListener.super.sessionResumed();");
        //start 点下一步先进来这
        stopWatch.start("test:"+ DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN));

    }

    //停止会话~清除
    @Override
    public void sessionStopped() {
        System.out.println("XDebugSessionListener.super.sessionStopped();");
    }

    //堆栈更改
    @Override
    public void stackFrameChanged() {
        System.out.println("XDebugSessionListener.super.stackFrameChanged();");
    }

    //在会话恢复之前
    @Override
    public void beforeSessionResume() {
        System.out.println("XDebugSessionListener.super.beforeSessionResume();");
    }

    //配置改变
    @Override
    public void settingsChanged() {
        System.out.println("XDebugSessionListener.super.settingsChanged();");
    }

    //断点静音
    @Override
    public void breakpointsMuted(boolean muted) {
        System.out.println("XDebugSessionListener.super.breakpointsMuted(muted);");
    }
}
