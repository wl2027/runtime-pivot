package com.runtime.pivot.plugin.model;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.io.FileUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.impl.breakpoints.XExpressionImpl;
import com.intellij.xdebugger.impl.ui.DebuggerUIUtil;
import com.runtime.pivot.agent.model.ActionType;
import com.runtime.pivot.plugin.test.XTestEvaluationCallback;
import com.runtime.pivot.plugin.utils.ActionExecutorUtil;
import com.runtime.pivot.plugin.view.RuntimePivotToolsWindow;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 维护一个map<ssion,xdebug> => 服务组件的概念=>颗粒度=>全局/项目/程序/会话
 */
public class XDebugMethodWatchListener implements XDebugSessionListener {
    private final StopWatch stopWatch ;
    private final XDebugSession xDebugSession ;
    private final Project project ;

    public XDebugMethodWatchListener(Project project, String id, XDebugSession xDebugSession) {
        this.stopWatch = new StopWatch(id);
        this.xDebugSession = xDebugSession;
        this.project = project;
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
        prettyPrint(stopWatch);
//        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("RuntimePivotWindow");
//        toolWindow.show();
        //toolWindow.activate(()->{},true,true);

        /**===============20240603-更换成窗口====================
        String prettyPrint = stopWatch.prettyPrint();
        System.out.println(prettyPrint);
        String text = ActionExecutorUtil.buildCode(ActionType.Method.trackTime,null,ActionExecutorUtil.buildStringObject(prettyPrint));
        XStackFrame frame = xDebugSession.getCurrentStackFrame();
        XDebuggerEvaluator evaluator = frame.getEvaluator();
        XTestEvaluationCallback callback = new XTestEvaluationCallback();
        XExpressionImpl xExpression = XExpressionImpl.fromText(text);
//        XExpressionImpl xExpression = XExpressionImpl.fromText(text, EvaluationMode.CODE_FRAGMENT);
        evaluator.evaluate(xExpression, callback, xDebugSession.getCurrentPosition());
         **/
    }

    private void prettyPrint(StopWatch stopWatch) {
        TimeUnit unit = null;
        if (null == unit) {
            unit = TimeUnit.NANOSECONDS;
        }
        String shortSummary = stopWatch.shortSummary(unit);
        final NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(9);
        nf.setGroupingUsed(false);
        final NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(2);
        pf.setGroupingUsed(false);
        List<String[]> dataList = new ArrayList<>();
        for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
            dataList.add(new String[]{
                    nf.format(task.getTime(unit)),
                    pf.format((double) task.getTimeNanos() / stopWatch.getTotalTimeNanos()),
                    task.getTaskName()
            });
        }
        RuntimePivotToolsWindow.addData(shortSummary,dataList);
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
