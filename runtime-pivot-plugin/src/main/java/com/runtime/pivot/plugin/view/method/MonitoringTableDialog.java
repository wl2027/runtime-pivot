package com.runtime.pivot.plugin.view.method;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.xdebugger.XDebugSession;
import com.runtime.pivot.plugin.listeners.XDebugMethodWatchListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.Vector;

public class MonitoringTableDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea textArea;
    private JButton startButton;
    private JButton stopButton;
    private JButton restartButton;
    private final Project project;
    private final XDebugSession xDebugSession;
    private XDebugMethodWatchListener xDebugMethodWatchListener;

    public MonitoringTableDialog(Project project,XDebugSession xDebugSession) {
        super(WindowManager.getInstance().getFrame(project), "Method Monitoring", false);
        this.project = project;
        this.xDebugSession = xDebugSession;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);

        // 添加窗口关闭事件
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // 创建表格模型和表格
//        String[] columnNames = {"Column 1", "Column 2", "Column 3"};
//        Object[][] data = {
//                {"Data 1", "Data 2", "Data 3"},
//                {"Data 4", "Data 5", "Data 6"},
//                {"Data 7", "Data 8", "Data 9"}
//        };
//        tableModel = new DefaultTableModel(data, columnNames);
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 创建顶部按钮面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startButton = new JButton("Start",com.intellij.icons.AllIcons.Actions.Execute);
        stopButton = new JButton("Stop", AllIcons.Actions.Suspend);
        restartButton = new JButton("Restart",com.intellij.icons.AllIcons.Actions.Restart);

        startButton.addActionListener(this::onStartButtonClicked);
        stopButton.addActionListener(this::onStopButtonClicked);
        restartButton.addActionListener(this::onRestartButtonClicked);

        stopButton.setEnabled(false);
        restartButton.setEnabled(false);

        topPanel.add(startButton);
        topPanel.add(stopButton);
        topPanel.add(restartButton);

        add(topPanel, BorderLayout.NORTH);

        // 创建底部文本区域和关闭按钮
        JPanel bottomPanel = new JPanel(new BorderLayout());

        textArea = new JTextArea(3, 20);
        textArea.setText("Initial text");
        bottomPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            onClose();
            dispose();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(WindowManager.getInstance().getFrame(project));
    }

    private void onStartButtonClicked(ActionEvent e) {
        xDebugMethodWatchListener = new XDebugMethodWatchListener(project,xDebugSession.getSessionName()+ "@"+DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN), xDebugSession);
        xDebugSession.addSessionListener(xDebugMethodWatchListener);
        System.out.println("Start button clicked");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        restartButton.setEnabled(true);
    }

    private void onStopButtonClicked(ActionEvent e) {
        xDebugSession.removeSessionListener(xDebugMethodWatchListener);
        System.out.println("Stop button clicked");
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        restartButton.setEnabled(false);
    }

    private void onRestartButtonClicked(ActionEvent e) {
        System.out.println("Restart button clicked");
        // Restart logic here, for now just simulating stop and start sequence
        //TODO 可以直接清口数据而不是重新监听
        onStopButtonClicked(e);
        onStartButtonClicked(e);
    }

    // 更新表头和表格数据的方法
    public void updateTableData(String[] newColumnNames, Object[][] newData) {
        tableModel.setDataVector(newData, newColumnNames);
    }
    public void updateTableData(String[] newColumnNames, java.util.List<String[]> newData) {
        tableModel.setDataVector((Object[][])convertListToArray(newData), newColumnNames);
    }

    public static <T> Object convertListToArray(java.util.List<T> list) {
        if (list == null || list.isEmpty()) {
            return new Object[0]; // 返回空数组
        }
        // 获取列表元素的类型
        Class<?> componentType = list.get(0).getClass();
        // 创建对应类型的数组
        Object array = Array.newInstance(componentType, list.size());
        // 将列表元素复制到数组中
        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, list.get(i));
        }
        return array;
    }

    // 更新文本区域的方法
    public void updateTextArea(String newText) {
        textArea.setText(newText);
    }

    // 关闭窗口和点击关闭按钮时执行的操作
    private void onClose() {
        System.out.println("Dialog is closing");
        // 在这里添加你需要执行的操作
    }
}
