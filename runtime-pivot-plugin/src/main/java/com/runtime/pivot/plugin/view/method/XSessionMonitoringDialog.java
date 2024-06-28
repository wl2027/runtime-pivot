package com.runtime.pivot.plugin.view.method;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.StopWatch;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebugSessionListener;
import com.intellij.xdebugger.XSourcePosition;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.runtime.pivot.plugin.config.RuntimePivotConstants;
import com.runtime.pivot.plugin.model.XSessionComponent;
import com.runtime.pivot.plugin.model.XStackContext;
import com.runtime.pivot.plugin.service.RuntimePivotMethodService;
import com.runtime.pivot.plugin.utils.RuntimePivotUtil;
import org.jetbrains.annotations.Nullable;

public class XSessionMonitoringDialog extends XSessionComponent<XSessionMonitoringDialog> {
    private JTable table;
    private DefaultTableModel tableModel = new DefaultTableModel();
    private JTextArea textArea;
    private JButton clearButton;
    private Map<Integer, Object> rowDataMap  = new ConcurrentHashMap<>();

    private StopWatch stopWatch ;
    private String currentTaskName ;
    private final Map<StopWatch.TaskInfo,XSourcePosition> taskInfoXSourcePositionMap = new ConcurrentHashMap<>();

    protected XSessionMonitoringDialog(XDebugSession xDebugSession) {
        super(xDebugSession, RuntimePivotConstants.X_SESSION_MONITORING);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeComponent();
            }
        });
        table = new JBTable(tableModel);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    Object hiddenData = rowDataMap.get(selectedRow);
                    onRowSelected(selectedRow, hiddenData);
                }
            }
        });
        add(new JBScrollPane(table), BorderLayout.CENTER);
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("Clear", AllIcons.Actions.GC);
        clearButton.addActionListener(e -> initData(null));
        topPanel.add(clearButton);
        add(topPanel, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        textArea = new JBTextArea(3, 20);
        bottomPanel.add(new JBScrollPane(textArea), BorderLayout.CENTER);
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            closeComponent();
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(WindowManager.getInstance().getFrame(myProject));
        initData(XStackContext.getInstance(xDebugSession));
    }

    public static XSessionMonitoringDialog getInstance(XDebugSession xDebugSession) {
        XSessionMonitoringDialog xSessionMonitoringDialog = new XSessionMonitoringDialog(xDebugSession);
        xSessionMonitoringDialog.initData(XStackContext.getInstance(xDebugSession));
        return xSessionMonitoringDialog;
    }

    @Override
    public XDebugSessionListener getXDebugSessionListener() {
        return new XDebugSessionListener() {
            @Override
            public void beforeSessionResume() {
                XDebugSessionListener.super.beforeSessionResume();
                currentTaskName = getCurrentNextPositionName();
            }

            //进入暂停
            @Override
            public void sessionPaused() {
                if (stopWatch.isRunning()) {
                    stopWatch.stop();
                    updateMonitoringTableDialog(stopWatch);
                }
            }

            //恢复执行
            @Override
            public void sessionResumed() {
                stopWatch.start(currentTaskName);
            }

            private String getCurrentNextPositionName() {
                XSourcePosition currentPosition = myXDebugSession.getCurrentPosition();
                return RuntimePivotUtil.getNextPositionName(currentPosition);
            }

            synchronized private void updateMonitoringTableDialog(StopWatch stopWatch) {
                StopWatch.TaskInfo lastTaskInfo = stopWatch.getLastTaskInfo();
                taskInfoXSourcePositionMap.put(lastTaskInfo,myXDebugSession.getCurrentPosition());
                final long totalTimeNanos = stopWatch.getTotalTimeNanos();
                TimeUnit unit = chooseTimeUnit(totalTimeNanos);
                String shortSummary = stopWatch.shortSummary(unit);
                final NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMinimumIntegerDigits(9);
                nf.setGroupingUsed(false);
                final NumberFormat pf = NumberFormat.getPercentInstance();
                pf.setMinimumIntegerDigits(2);
                pf.setGroupingUsed(false);
                java.util.List<String[]> dataList = new ArrayList<>();
                List<XSourcePosition> xSourcePositions = new ArrayList<>();
                for (StopWatch.TaskInfo task : stopWatch.getTaskInfo()) {
                    dataList.add(new String[]{
                            nf.format(task.getTime(unit)),
                            pf.format((double) task.getTimeNanos() / totalTimeNanos),
                            "["+task.getTaskName()+","+ RuntimePivotUtil.getPositionName(taskInfoXSourcePositionMap.get(task))+"]"
                    });
                    XSourcePosition xSourcePosition = taskInfoXSourcePositionMap.get(task);
                    xSourcePositions.add(xSourcePosition);
                }
                String[] columnNames = new String[]{unit.name(),"%", "Task Intervals"};
                updateTextArea(shortSummary);
                updateTableData(columnNames,dataList,xSourcePositions);
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
        };
    }

    @Override
    public void initData(@Nullable XStackContext xStackContext) {
        stopWatch = new StopWatch(getTaskName());
        textArea.setText("No monitoring data");
        tableModel.setRowCount(0);
        rowDataMap.clear();
        currentTaskName = null;
        taskInfoXSourcePositionMap.clear();
    }

    @Deprecated
    @Override
    public void updateData(@Nullable XStackContext xStackContext) {
        //只存在监听器更新表格数据+更新文本
    }

    @Override
    public void removeXSessionComponent() {
        RuntimePivotMethodService.getInstance(myProject).removeXSessionMonitoringDialog(myXDebugSession);
    }

    @Override
    public void closeComponent() {
        super.closeComponent();
        dispose();
    }

    private String getTaskName() {
        return myXDebugSession.getSessionName() + "@" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
    }

    private void onRowSelected(int rowIndex, Object hiddenData) {
        XSourcePosition xSourcePosition  = (XSourcePosition) hiddenData;
        xSourcePosition.createNavigatable(myProject).navigate(true);
    }

    public void updateTableData(String[] newColumnNames, Object[][] newData, Object[] hiddenDataArray) {
        tableModel.setDataVector(newData, newColumnNames);
        rowDataMap.clear();
        for (int i = 0; i < newData.length; i++) {
            rowDataMap.put(i, hiddenDataArray[i]);
        }
    }

    public void updateTableData(String[] newColumnNames, java.util.List<String[]> newData, java.util.List<XSourcePosition> hiddenDataArray) {
        updateTableData(newColumnNames,(Object[][]) convertListToArray(newData), hiddenDataArray.toArray());
    }

    public static <T> Object convertListToArray(java.util.List<T> list) {
        if (list == null || list.isEmpty()) {
            return new Object[0];
        }
        Class<?> componentType = list.get(0).getClass();
        Object array = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    public void updateTextArea(String newText) {
        textArea.setText(newText);
    }
}