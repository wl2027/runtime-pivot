package com.runtime.pivot.plugin.view.method;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
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

public class MonitoringTableDialog extends JDialog {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextArea textArea;
    private JButton clearButton;
    private Map<Integer, Object> rowDataMap;
    private final Project project;
    private final XDebugSession xDebugSession;
    private final XDebugMethodWatchListener xDebugMethodWatchListener;

    public static MonitoringTableDialog getInstance(Project project, XDebugSession xDebugSession){
        return new MonitoringTableDialog(project, xDebugSession);
    }

    private MonitoringTableDialog(Project project, XDebugSession xDebugSession) {
        super(WindowManager.getInstance().getFrame(project), "Method Monitoring", false);
        this.project = project;
        this.xDebugSession = xDebugSession;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        xDebugMethodWatchListener = new XDebugMethodWatchListener(project,getTaskName(),xDebugSession);
        xDebugSession.addSessionListener(xDebugMethodWatchListener);
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
        rowDataMap = new ConcurrentHashMap<>();
//        for (int i = 0; i < data.length; i++) {
//            rowDataMap.put(i, "Hidden Data " + (i + 1));  // 初始化隐藏数据
//        }
//        tableModel = new DefaultTableModel(data, columnNames);
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 创建顶部按钮面板
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clearButton = new JButton("clear",AllIcons.Actions.ForceRefresh);
        clearButton.addActionListener(e -> onClearButtonClicked());
        topPanel.add(clearButton);
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

    private String getTaskName() {
        return xDebugSession.getSessionName()+ "@"+ DateUtil.format(new Date(), DatePattern.NORM_DATETIME_MS_PATTERN);
    }

    private void onClearButtonClicked() {
        System.out.println("Clear button clicked");
        tableModel.setRowCount(0);
        rowDataMap.clear();
        xDebugMethodWatchListener.clear(getTaskName());
        // 在这里添加清除表格内容时需要触发的其他事件
    }

    private void onRowSelected(int rowIndex, Object hiddenData) {
        XSourcePosition xSourcePosition  = (XSourcePosition) hiddenData;
        xSourcePosition.createNavigatable(project).navigate(true);
        System.out.println("Row " + rowIndex + " selected with hidden data: " + hiddenData);
        // 在这里处理行选择事件，并使用隐藏数据对象
    }

    // 更新表头和表格数据的方法
    public void updateTableData(String[] newColumnNames, Object[][] newData, Object[] hiddenDataArray) {
        tableModel.setDataVector(newData, newColumnNames);
        rowDataMap.clear();
        for (int i = 0; i < newData.length; i++) {
            rowDataMap.put(i, hiddenDataArray[i]);
        }
    }

    public void updateTableData(String[] newColumnNames, java.util.List<String[]> newData, java.util.List<XSourcePosition> hiddenDataArray) {
        tableModel.setDataVector((Object[][])convertListToArray(newData), newColumnNames);
        rowDataMap.clear();
        for (int i = 0; i < newData.size(); i++) {
            rowDataMap.put(i, hiddenDataArray.get(i));
        }
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
        xDebugSession.removeSessionListener(xDebugMethodWatchListener);
        System.out.println("Dialog is closing");
        // 在这里添加你需要执行的操作
    }
}
