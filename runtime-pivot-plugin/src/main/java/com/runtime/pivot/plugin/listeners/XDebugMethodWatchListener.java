package com.runtime.pivot.plugin.listeners;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;
import com.runtime.pivot.plugin.service.XDebugMethodContext;
import com.runtime.pivot.plugin.view.RuntimePivotToolsWindow;
import com.runtime.pivot.plugin.view.method.MonitoringTableDialog;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 维护一个map<ssion,xdebug> => 服务组件的概念=>颗粒度=>全局/项目/程序/会话
 */
public class XDebugMethodWatchListener implements XDebugSessionListener {
    private final StopWatch stopWatch ;
    private String currentTaskName ;
    //TODO 关闭时需要清除
    private final Map<StopWatch.TaskInfo,XSourcePosition> taskInfoXSourcePositionMap = new ConcurrentHashMap<>();
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

    @Override
    public void beforeSessionResume() {
        XDebugSessionListener.super.beforeSessionResume();
        currentTaskName = getCurrentPositionName();
    }

    //进入暂停
    @Override
    public void sessionPaused() {
        //stop
        stopWatch.stop();
//        prettyPrint(stopWatch);
        updateMonitoringTableDialog(stopWatch);
//        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("RuntimePivotWindow");
//        toolWindow.show();
        //toolWindow.activate(()->{},true,true);
    }

    private void updateMonitoringTableDialog(StopWatch stopWatch) {
        StopWatch.TaskInfo lastTaskInfo = stopWatch.getLastTaskInfo();
        taskInfoXSourcePositionMap.put(lastTaskInfo,xDebugSession.getCurrentPosition());
        final long totalTimeNanos = stopWatch.getTotalTimeNanos();
        TimeUnit unit = chooseTimeUnit(totalTimeNanos);
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
                    pf.format((double) task.getTimeNanos() / totalTimeNanos),
                    "["+task.getTaskName()+","+getPositionName(taskInfoXSourcePositionMap.get(task))+"]"
            });
        }

        MonitoringTableDialog monitoringTableDialog = XDebugMethodContext.getInstance(project).getSessionMonitoringTableMap().get(xDebugSession);
        monitoringTableDialog.updateTextArea(shortSummary);
        String[] columnNames = new String[]{unit.name(),"%", "Task Intervals"};
        monitoringTableDialog.updateTableData(columnNames,dataList);
    }

    private static TimeUnit chooseTimeUnit(long totalTimeNanos) {
        if (totalTimeNanos < TimeUnit.MILLISECONDS.toNanos(1)) {
            // 当总时间小于1毫秒时，使用纳秒作为时间单位
            return TimeUnit.NANOSECONDS;
        } else if (totalTimeNanos < TimeUnit.SECONDS.toNanos(1)) {
            // 当总时间小于1秒时，使用毫秒作为时间单位
            return TimeUnit.MILLISECONDS;
        } else if (totalTimeNanos < TimeUnit.MINUTES.toNanos(1)) {
            // 当总时间小于1分钟时，使用秒作为时间单位
            return TimeUnit.SECONDS;
        } else {
            // 当总时间大于或等于1分钟时，使用分钟作为时间单位
            return TimeUnit.MINUTES;
        }
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
        //TODO 读取标记
        //start 点下一步先进来这
        //TODO 恢复执行是没有当前栈帧的
        stopWatch.start(currentTaskName);
    }

    private String getCurrentPositionName() {
        XSourcePosition currentPosition = xDebugSession.getCurrentPosition();
        return getPositionName(currentPosition);
    }
    private String getPositionName(@NotNull XSourcePosition currentPosition) {
        return currentPosition==null?"":currentPosition.getFile().getName() + ":" + currentPosition.getLine();
    }

}
