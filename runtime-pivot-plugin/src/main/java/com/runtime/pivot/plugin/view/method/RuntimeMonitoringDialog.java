package com.runtime.pivot.plugin.view.method;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.table.JBTable;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XSourcePosition;
import com.runtime.pivot.plugin.listeners.XDebugMethodWatchListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Array;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;

public class RuntimeMonitoringDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea textArea;
    private JButton clearButton;
    private Map<Integer, Object> rowDataMap;
    private final Project project;
    private final XDebugSession xDebugSession;
    private final XDebugMethodWatchListener xDebugMethodWatchListener;

    public static RuntimeMonitoringDialog getInstance(Project project, XDebugSession xDebugSession){
        return new RuntimeMonitoringDialog(project, xDebugSession);
    }

    private RuntimeMonitoringDialog(Project project, XDebugSession xDebugSession) {
        super(WindowManager.getInstance().getFrame(project), "Method Monitoring", false);
        this.project = project;
        this.xDebugSession = xDebugSession;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        xDebugMethodWatchListener = new XDebugMethodWatchListener(project,getTaskName(),xDebugSession);
        xDebugSession.addSessionListener(xDebugMethodWatchListener);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        rowDataMap = new ConcurrentHashMap<>();
        tableModel = new DefaultTableModel();
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
        clearButton.addActionListener(e -> onClearButtonClicked());
        topPanel.add(clearButton);
        add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        textArea = new JBTextArea(3, 20);
        textArea.setText("No monitoring data");
        bottomPanel.add(new JBScrollPane(textArea), BorderLayout.CENTER);

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

    private String getTaskName() {
        return xDebugSession.getSessionName() + "@" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
    }

    public void onClearButtonClicked() {
        tableModel.setRowCount(0);
        rowDataMap.clear();
        textArea.setText("No monitoring data");
        xDebugMethodWatchListener.clear(getTaskName());
    }

    private void onRowSelected(int rowIndex, Object hiddenData) {
        XSourcePosition xSourcePosition  = (XSourcePosition) hiddenData;
        xSourcePosition.createNavigatable(project).navigate(true);
    }

    public void updateTableData(String[] newColumnNames, Object[][] newData, Object[] hiddenDataArray) {
        tableModel.setDataVector(newData, newColumnNames);
        rowDataMap.clear();
        for (int i = 0; i < newData.length; i++) {
            rowDataMap.put(i, hiddenDataArray[i]);
        }
    }

    public void updateTableData(String[] newColumnNames, java.util.List<String[]> newData, java.util.List<XSourcePosition> hiddenDataArray) {
        tableModel.setDataVector((Object[][]) convertListToArray(newData), newColumnNames);
        rowDataMap.clear();
        for (int i = 0; i < newData.size(); i++) {
            rowDataMap.put(i, hiddenDataArray.get(i));
        }
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

    private void onClose() {
        xDebugSession.removeSessionListener(xDebugMethodWatchListener);
    }
}

