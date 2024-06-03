package com.runtime.pivot.plugin.view;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RuntimePivotToolsWindow implements ToolWindowFactory {
    private static JLabel label;
    private static JTable table;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.getInstance();
        MethodMonitoringTable methodMonitoringTable = new MethodMonitoringTable();
        table = methodMonitoringTable.getTable();
        label = methodMonitoringTable.getLabel();
        Content content = contentFactory.createContent(methodMonitoringTable.getJScrollPane(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    static class MethodMonitoringTable {
        // Swing滑动窗口视图
        private final JScrollPane jScrollPane;
        private final JTable table;
        private final JLabel label;

        public static final String[] header = {"ns", "%", "Task Range"};

        public MethodMonitoringTable() {
            DefaultTableModel tableModel = new DefaultTableModel(null, header);
            this.table = new JTable();
            this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            this.table.setModel(tableModel);

            // 设置表格的首选大小
            this.table.setPreferredScrollableViewportSize(new Dimension(300, 150));

            this.label = new JLabel();
            this.label.setAlignmentX(JLabel.CENTER_ALIGNMENT);

            // 创建一个面板，将table和label添加到面板中
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

            // 将table的JScrollPane添加到panel中
            JScrollPane tableScrollPane = new JScrollPane(this.table);
            panel.add(tableScrollPane);

            // 添加一个分隔符
            panel.add(Box.createRigidArea(new Dimension(0, 10)));  // 添加一些垂直间距

            // 将label添加到panel中
            panel.add(this.label);

            // 将面板设置为JBScrollPane的视口组件
            this.jScrollPane = new JBScrollPane(panel);
            this.jScrollPane.setPreferredSize(new Dimension(300, 800));
        }

        public JScrollPane getJScrollPane() {
            return jScrollPane;
        }

        public JTable getTable() {
            return table;
        }

        public JLabel getLabel() {
            return label;
        }
    }

    @Deprecated
    public static void addRow(String info,String ... args) {
        if (table == null) {
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().clear();
        tableModel.addRow(args);
        label.setText(info);
    }

    public static void addData(String info, List<String[]> dataList) {
        if (table == null) {
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.getDataVector().clear();
        tableModel.setColumnIdentifiers(MethodMonitoringTable.header);
        if (dataList!=null){
            for (String[] args : dataList) {
                tableModel.addRow(args);
            }
        }
        label.setText(info);
    }

    public static JTable getTable() {
        return table;
    }

}
